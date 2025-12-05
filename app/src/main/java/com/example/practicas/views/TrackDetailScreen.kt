@file:OptIn(ExperimentalAnimationApi::class)

package com.example.practicas.views

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.practicas.model.SpotifyTrack
import com.example.practicas.viewmodel.MusicViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min


//══════════════════════════════════════════════════════════════════════
//   MAIN SCREEN (TrackDetailScreen) — SIN CAMBIOS, SOLO FIX DE LYRICS
//══════════════════════════════════════════════════════════════════════

@Composable
fun TrackDetailScreen(
    track: SpotifyTrack,
    lyricsOriginal: String,
    lyricsTranslated: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scroll = rememberScrollState()
    val musicVM: MusicViewModel = viewModel()

    var selectedTab by remember { mutableStateOf(0) }

    // FAVORITOS
    val likedTracks by musicVM.likedTracks.collectAsState()
    val isFavorite = likedTracks.any { it.id == track.id }

    val favScale by animateFloatAsState(
        targetValue = if (isFavorite) 1.25f else 1f,
        tween(200)
    )

    // COLOR DOMINANTE
    var dominantColor by remember { mutableStateOf(Color(0xFF111111)) }

    LaunchedEffect(track.id) {
        val url = track.album.images.firstOrNull()?.url ?: return@LaunchedEffect
        val loader = ImageLoader(context)
        val req = ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false)
            .build()

        val result = withContext(Dispatchers.IO) { loader.execute(req) }
        val bmp = (result.drawable as? BitmapDrawable)?.bitmap
        bmp?.let {
            val palette = Palette.from(it).generate()
            val colorInt = palette.getVibrantColor(palette.getDominantColor(0xFF111111.toInt()))
            dominantColor = Color(colorInt)
        }
    }

    // 🎧 PREVIEW AUDIO (Spotify o Apple)
    var previewUrl by remember { mutableStateOf(track.preview_url) }

    val mediaPlayer = remember { MediaPlayer() }

    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }

    var currentTime by remember { mutableStateOf(0) }      // ms
    var totalTime by remember { mutableStateOf(30000) }    // default 30s

// 🔄 CONFIGURACIÓN + LIMPIEZA DEL MEDIAPLAYER
    DisposableEffect(previewUrl) {

        if (!previewUrl.isNullOrEmpty()) {
            mediaPlayer.setOnCompletionListener {
                isPlaying = false
                progress = 1f
                currentTime = totalTime
            }
        }

        onDispose {
            try { mediaPlayer.stop() } catch (_: Exception) {}
            try { mediaPlayer.reset() } catch (_: Exception) {}
            try { mediaPlayer.release() } catch (_: Exception) {}
        }
    }

    // ▶ FUNCIÓN PLAY / PAUSE
    fun togglePlay() {
        if (previewUrl.isNullOrEmpty()) return

        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            isPlaying = false
        } else {
            try {
                mediaPlayer.reset()
                mediaPlayer.setDataSource(previewUrl)
                mediaPlayer.prepareAsync()
                mediaPlayer.setOnPreparedListener {
                    totalTime = it.duration
                    it.start()
                    isPlaying = true
                }
            } catch (_: Exception) { }
        }
    }

