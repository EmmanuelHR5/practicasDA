package com.example.practicas.components

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import kotlinx.coroutines.delay

@Composable
fun PreviewPlayer(
    previewUrl: String?,
    modifier: Modifier = Modifier
) {
    if (previewUrl.isNullOrBlank()) return

    val context = LocalContext.current

    val exoPlayer = remember(previewUrl) {
        try {
            val httpDataSourceFactory = DefaultHttpDataSource.Factory()
                .setUserAgent("Mozilla/5.0")
                .setDefaultRequestProperties(mapOf("Accept" to "*/*"))

            val mediaSourceFactory = DefaultMediaSourceFactory(httpDataSourceFactory)

            ExoPlayer.Builder(context)
                .setMediaSourceFactory(mediaSourceFactory)
                .build().apply {
                    setMediaItem(MediaItem.fromUri(previewUrl))
                    prepare()
                }
        } catch (e: Exception) {
            Log.e("PreviewPlayer", "Error creando ExoPlayer: ${e.message}")
            null
        }
    }

    if (exoPlayer == null) return

    var isPlaying by remember { mutableStateOf(false) }
    var durationMs by remember { mutableStateOf(30_000L) }
    var progress by remember { mutableStateOf(0f) }

    // Listener
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {

            override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                isPlaying = isPlayingNow
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    val d = exoPlayer.duration
                    if (d > 0) durationMs = d
                } else if (state == Player.STATE_ENDED) {
                    isPlaying = false
                    progress = 1f
                }
            }
        }

        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.stop()
            exoPlayer.release()
        }
    }

    // Actualizar barra
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            val pos = exoPlayer.currentPosition
            progress = (pos.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
            delay(80)
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(200, easing = LinearEasing),
        label = "previewProgress"
    )

    fun togglePlay() {
        try {
            if (exoPlayer.isPlaying) {
                exoPlayer.pause()
            } else {
                exoPlayer.playWhenReady = true
                exoPlayer.play()
            }
        } catch (e: Exception) {
            Log.e("PreviewPlayer", "Error togglePlay: ${e.message}")
        }
    }

    // ---------------- UI ADAPTADA A MODO CLARO/OSCURO ----------------
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            // Botón Play/Pause adaptado
            IconButton(
                onClick = { togglePlay() },
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                // Barra de progreso adaptada al tema
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                )

                Spacer(Modifier.height(4.dp))

                val currentSec = (durationMs * progress).toInt() / 1000
                val totalSec = (durationMs / 1000).toInt()

                // Tiempos adaptados al tema
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "0:${currentSec.toString().padStart(2, '0')}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "0:${totalSec.toString().padStart(2, '0')}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
