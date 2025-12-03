package com.example.firebasenotes.views

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.firebasenotes.viewModels.NotesViewModel

@Composable
fun AccountView(
    navController: NavController,
    notesVM: NotesViewModel
) {

    var phone by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }

    val context = LocalContext.current
    val activity = context as Activity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp)
    ) {

        Text("Mi Cuenta", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))

        // ==== INGRESO DE TELÉFONO ====
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it.filter { c -> c.isDigit() } },
            label = { Text("Número de teléfono") },
            placeholder = { Text("5512345678") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = {
                val formatted = notesVM.formatPhoneNumber(phone)

                if (formatted.length != 13) {
                    Toast.makeText(context, "Número inválido", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                notesVM.sendSmsVerification(formatted, activity)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar SMS de verificación")
        }

        Spacer(Modifier.height(30.dp))

        // ==== CÓDIGO SMS ====
        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            label = { Text("Código recibido") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = {
                notesVM.verifySmsCode(activity, code)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Verificar Código")
        }

        Spacer(Modifier.height(20.dp))

        // ==== DESVINCULAR ====
        var showUnlinkDialog by remember { mutableStateOf(false) }

        if (showUnlinkDialog) {
            AlertDialog(
                onDismissRequest = { showUnlinkDialog = false },
                title = { Text("Quitar número de teléfono") },
                text = { Text("¿Seguro que deseas desvincular tu número? Ya no podrás hacer inicio de sesión por SMS.") },
                confirmButton = {
                    TextButton(onClick = {
                        notesVM.unlinkPhone(activity)
                        showUnlinkDialog = false
                    }) {
                        Text("Desvincular", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showUnlinkDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        Button(
            onClick = { showUnlinkDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Desvincular Número")
        }


        Spacer(Modifier.height(30.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}
