package com.example.practicas.views

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.practicas.viewmodel.MusicViewModel

@Composable
fun AccountView(
    viewModel: MusicViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val user by viewModel.userProfile.collectAsState()
    val followers by viewModel.followers.collectAsState()
    val playlistCount by viewModel.playlistCount.collectAsState()
    val dominantGenre by viewModel.dominantGenre.collectAsState()

    var showUnlinkDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Cargar datos extras cuando ya hay usuario
    LaunchedEffect(user) {
        if (user != null) {
            viewModel.loadExtraProfileData()
        }
    }

    // Fondo adaptable al tema
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        if (user == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.height(12.dp))

                // ===== AVATAR =====
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .shadow(20.dp, CircleShape, clip = true)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    AsyncImage(
                        model = user!!.images.firstOrNull()?.url,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(Modifier.height(18.dp))

                // ===== NOMBRE =====
                Text(
                    text = user!!.display_name ?: "Usuario",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(Modifier.height(8.dp))

                // ===== BADGES =====
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Badge Premium (Mantiene gradiente)
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFFFD700), Color(0xFFFFC107))
                                ),
                                RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("PREMIUM", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    // Badge País (adaptado a tema)
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(user!!.country ?: "MX", color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ===== CARD INFORMACIÓN =====
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(10.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InfoRow("Email", user!!.email ?: "Desconocido")
                        InfoRow("País", user!!.country ?: "Desconocido")
                        InfoRow("Plan", user!!.product ?: "Free")
                    }
                }

                // ===== ESTADÍSTICAS =====
                Text(
                    "Estadísticas del usuario",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(bottom = 8.dp, top = 16.dp)
                        .widthIn(max = 420.dp)
                )

                StatCard("Seguidores", followers.toString())
                StatCard("Playlists creadas", playlistCount.toString())
                StatCard("Música dominante", dominantGenre)

                Spacer(Modifier.height(28.dp))

                // ===== ACCIONES =====
                Text(
                    "Opciones",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .widthIn(max = 420.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    ActionButton(
                        text = "Ver perfil en Spotify",
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        val url = user!!.external_urls["spotify"]
                        if (url != null) {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            )
                        }
                    }

                    ActionButton(
                        text = "Actualizar información",
                        color = MaterialTheme.colorScheme.secondary
                    ) {
                        viewModel.loadUserProfile()
                    }

                    ActionButton(
                        text = "Desvincular cuenta",
                        color = MaterialTheme.colorScheme.error
                    ) {
                        showUnlinkDialog = true
                    }

                    OutlinedButton(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cerrar sesión local", color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }

    // ================= DIÁLOGOS =================

    if (showUnlinkDialog) {
        ConfirmationDialog(
            title = "Desvincular cuenta",
            text = "Esto eliminará tus tokens y deberás iniciar sesión nuevamente.",
            confirmText = "Desvincular",
            confirmColor = MaterialTheme.colorScheme.error,
            onConfirm = {
                viewModel.logout()
                showUnlinkDialog = false
            },
            onDismiss = { showUnlinkDialog = false }
        )
    }

    if (showLogoutDialog) {
        ConfirmationDialog(
            title = "Cerrar sesión",
            text = "Esto cerrará tu sesión local, pero no revocará el acceso a Spotify.",
            confirmText = "Cerrar sesión",
            confirmColor = MaterialTheme.colorScheme.error,
            onConfirm = {
                viewModel.logout()
                showLogoutDialog = false
            },
            onDismiss = { showLogoutDialog = false }
        )
    }
}

// ------------------------------------------------------------
// COMPONENTES REUTILIZABLES
// ------------------------------------------------------------

@Composable
fun InfoRow(label: String, value: String) {
    Column {
        Text(
            label,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontSize = 13.sp
        )
        Text(
            value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
fun ActionButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(text, color = MaterialTheme.colorScheme.onPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ConfirmationDialog(
    title: String,
    text: String,
    confirmText: String,
    confirmColor: Color,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(title, color = MaterialTheme.colorScheme.onSurface) },
        text = { Text(text, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText, color = confirmColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    )
}

@Composable
fun StatCard(title: String, value: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .padding(vertical = 4.dp)
            .shadow(8.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                title,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontSize = 14.sp
            )
            Text(
                value,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
