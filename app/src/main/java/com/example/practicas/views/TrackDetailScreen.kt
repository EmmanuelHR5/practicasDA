@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.example.practicas.views

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.practicas.cache.LyricsCache
import com.example.practicas.components.PreviewPlayer
import com.example.practicas.model.SpotifyTrack
import com.example.practicas.viewmodel.MusicViewModel
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TrackDetailScreen(
    track: SpotifyTrack,
    musicVM: MusicViewModel,
    onBack: () -> Unit
) {

    val context = LocalContext.current

    // ------------------------------------------------------------
    //  CARGA DE LETRAS AUTOMÁTICA (ORIGINAL + TRADUCCIÓN)
    // ------------------------------------------------------------

    LaunchedEffect(track.id) {
        musicVM.loadLyrics(track)

        // Cargar preview de Deezer
        musicVM.loadDeezerPreviewForTrack(
            trackName = track.name,
            artistName = track.artists.firstOrNull()?.name
        )
    }

    val lyricsOriginal by musicVM.lyricsOriginal.collectAsState()
    val lyricsTranslated by musicVM.lyricsTranslated.collectAsState()
    val likedTracks by musicVM.likedTracks.collectAsState()
    val deezerPreview by musicVM.deezerPreviewUrl.collectAsState()

    val isFavorite = likedTracks.any { it.id == track.id }

    var originalState by remember { mutableStateOf("_loading_") }
    var translatedState by remember { mutableStateOf("_loading_") }

    LaunchedEffect(track.id) {
        val cacheO = LyricsCache.getCachedLyrics(context, track.id, false)
        val cacheT = LyricsCache.getCachedLyrics(context, track.id, true)

        originalState = cacheO ?: "_loading_"
        translatedState = cacheT ?: "_loading_"
    }

    LaunchedEffect(lyricsOriginal) {
        if (lyricsOriginal.isNotBlank() && lyricsOriginal != "_loading_") {
            originalState = lyricsOriginal
            LyricsCache.saveLyrics(context, track.id, false, lyricsOriginal)
        }
    }

    LaunchedEffect(lyricsTranslated) {
        if (lyricsTranslated.isNotBlank() && lyricsTranslated != "_loading_") {
            translatedState = lyricsTranslated
            LyricsCache.saveLyrics(context, track.id, true, lyricsTranslated)
        }
    }

    val favoriteScale by animateFloatAsState(
        targetValue = if (isFavorite) 1.3f else 1f,
        animationSpec = tween(350),
        label = ""
    )

    // ------------------------------------------------------------
    // UI COMPLETA
    // ------------------------------------------------------------
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {

        // Fondo con portada
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(310.dp)
        ) {
            AsyncImage(
                model = track.album.images.firstOrNull()?.url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)
                        )
                    )
            )

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(12.dp)
                    .size(34.dp)
            ) {
                Icon(Icons.Default.ArrowBack, null, tint = Color.White)
            }
        }

        Spacer(Modifier.height(10.dp))

        Text(
            track.name,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Color.Gray
        )

        Text(
            track.artists.joinToString { it.name },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Color.Gray
        )

        Spacer(Modifier.height(20.dp))

        // ------------------------------------------------------------
        // PREVIEW DE 30s (DEEZER)
        // ------------------------------------------------------------
        PreviewPlayer(
            previewUrl = deezerPreview,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1A1A1A))
                .padding(12.dp)
        )

        Spacer(Modifier.height(20.dp))

        // ------------------------------------------------------------
        //  BOTÓN DE FAVORITO
        // ------------------------------------------------------------
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {

            IconButton(
                onClick = { musicVM.toggleFavorite(track) },
                modifier = Modifier.scale(favoriteScale)
            ) {
                Icon(
                    if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    null,
                    tint = if (isFavorite) Color.Red else Color.White
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // ------------------------------------------------------------
        // TABS DE LETRAS
        // ------------------------------------------------------------

        var tabIndex by remember { mutableStateOf(0) }
        val tabs = listOf("Original", "Traducción")

        ScrollableTabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { i, tab ->
                Tab(
                    selected = tabIndex == i,
                    onClick = { tabIndex = i },
                    selectedContentColor = MaterialTheme.colorScheme.primary
                ) {
                    Text(tab, Modifier.padding(10.dp))
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        val textShown = if (tabIndex == 0) originalState else translatedState

        val isLoading = textShown == "_loading_"
        val isNotFound = textShown.isBlank() || textShown == "Letra no disponible."

        when {
            isLoading -> {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(8.dp))
                    Text("Buscando letra...", color = Color.Gray)
                }
            }

            isNotFound -> {
                Text(
                    "Letra no disponible.",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Gray
                )
            }

            else -> {
                ShareableLyricsBox(textShown, context)
            }
        }
    }
}


// ------------------------------------------------------------
// UTILIDADES
// ------------------------------------------------------------

@Composable
fun ShareableLyricsBox(text: String, context: android.content.Context) {
    Column(Modifier.padding(horizontal = 16.dp)) {

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1A1A1A))
                .padding(14.dp)
        ) {
            SelectionContainer {
                Text(text, color = Color.White)
            }
        }

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = { shareText(context, text) },
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Share, null)
            Spacer(Modifier.width(6.dp))
            Text("Compartir letra completa")
        }
    }
}

fun shareText(context: android.content.Context, text: String) {
    val intent = android.content.Intent().apply {
        action = android.content.Intent.ACTION_SEND
        putExtra(android.content.Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    context.startActivity(android.content.Intent.createChooser(intent, null))
}
