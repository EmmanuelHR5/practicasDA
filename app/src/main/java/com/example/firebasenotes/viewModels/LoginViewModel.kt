package com.example.firebasenotes.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasenotes.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore

    var showAlert by mutableStateOf(false)

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                onSuccess()

            } catch (e: Exception) {
                Log.d("ERROR LOGIN", "Error: ${e.localizedMessage}")
                showAlert = true
            }
        }
    }

    fun createUser(email: String, password: String, username: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()

                // 🔥 ENVÍA EMAIL DE VERIFICACIÓN
                auth.currentUser?.sendEmailVerification()?.await()

                saveUser(username)
                onSuccess()

            } catch (e: Exception) {
                Log.d("ERROR CREAR", "Error: ${e.localizedMessage}")
                showAlert = true
            }
        }
    }

    private fun saveUser(username: String) {
        val userId = auth.currentUser?.uid
        val email = auth.currentUser?.email

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = UserModel(
                    userId = userId ?: "",
                    email = email ?: "",
                    username = username
                )

                firestore.collection("Users")
                    .document(userId ?: "sin_id")
                    .set(user)
                    .await()

                Log.d("FIRESTORE", "Usuario guardado correctamente")

            } catch (e: Exception) {
                Log.d("FIRESTORE", "Error al guardar: ${e.localizedMessage}")
            }
        }
    }

    fun closeAlert() {
        showAlert = false
    }
}
