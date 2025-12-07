package com.example.practicas.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.practicas.model.SpotifyTrack
import com.example.practicas.viewmodel.MusicViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SearchScreen(
    viewModel: MusicViewModel,
    navController: NavHostController
) {
    val results by viewModel.searchResults.collectAsState()
    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                viewModel.search(query)
            },
            label = { Text("Buscar canción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(Modifier.fillMaxSize()) {
            items(results) { track ->
                SearchResultItem(
                    track = track,
                    onClick = {
                        val context = navController.context
                        viewModel.selectTrack(context, track)
                        navController.navigate("trackDetail")
                    }

                )
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    track: SpotifyTrack,
    onClick: () -> Unit
) {
    val imageUrl = track.album.images.firstOrNull()?.url ?: ""
    val artistNames = track.artists.joinToString { it.name }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .noRippleClickable(onClick)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = track.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = track.name,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = artistNames,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