// 🔁 ACTUALIZAR PROGRESO + TIMER
    LaunchedEffect(isPlaying) {
        while (isPlaying && mediaPlayer.isPlaying) {
            currentTime = mediaPlayer.currentPosition

            progress = (currentTime.toFloat() / mediaPlayer.duration.toFloat())
                .coerceIn(0f, 1f)

            delay(100)
        }
    }



    //══════════════════════════════ UI ══════════════════════════════

    Box(Modifier.fillMaxSize()) {

        // PORTADA
        AsyncImage(
            model = track.album.images.firstOrNull()?.url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(360.dp)
        )

        // FONDO GRADIENT
        Box(
            Modifier
                .fillMaxSize()
                .padding(top = 360.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            dominantColor,
                            dominantColor.copy(alpha = 0.75f),
                            dominantColor.copy(alpha = 0.55f),
                            Color.Black.copy(alpha = 0.9f)
                        )
                    )
                )
        )

        // BOTÓN BACK FIJO
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp)
                .size(44.dp)
                .background(Color.Black.copy(alpha = 0.45f), CircleShape)
                .zIndex(10f)
        ) {
            Icon(Icons.Default.ArrowBack, null, tint = Color.White)
        }

        // CONTENIDO SCROLL
        Column(
            Modifier.fillMaxSize().verticalScroll(scroll).zIndex(5f)
        ) {

            Spacer(Modifier.height(360.dp))

            Text(track.name, color = Color.White, fontSize = 26.sp,
                modifier = Modifier.padding(horizontal = 16.dp))

            Text(track.artists.joinToString { it.name },
                color = Color.LightGray,
                modifier = Modifier.padding(horizontal = 16.dp))

            Spacer(Modifier.height(18.dp))

            Row(
                Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // ▶ Play button
                IconButton(
                    onClick = { togglePlay() },
                    modifier = Modifier
                        .scale(if (isPlaying) 1.15f else 1f)
                        .size(64.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                // Barra de progreso
                Column(Modifier.weight(1f)) {

                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(formatTime(currentTime), color = Color.White, fontSize = 12.sp)
                        Text(formatTime(totalTime), color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                }

                Spacer(Modifier.width(12.dp))

                // ❤️ Favorito
                IconButton(
                    onClick = { musicVM.toggleFavorite(track) },
                    modifier = Modifier.scale(if (isFavorite) 1.2f else 1f)
                ) {
                    Icon(
                        if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        null,
                        tint = if (isFavorite) Color.Red else Color.White
                    )
                }
            }


            Spacer(Modifier.height(20.dp))

            // TABS
            val tabs = listOf("Original", "Traducción")

            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { i, t ->
                    Tab(
                        selected = selectedTab == i,
                        onClick = { selectedTab = i },
                        text = {
                            Text(
                                t,
                                color = if (selectedTab == i)
                                    MaterialTheme.colorScheme.primary else Color.LightGray
                            )
                        }
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            val textToShow =
                if (selectedTab == 0) lyricsOriginal else lyricsTranslated

            LyricsBoxPro(
                lyrics = textToShow,
                modifier = Modifier.fillMaxWidth().padding(bottom = 140.dp)
            )
        }
    }
}


//══════════════════════════════════════════════════════════════════════
//   LYRICS PRO — Genius Highlight + LongPress Menu + Share Image
//══════════════════════════════════════════════════════════════════════

@Composable
fun LyricsBoxPro(
    lyrics: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lines = remember(lyrics) { lyrics.split("\n") }
    val selectedLines = remember { mutableStateListOf<Int>() }
    var showMenuForLine by remember { mutableStateOf<Int?>(null) }

    Box(modifier) {

        Column(Modifier.fillMaxWidth()) {

            lines.forEachIndexed { index, line ->

                val isSelected = selectedLines.contains(index)

                val highlightAlpha by animateFloatAsState(
                    targetValue = if (isSelected) 0.35f else 0f,
                    animationSpec = tween(200),
                    label = ""
                )

                Box(
                    Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = {
                                if (isSelected) selectedLines.remove(index)
                                else selectedLines.add(index)
                            },
                            onLongClick = {
                                showMenuForLine = index
                            }
                        )
                        .background(
                            Color.Yellow.copy(alpha = highlightAlpha),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(6.dp)
                ) {
                    Text(
                        text = line.ifBlank { " " },
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // MENÚ EMERGENTE
        showMenuForLine?.let { idx ->
            DropdownMenu(
                expanded = true,
                onDismissRequest = { showMenuForLine = null }
            ) {
                DropdownMenuItem(
                    text = { Text("Copiar línea") },
                    onClick = {
                        val clip = android.content.ClipData.newPlainText("line", lines[idx])
                        val clipboard =
                            context.getSystemService(Context.CLIPBOARD_SERVICE)
                                    as android.content.ClipboardManager
                        clipboard.setPrimaryClip(clip)
                        showMenuForLine = null
                    }
                )

                DropdownMenuItem(
                    text = { Text("Compartir línea") },
                    onClick = {
                        shareText(context, lines[idx])
                        showMenuForLine = null
                    }
                )

                DropdownMenuItem(
                    text = { Text("Seleccionar múltiple") },
                    onClick = {
                        if (!selectedLines.contains(idx)) selectedLines.add(idx)
                        showMenuForLine = null
                    }
                )
            }
        }

        // BOTÓN FLOTANTE PARA COMPARTIR SOLO TEXTO
        if (selectedLines.isNotEmpty()) {
            FloatingActionButton(
                onClick = {
                    val finalText = selectedLines.sorted()
                        .joinToString("\n") { lines[it] }

                    shareText(context, finalText)
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Share, null, tint = Color.White)
            }
            // GUARDAR TEXTO SELECCIONADO O TODA LA LETRA
            FloatingActionButton(
                onClick = {
                    val finalText = if (selectedLines.isNotEmpty())
                        selectedLines.sorted().joinToString("\n") { lines[it] }
                    else
                        lyrics

                    saveLyricsToFile(context, "Letra_${System.currentTimeMillis()}", finalText)
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Download, null, tint = Color.White)
            }

        }
    }
}



//══════════════════════════════════════════════════════════════════════
//   SHARE TEXT
//══════════════════════════════════════════════════════════════════════

fun shareText(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Compartir letra"))
}

fun saveLyricsToFile(context: Context, title: String, text: String) {
    try {
        val fileName = "${title.replace(" ", "_")}.txt"
        val file = File(context.getExternalFilesDir(null), fileName)

        FileOutputStream(file).use {
            it.write(text.toByteArray())
        }

        // Compartir / Abrir el archivo ya guardado
        val uri = FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "text/plain")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(
            Intent.createChooser(intent, "Archivo guardado correctamente")
        )

    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun formatTime(ms: Int): String {
    val sec = ms / 1000
    val m = sec / 60
    val s = sec % 60
    return "%01d:%02d".format(m, s)
}
