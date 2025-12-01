package com.example.firebasenotes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.firebasenotes.dataStore.getOnboardingSeen
import com.example.firebasenotes.navigation.NavManager
import com.example.firebasenotes.onBoarding.OnboardingScreen
import com.example.firebasenotes.ui.theme.FirebaseNotesTheme
import com.example.firebasenotes.viewModels.LoginViewModel
import com.example.firebasenotes.viewModels.NotesViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val loginVM: LoginViewModel by viewModels()
        val notesVM: NotesViewModel by viewModels()

        // 💥 PROCESAR EL LINK DE VERIFICACIÓN SI VIENE DEL HOSTING
        handleIncomingVerificationLink()

        setContent {
            FirebaseNotesTheme {
                val context = LocalContext.current
                val seen by getOnboardingSeen(context).collectAsState(initial = false)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (seen) {
                        NavManager(loginVM, notesVM)
                    } else {
                        OnboardingScreen()
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------------------------
    // ⭐ RECIBE EL DEEP LINK: myapp://verifyEmail?oobCode=XXXX
    // ------------------------------------------------------------------------------
    private fun handleIncomingVerificationLink() {
        val data = intent?.data
        if (data != null) {
            val oobCode = data.getQueryParameter("oobCode")

            if (oobCode != null) {
                verifyEmail(oobCode)
            }
        }
    }

    // ------------------------------------------------------------------------------
    // ⭐ VERIFICA EL CORREO EN FIREBASE
    // ------------------------------------------------------------------------------
    private fun verifyEmail(oobCode: String) {
        FirebaseAuth.getInstance().applyActionCode(oobCode)
            .addOnSuccessListener {
                Toast.makeText(this, "Correo verificado correctamente", Toast.LENGTH_LONG).show()

                // 🔥 Reiniciar la app y mandar al Login
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("openLogin", true)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error verificando correo", Toast.LENGTH_LONG).show()
            }
    }
}
