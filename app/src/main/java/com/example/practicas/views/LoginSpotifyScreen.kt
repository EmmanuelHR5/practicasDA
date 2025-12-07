package com.example.practicas.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.practicas.viewmodel.SpotifyOAuthViewModel
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.animateFloatAsState
import com.example.practicas.R

@Composable
fun LoginSpotifyScreen(oauthVM: SpotifyOAuthViewModel) {

    val activity = LocalActivity.current ?: error("Activity no disponible")

    // Animación sutil para el botón
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        label = ""
    )

    // Fondo con gradiente estilo Spotify Premium
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF121212),
                        Color(0xFF000000)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // LOGO SPOTIFY
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.spotify_logo_white), // AGREGA ESTE PNG
                    contentDescription = "Spotify Logo",
                    modifier = Modifier.size(78.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Text(
                text = "Bienvenido",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Conecta tu cuenta de Spotify para continuar",
                color = Color.LightGray,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // === BOTÓN ESTILO SPOTIFY ===
            Button(
                onClick = {
                    pressed = true
                    oauthVM.startOAuth(activity)
                },
                modifier = Modifier
                    .scale(scale)
                    .height(52.dp)
                    .width(260.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1DB954), // Spotify Green
                    contentColor = Color.Black
                ),
                shape = CircleShape,
            ) {
                Text(
                    "Continuar con Spotify",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Texto pequeño
            Text(
                text = "No guardamos tu contraseña.\n Solo usamos permisos oficiales de Spotify.",
                color = Color.Gray,
                fontSize = 13.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
