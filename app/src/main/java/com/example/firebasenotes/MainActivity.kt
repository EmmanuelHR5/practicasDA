package com.example.firebasenotes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.firebasenotes.dataStore.getOnboardingSeen
import com.example.firebasenotes.navigation.NavManager
import com.example.firebasenotes.onBoarding.OnboardingScreen
import com.example.firebasenotes.ui.theme.FirebaseNotesTheme
import com.example.firebasenotes.viewModels.LoginViewModel
import com.example.firebasenotes.viewModels.NotesViewModel
import com.example.firebasenotes.viewModels.ThemeViewModel
import com.google.firebase.auth.FirebaseAuth


class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val loginVM: LoginViewModel by viewModels()
        val notesVM: NotesViewModel by viewModels()
        val themeVM: ThemeViewModel by viewModels()

        handleIncomingVerificationLink()

        setContent {

            val isDark by themeVM.isDarkMode.observeAsState(initial = false)

            FirebaseNotesTheme(darkTheme = isDark) {

                val context = LocalContext.current
                val seen by getOnboardingSeen(context).collectAsState(initial = false)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    NavManager(
                        loginVM = loginVM,
                        notesVM = notesVM,
                        themeVM = themeVM,
                        startInSplash = true, // 👈 IMPORTANTE
                        onboardingSeen = seen
                    )
                }
            }
        }
    }

    private fun handleIncomingVerificationLink() {
        val data = intent?.data
        if (data != null) {
            val oobCode = data.getQueryParameter("oobCode")
            if (oobCode != null) verifyEmail(oobCode)
        }
    }

    private fun verifyEmail(oobCode: String) {
        FirebaseAuth.getInstance().applyActionCode(oobCode)
            .addOnSuccessListener {
                Toast.makeText(this, "Correo verificado correctamente", Toast.LENGTH_LONG).show()
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
