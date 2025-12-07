    package com.example.practicas.views

    import android.os.Build
    import androidx.annotation.RequiresApi
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.interaction.MutableInteractionSource
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.lazy.items
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material3.CircularProgressIndicator
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.Text
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.layout.ContentScale
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.unit.dp
    import androidx.navigation.NavHostController
    import coil.compose.AsyncImage
    import com.example.practicas.model.SpotifyPlaylistTrackItem
    import com.example.practicas.viewmodel.MusicViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun HomeScreen(
        viewModel: MusicViewModel,
        navController: NavHostController
    ) {
        val context = LocalContext.current

        val tracks by viewModel.playlistTracks.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()

        // 🚀 CARGA AUTOMÁTICA DE LA PLAYLIST AL ENTRAR
        LaunchedEffect(Unit) {
            if (tracks.isEmpty()) {
                viewModel.loadImBored()
            }
        }

        Box(Modifier.fillMaxSize()) {

            if (isLoading && tracks.isEmpty()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(tracks) { item ->
                        SongCard(
                            item = item,
                            onClick = {
                                viewModel.selectTrack(context, item.track)
                                navController.navigate("trackDetail")
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun SongCard(
        item: SpotifyPlaylistTrackItem,
        onClick: () -> Unit
    ) {
        val imageUrl = item.track.album.images.firstOrNull()?.url ?: ""
        val title = item.track.name
        val artist = item.track.artists.joinToString { it.name }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .noRippleClickable(onClick)
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = artist,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    @Composable
    fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier =
        this.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
            onClick = onClick
        )
