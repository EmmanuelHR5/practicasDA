@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.example.practicas.views

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.practicas.cache.LyricsCache
import com.example.practicas.components.PreviewPlayer
import com.example.practicas.model.SpotifyTrack
import com.example.practicas.viewmodel.MusicViewModel
import androidx.palette.graphics.Palette
import android.graphics.RenderEffect
import android.graphics.Shader
import androidx.compose.ui.graphics.asComposeRenderEffect


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TrackDetailScreen(
    track: SpotifyTrack,
    musicVM: MusicViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val themeBackground = MaterialTheme.colorScheme.background

    // ---------------- CARGA AUTOMÁTICA ----------------
    LaunchedEffect(track.id) {
        musicVM.loadLyrics(track)
        musicVM.loadDeezerPreviewForTrack(track.name, track.artists.firstOrNull()?.name)
    }

    val lyricsOriginal by musicVM.lyricsOriginal.collectAsState()
    val lyricsTranslated by musicVM.lyricsTranslated.collectAsState()
    val likedTracks by musicVM.likedTracks.collectAsState()
    val previewUrl by musicVM.deezerPreviewUrl.collectAsState()

    val isFavorite = likedTracks.any { it.id == track.id }

    var originalState by remember { mutableStateOf("_loading_") }
    var translatedState by remember { mutableStateOf("_loading_") }

    // -------- CACHE LETRAS --------
    LaunchedEffect(track.id) {
        originalState = LyricsCache.getCachedLyrics(context, track.id, false) ?: "_loading_"
        translatedState = LyricsCache.getCachedLyrics(context, track.id, true) ?: "_loading_"
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

    // Animación del corazón
    val favoriteScale by animateFloatAsState(
        targetValue = if (isFavorite) 1.3f else 1f,
        animationSpec = tween(350)
    )

    // ---------------- COLOR DOMINANTE + BLUR ----------------
    val albumUrl = track.album.images.firstOrNull()?.url
    var dominantColor by remember { mutableStateOf(themeBackground) }

    LaunchedEffect(albumUrl) {
        albumUrl?.let {
            val bmp = loadBitmapFromUrl(context, it)
            bmp?.let { img ->
                dominantColor = extractDominantColor(img) ?: themeBackground
            }
        }
    }

    // ---------------- UI ----------------
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(dominantColor)
    ) {

        // Fondo blur dinámico
        if (!albumUrl.isNullOrEmpty()) {
            AsyncImage(
                model = albumUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            renderEffect = RenderEffect.createBlurEffect(
                                40f,
                                40f,
                                Shader.TileMode.CLAMP
                            ).asComposeRenderEffect()
                        }
                        alpha = 0.9f
                    }
            )
        }

        // Gradiente para legibilidad
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            dominantColor.copy(alpha = 0.6f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // ------- Portada + botón atrás -------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(top = 40.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = albumUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(220.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        track.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        track.artists.joinToString { it.name },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ------- Preview player -------
            PreviewPlayer(
                previewUrl = previewUrl,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                    .padding(12.dp)
            )

            Spacer(Modifier.height(20.dp))

            // ------- Botón de favorito -------
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
                        contentDescription = null,
                        tint = if (isFavorite) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ------- TABS LETRAS -------
            var tabIndex by remember { mutableStateOf(0) }
            val tabs = listOf("Original", "Traducción")

            ScrollableTabRow(
                selectedTabIndex = tabIndex,
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
            ) {
                tabs.forEachIndexed { i, label ->
                    Tab(
                        selected = tabIndex == i,
                        onClick = { tabIndex = i },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ) {
                        Text(label, Modifier.padding(10.dp))
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // ------- LETRAS -------
            val textShown = if (tabIndex == 0) originalState else translatedState
            val isLoading = textShown == "_loading_"
            val notFound = textShown.isBlank() || textShown == "Letra no disponible."

            when {
                isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(8.dp))
                        Text("Buscando letra…", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                notFound -> {
                    Text(
                        "Letra no disponible.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                else -> {
                    ShareableLyricsBox(textShown)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ------------------------------------------------------------
// Caja de letras compartible
// ------------------------------------------------------------

@Composable
fun ShareableLyricsBox(text: String) {
    Column(Modifier.padding(horizontal = 16.dp)) {

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                .padding(14.dp)
        ) {
            SelectionContainer {
                Text(text, color = MaterialTheme.colorScheme.onSurface)
            }
        }

        Spacer(Modifier.height(12.dp))
    }
}

// ------------------------------------------------------------
// Compartir letra
// ------------------------------------------------------------

fun shareText(context: Context, text: String) {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(intent, null))
}

// ------------------------------------------------------------
// HELPERS: carga de Bitmap + color dominante
// ------------------------------------------------------------

private suspend fun loadBitmapFromUrl(
    context: Context,
    url: String
): Bitmap? = try {
    val loader = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(url)
        .allowHardware(false)
        .build()

    val result = loader.execute(request)
    if (result is SuccessResult) {
        (result.drawable as? BitmapDrawable)?.bitmap
    } else null
} catch (e: Exception) {
    null
}

private fun extractDominantColor(bitmap: Bitmap): Color? = try {
    val palette = Palette.from(bitmap).generate()
    val rgb = palette.getDominantColor(
        palette.getVibrantColor(
            palette.getMutedColor(0xFF444444.toInt())
        )
    )
    Color(rgb)
} catch (_: Exception) {
    null
}
