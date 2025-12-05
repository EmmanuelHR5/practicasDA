package com.example.practicas.data

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.practicas.model.SpotifyTrack
import com.example.practicas.network.GeniusApiClient
import com.example.practicas.network.LyricsScraper
import com.example.practicas.utils.cleanTitle
import com.example.practicas.utils.cleanArtist
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class LyricsRepository(private val context: Context) {

    private val token = "Bearer S92bWJpKznKQqq4ODqnm0M9jPBTtVdxEq4UrgiXnmER1lfNfM06YTQaBUan170MS"

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getLyrics(track: SpotifyTrack): String {
        val title = cleanTitle(track.name)
        val artist = cleanArtist(track.artists.first().name)

        val queries = listOf(
            "$title $artist lyrics",
            "$title lyrics",
            "$artist $title",
            title
        )

        for (q in queries) {
            Log.d("LyricsAPI", "🔍 Buscando: $q")

            try {
                val search = GeniusApiClient.api.searchSong(q, token)

                val validHit = search.response.hits
                    .map { it.result }
                    .firstOrNull { result ->

                        val url = result.url.lowercase()

                        url.contains("-lyrics") &&        // debe ser canción
                                url.contains(cleanTitle(title).lowercase().replace(" ", "-")) && // match del título
                                url.contains(cleanArtist(artist).lowercase().replace(" ", "-"))  // match del artista
                    }


                Log.d("LyricsAPI", "🔗 URL encontrada: ${validHit?.url}")
                Log.d("PREVIEW", track.preview_url ?: "NO PREVIEW")

                if (validHit != null) {
                    val lyrics = LyricsScraper.scrape(context, validHit.url)

                    if (lyrics.isNotBlank() && lyrics != "Letra no disponible.") {
                        return lyrics
                    }
                }

            } catch (e: Exception) {
                Log.e("LyricsAPI", "❌ Error buscando letras: ${e.message}")
            }
        }

        return "Letra no disponible."
    }

    private fun fetchLyricsGeniusApi(songId: Int): String {
        return try {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url("https://genius.com/api/songs/$songId?text_format=plain")
                .addHeader("Authorization", token)
                .build()

            val response = client.newCall(request).execute()
            val json = response.body?.string() ?: return "Letra no disponible."

            Log.d("LyricsAPI", "📄 JSON COMPLETO:\n$json")

            val root = JSONObject(json)
            val song = root
                .optJSONObject("response")
                ?.optJSONObject("song")
                ?: return "Letra no disponible."

            val lyricsMarkdown = song.optString("lyrics_markdown", "")
            val lyricsPlain = song.optString("lyrics", "")

            val finalLyrics =
                when {
                    lyricsMarkdown.isNotBlank() -> lyricsMarkdown
                    lyricsPlain.isNotBlank() -> lyricsPlain
                    else -> ""
                }

            if (finalLyrics.isBlank()) "Letra no disponible." else finalLyrics

        } catch (e: Exception) {
            Log.e("LyricsAPI", "❌ Error al obtener letra: ${e.message}")
            "Letra no disponible."
        }
    }



}
