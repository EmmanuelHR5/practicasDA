package com.example.firebasenotes.views

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.firebasenotes.viewModels.NotesViewModel

@Composable
fun AccountView(
    navController: NavController,
    notesVM: NotesViewModel
) {

    var phone by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var showUnlinkDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as Activity

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1565C0), Color(0xFF1E88E5))
                )
            )
            .padding(20.dp)
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                "Mi Cuenta",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        "Verificación por SMS",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 15.dp)
                    )

                    // === TELÉFONO ===
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it.filter { c -> c.isDigit() } },
                        label = { Text("Número de teléfono", color = Color.Black) },
                        placeholder = { Text("5512345678", color = Color.Gray) },
                        textStyle = LocalTextStyle.current.copy(color = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val formatted = notesVM.formatPhoneNumber(phone)

                            if (formatted.length != 13) {
                                Toast.makeText(context, "Número inválido", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            notesVM.sendSmsVerification(formatted, activity)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFBBDEFB),
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Enviar SMS de verificación")
                    }

                    Spacer(Modifier.height(25.dp))

                    // === CÓDIGO SMS ===
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("Código recibido", color = Color.Black) },
                        textStyle = LocalTextStyle.current.copy(color = Color.Black),
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("123456", color = Color.Gray) }
                    )

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = { notesVM.verifySmsCode(activity, code) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFBBDEFB),
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Verificar Código")
                    }

                    Spacer(Modifier.height(25.dp))

                    Divider(thickness = 1.dp, color = Color.LightGray)

                    Spacer(Modifier.height(25.dp))

                    // === DESVINCULAR ===
                    Button(
                        onClick = { showUnlinkDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Desvincular Número", color = Color.White)
                    }

                }
            }

            Spacer(Modifier.height(25.dp))

            // === BOTÓN VOLVER ===
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text("Volver")
            }
        }
    }

    // ------------------------------------
    // DIÁLOGO DE DESVINCULACIÓN
    // ------------------------------------
    if (showUnlinkDialog) {
        AlertDialog(
            onDismissRequest = { showUnlinkDialog = false },
            title = { Text("Quitar número de teléfono", color = Color.Black) },
            text = { Text("¿Seguro que deseas desvincular tu número? Ya no podrás hacer inicio de sesión por SMS.", color = Color.Black) },
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
                    Text("Cancelar", color = Color.Black)
                }
            }
        )
    }
}
