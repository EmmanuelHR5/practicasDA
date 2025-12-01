package com.example.firebasenotes.viewModels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RecuperarPasswordViewModel : ViewModel() {

    var isEmailSent = mutableStateOf(false)
    var showError = mutableStateOf(false)

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            try {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).await()
                isEmailSent.value = true

            } catch (e: Exception) {
                Log.d("RESET", "Error: ${e.localizedMessage}")
                showError.value = true
            }
        }
    }
}
