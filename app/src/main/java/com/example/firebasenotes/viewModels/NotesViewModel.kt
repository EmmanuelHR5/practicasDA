package com.example.firebasenotes.viewModels

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasenotes.model.NotesState
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class NotesViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = com.google.firebase.Firebase.firestore

    // Lista de notas
    private val _notesData = MutableStateFlow<List<NotesState>>(emptyList())
    val notesData: StateFlow<List<NotesState>> = _notesData

    // Estado de la nota que se está editando
    var state by mutableStateOf(NotesState())
        private set

    // ===== AUTOSAVE =====
    private var autoSaveJob: Job? = null
    private var currentEditingId: String? = null
    var isSaving by mutableStateOf(false)
        private set

    // ===== SMS AUTH =====
    private var storedVerificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    // ========================
    //       NOTAS
    // ========================
    fun onValue(value: String, field: String) {
        when (field) {
            "title" -> state = state.copy(title = value)
            "note" -> state = state.copy(note = value)
            "category" -> state = state.copy(category = value)
        }
    }

    fun setColor(colorHex: String) {
        state = state.copy(colorHex = colorHex)
    }

    fun toggleFavorite() {
        val docId = state.idDoc
        val newValue = !state.favorite

        // Actualiza la UI
        state = state.copy(favorite = newValue)

        if (docId.isEmpty()) return

        firestore.collection("Notes")
            .document(docId)
            .update("isFavorite", newValue)
            .addOnSuccessListener {
                Log.d("FAVORITE", "Favorito actualizado: $newValue")
            }
            .addOnFailureListener {
                Log.e("FAVORITE", "Error al actualizar favorito: ${it.localizedMessage}")
            }
    }

    fun toggleFavoriteById(idDoc: String) {
        firestore.collection("Notes")
            .document(idDoc)
            .get()
            .addOnSuccessListener { snap ->
                val current = snap.getBoolean("isFavorite") ?: false
                val newValue = !current
                firestore.collection("Notes")
                    .document(idDoc)
                    .update("isFavorite", newValue)
            }
    }

    fun fetchNotes() {
        val email = auth.currentUser?.email ?: return

        firestore.collection("Notes")
            .whereEqualTo("emailUser", email)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    Log.e("FETCH_NOTES", "Error: ${error.localizedMessage}")
                    return@addSnapshotListener
                }

                val documents = mutableListOf<NotesState>()
                if (querySnapshot != null) {
                    for (document in querySnapshot) {
                        val myDocument =
                            document.toObject(NotesState::class.java).copy(idDoc = document.id)
                        documents.add(myDocument)
                    }
                }
                _notesData.value = documents
            }
    }

    fun saveNewNote(
        title: String,
        note: String,
        colorHex: String,
        isFavorite: Boolean,
        category: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val email = auth.currentUser?.email ?: ""

        val idDoc = firestore.collection("Notes").document().id

        val newNote = mapOf(
            "idDoc" to idDoc,
            "title" to title,
            "note" to note,
            "date" to formatDate(),
            "emailUser" to email,
            "isFavorite" to isFavorite,
            "colorHex" to colorHex,
            "category" to category
        )

        firestore.collection("Notes")
            .document(idDoc)
            .set(newNote)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onFailure(e.localizedMessage ?: "Error desconocido")
            }
    }

    private fun formatDate(): String {
        val currentDate: Date = Calendar.getInstance().time
        val res = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return res.format(currentDate)
    }

    fun getNoteById(documentId: String) {
        firestore.collection("Notes")
            .document(documentId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val note = snapshot.toObject(NotesState::class.java)
                    state = state.copy(
                        idDoc = documentId,
                        title = note?.title ?: "",
                        note = note?.note ?: "",
                        favorite = note?.favorite ?: false,
                        colorHex = note?.colorHex ?: "#FFFFFF",
                        emailUser = note?.emailUser ?: "",
                        date = note?.date ?: "",
                        category = note?.category ?: "General",
                        phone = note?.phone ?: ""
                    )
                }
            }
    }

    fun updateNote(idDoc: String, onSuccess: () -> Unit) {
        isSaving = true
        val data = mapOf(
            "title" to state.title,
            "note" to state.note,
            "isFavorite" to state.favorite,
            "colorHex" to state.colorHex,
            "category" to state.category
        )

        firestore.collection("Notes").document(idDoc)
            .update(data)
            .addOnSuccessListener {
                isSaving = false
                onSuccess()
            }
            .addOnFailureListener {
                isSaving = false
            }
    }

    fun deleteNote(idDoc: String, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                firestore.collection("Notes").document(idDoc)
                    .delete()
                    .addOnSuccessListener {
                        onSuccess()
                    }
            } catch (e: Exception) {
                Log.d("ERROR DELETE", "Error al eliminar ${e.localizedMessage} ")
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }

    // ========================
    //       AUTOSAVE
    // ========================
    fun startAutoSave(idDoc: String) {
        currentEditingId = idDoc
        autoSaveJob?.cancel()

        autoSaveJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                while (isActive) {
                    delay(3000L) // cada 3 segundos

                    val docId = currentEditingId ?: continue
                    val editNote = hashMapOf(
                        "title" to state.title,
                        "note" to state.note,
                        "isFavorite" to state.favorite,
                        "colorHex" to state.colorHex,
                        "category" to state.category
                    )

                    isSaving = true

                    firestore.collection("Notes")
                        .document(docId)
                        .update(editNote as Map<String, Any>)
                        .addOnSuccessListener {
                            isSaving = false
                        }
                        .addOnFailureListener {
                            isSaving = false
                        }
                }
            } catch (e: Exception) {
                Log.d("ERROR AUTOSAVE", "Exception autosave ${e.localizedMessage}")
            }
        }
    }

    fun stopAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = null
        currentEditingId = null
    }

    // ========================
    //       EXPORTAR PDF
    // ========================
    fun exportNoteAsPdfToUri(
        context: Context,
        uri: android.net.Uri,
        title: String,
        content: String
    ) {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 aprox
            val page = pdfDocument.startPage(pageInfo)

            val canvas = page.canvas
            val paint = Paint().apply { textSize = 14f }

            var y = 40f
            canvas.drawText(title.ifBlank { "Nota" }, 20f, y, paint)
            y += 30f

            val lines = content.split("\n")
            for (line in lines) {
                canvas.drawText(line, 20f, y, paint)
                y += 20f
            }

            pdfDocument.finishPage(page)

            context.contentResolver.openOutputStream(uri)?.use { out ->
                pdfDocument.writeTo(out)
            }

            pdfDocument.close()

            Toast.makeText(context, "PDF guardado correctamente", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error al exportar PDF", Toast.LENGTH_SHORT).show()
            Log.e("PDF", "Error: ${e.localizedMessage}")
        }
    }

    // ========================
