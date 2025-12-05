package com.example.practicas.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.practicas.viewmodel.SpotifyOAuthViewModel
import androidx.activity.compose.LocalActivity

@Composable
fun LoginSpotifyScreen(oauthVM: SpotifyOAuthViewModel) {

    // AHORA SÍ: usar tu LocalActivity
    val activity = LocalActivity.current
        ?: error("Activity no disponible")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {

        Button(
            onClick = { oauthVM.startOAuth(activity) }
        ) {
            Text("Conectar con Spotify")
        }
    }
}
