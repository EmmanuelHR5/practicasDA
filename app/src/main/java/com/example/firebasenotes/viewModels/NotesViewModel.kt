package com.example.firebasenotes.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasenotes.model.NotesState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NotesViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = com.google.firebase.Firebase.firestore

    private val _notesData = MutableStateFlow<List<NotesState>>(emptyList())
    val notesData: StateFlow<List<NotesState>> = _notesData

    var state by mutableStateOf(NotesState())
        private set

    // ===== AUTOSAVE =====
    private var autoSaveJob: Job? = null
    private var currentEditingId: String? = null

    var isSaving by mutableStateOf(false)
        private set

    fun onValue(value: String, text: String) {
        when (text) {
            "title" -> state = state.copy(title = value)
            "note" -> state = state.copy(note = value)
        }
    }

    fun setColor(colorHex: String) {
        state = state.copy(colorHex = colorHex)
    }

    fun toggleFavorite() {
        val docId = state.idDoc
        val newValue = !state.favorite

        // Actualiza el estado local
        state = state.copy(favorite = newValue)

        // Actualiza Firestore inmediatamente
        if (docId.isNotEmpty()) {
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
    }

    fun fetchNotes() {
        val email = auth.currentUser?.email
        firestore.collection("Notes")
            .whereEqualTo("emailUser", email.toString())
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    Log.e("FETCH_NOTES", "Error al obtener notas: ${error.localizedMessage}")
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
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val email = auth.currentUser?.email ?: ""

        Log.d("SAVE_NOTE", "Iniciando guardado... emailUser=$email")

        val idDoc = firestore.collection("Notes").document().id

        Log.d("SAVE_NOTE", "ID generado: $idDoc")

        val newNote = mapOf(
            "idDoc" to idDoc,
            "title" to title,
            "note" to note,
            "date" to formatDate(),
            "emailUser" to email,
            "isFavorite" to isFavorite,
            "colorHex" to colorHex
        )

        firestore.collection("Notes")
            .document(idDoc)
            .set(newNote)
            .addOnSuccessListener {
                Log.d("SAVE_NOTE", "Firestore respondió SUCCESS")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("SAVE_NOTE", "Error Firestore: ${e.localizedMessage}")
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
                        date = note?.date ?: ""
                    )
                }
            }
    }

    fun updateNote(idDoc: String, onSuccess: () -> Unit) {
        isSaving = true
        firestore.collection("Notes").document(idDoc)
            .update(
                mapOf(
                    "title" to state.title,
                    "note" to state.note,
                    "isFavorite" to state.favorite,
                    "colorHex" to state.colorHex
                )
            )
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

    // ===== AUTOSAVE API =====

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
                        "colorHex" to state.colorHex
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
}