//    SMS / PHONE AUTH
// ========================
    fun formatPhoneNumber(raw: String): String {
        val clean = raw.filter { it.isDigit() }
        return if (clean.length == 10) "+52$clean" else clean
    }

    fun sendSmsVerification(phone: String, activity: Activity) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.d("SMS", "Auto-verificado: ${credential.smsCode}")
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e("SMS", "Error: ${e.localizedMessage}")
                    Toast.makeText(activity, "Error enviando SMS", Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    storedVerificationId = verificationId
                    resendToken = token
                    Toast.makeText(activity, "Código enviado", Toast.LENGTH_SHORT).show()
                }

            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifySmsCode(activity: Activity, code: String) {
        val verificationId = storedVerificationId ?: return

        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        val user = auth.currentUser ?: return

        // Vincula el número al usuario
        user.linkWithCredential(credential)
            .addOnSuccessListener {
                val phone = user.phoneNumber ?: ""
                savePhoneToFirestore(phone)
                Toast.makeText(activity, "Número verificado ✓", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Código incorrecto", Toast.LENGTH_SHORT).show()
            }
    }

    private fun savePhoneToFirestore(phone: String) {
        val email = auth.currentUser?.email ?: return

        firestore.collection("Users")
            .document(email)
            .set(mapOf("phone" to phone), SetOptions.merge())
    }

    fun unlinkPhone(activity: Activity) {
        val user = auth.currentUser ?: return

        user.unlink(PhoneAuthProvider.PROVIDER_ID)
            .addOnSuccessListener {
                removePhoneFromFirestore()
                Toast.makeText(activity, "Número desvinculado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(activity, "No se pudo desvincular", Toast.LENGTH_SHORT).show()
            }
    }

    private fun removePhoneFromFirestore() {
        val email = auth.currentUser?.email ?: return

        firestore.collection("Users")
            .document(email)
            .update(mapOf("phone" to com.google.firebase.firestore.FieldValue.delete()))
    }

    //PAPELERA
    // Enviar nota a la papelera (borrado lógico)
    fun moveToTrash(idDoc: String, onSuccess: () -> Unit = {}) {
        firestore.collection("Notes")
            .document(idDoc)
            .update("isDeleted", true)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                Log.e("TRASH", "Error al enviar a papelera: ${e.localizedMessage}")
            }
    }

    // Restaurar desde papelera
    fun restoreNote(idDoc: String, onSuccess: () -> Unit = {}) {
        firestore.collection("Notes")
            .document(idDoc)
            .update("isDeleted", false)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                Log.e("TRASH", "Error al restaurar: ${e.localizedMessage}")
            }
    }

    // Eliminar definitivamente de Firestore
    fun deleteNoteForever(idDoc: String, onSuccess: () -> Unit = {}) {
        firestore.collection("Notes")
            .document(idDoc)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                Log.e("TRASH", "Error al borrar definitivo: ${e.localizedMessage}")
            }
    }




}
