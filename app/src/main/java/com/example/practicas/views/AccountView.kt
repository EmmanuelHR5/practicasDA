package com.example.practicas.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.practicas.viewmodel.MusicViewModel

@Composable
fun AccountView(
    viewModel: MusicViewModel,
    modifier: Modifier = Modifier
) {
    val user = viewModel.userProfile.collectAsState().value

    LaunchedEffect(Unit) { viewModel.loadUserProfile() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {

        if (user == null) {
            CircularProgressIndicator()
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    model = user.images.firstOrNull()?.url,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                user.display_name?.let { Text(it, style = MaterialTheme.typography.headlineSmall) }
                Text("Email: ${user.email}")
                Text("País: ${user.country ?: "Desconocido"}")
                Text("Plan: ${user.product ?: "Free"}")
            }
        }
    }
}
