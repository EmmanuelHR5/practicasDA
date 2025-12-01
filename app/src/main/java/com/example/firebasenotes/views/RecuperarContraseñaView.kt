package com.example.firebasenotes.views

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.firebasenotes.viewModels.RecuperarPasswordViewModel

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RecuperarContrasenaView(
    navController: NavController,
    vm: RecuperarPasswordViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    // --- Estados ---
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // --- Efecto de éxito ---
    LaunchedEffect(vm.isEmailSent.value) {
        if (vm.isEmailSent.value) {
            isLoading = false
            Toast.makeText(context, "Correo enviado correctamente", Toast.LENGTH_LONG).show()
            navController.popBackStack()
        }
    }

    // --- Efecto error ---
    LaunchedEffect(vm.showError.value) {
        if (vm.showError.value) {
            isLoading = false
            Toast.makeText(context, "Error al enviar correo", Toast.LENGTH_LONG).show()
        }
    }

    // --- Fondo limpio ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = "Recuperar Contraseña",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(25.dp))

            // --- TARJETA ELEVADA ---
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .shadow(10.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Ingresa tu correo para enviarte un enlace de recuperación",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo registrado") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // --- BOTÓN CON GRADIENTE ---
                    GradientButton(
                        text = if (isLoading) "Enviando..." else "Enviar correo",
                        enabled = !isLoading,
                        onClick = {
                            isLoading = true
                            vm.sendPasswordReset(email)
                        }
                    )
                }
            }
        }

        // --- LOADER ELEGANTE ---
        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp)
            )
        }
    }
}

// ---------------------------
// BOTÓN CON GRADIENTE
// ---------------------------
@Composable
fun GradientButton(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {

    val gradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF1E88E5), // azul primario
            Color(0xFF1565C0)  // azul más oscuro
        )
    )

    Button(
        onClick = { if (enabled) onClick() },
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
