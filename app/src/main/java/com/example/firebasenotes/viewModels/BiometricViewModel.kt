package com.example.firebasenotes.viewModels

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BiometricViewModel : ViewModel() {

    val authSuccess = MutableLiveData<Boolean>()

    fun authenticate(activity: FragmentActivity) {

        val executor = ContextCompat.getMainExecutor(activity)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Desbloquear categoría")
            .setSubtitle("Usa tu huella o PIN")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    authSuccess.value = true
                }

                override fun onAuthenticationFailed() {
                    authSuccess.value = false
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    authSuccess.value = false
                }
            }
        )

        biometricPrompt.authenticate(promptInfo)
    }
}
