package com.example.practicas.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.practicas.model.SpotifyTrack
import com.example.practicas.viewmodel.MusicViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LikedSongsScreen(
    viewModel: MusicViewModel,
    navController: NavHostController
) {
    val liked by viewModel.likedTracks.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        Text(
            text = "Favoritos ❤️",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        if (liked.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No tienes canciones guardadas.", color = Color.Gray)
            }
        } else {
            LazyColumn {
                items(liked, key = { it.id }) { track ->
                    FavoriteItemAnimated(
                        track = track,
                        onRemoveConfirmed = { viewModel.removeFavorite(track) },
                        onClick = {
                            viewModel.selectTrack(track)
                            navController.navigate("trackDetail")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteItemAnimated(
    track: SpotifyTrack,
    onRemoveConfirmed: () -> Unit,
    onClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var shouldRemove by remember { mutableStateOf(false) }

    // Estado de swipe
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart ||
                value == SwipeToDismissBoxValue.StartToEnd
            ) {
                // En lugar de borrar directo → mostramos diálogo
                showDialog = true
                return@rememberSwipeToDismissBoxState false // cancelar swipe real
            }
            true
        }
    )

    // Animación de desaparecer al confirmar
    AnimatedVisibility(
        visible = !shouldRemove,
        exit = shrinkVertically() + fadeOut()
    ) {
        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Red.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(start = 24.dp)
                    )
                }
            },
            content = {
                FavoriteRow(track, onClick)
            }
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            icon = {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color.Red
                )
            },
            title = { Text("Quitar de favoritos") },
            text = {
                Text("¿Deseas quitar \"${track.name}\" de tus favoritos?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        shouldRemove = true
                        onRemoveConfirmed()
                    }
                ) {
                    Text("Sí, quitar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun FavoriteRow(track: SpotifyTrack, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = track.album.images.firstOrNull()?.url,
            contentDescription = track.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text(track.name, color = Color.White)
            Text(
                track.artists.joinToString { it.name },
                color = Color.LightGray,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
