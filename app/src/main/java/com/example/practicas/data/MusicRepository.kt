package com.example.practicas.data

import android.content.Context
import android.util.Log
import com.example.practicas.model.*
import com.example.practicas.network.GeniusApiClient
import com.example.practicas.network.LyricsScraper
import com.example.practicas.oAuth.TokenStore
import com.example.practicas.utils.cleanArtist
import com.example.practicas.utils.cleanTitle

class MusicRepository(
    private val context: Context,
    private val api: SpotifyApi = SpotifyUserApiService.create()

) {

    private val geniusToken =
        "Bearer S92bWJpKznKQqq4ODqnm0M9jPBTtVdxEq4UrgiXnmER1lfNfM06YTQaBUan170MS"

    suspend fun getPlaylistTracks(id: String, limit: Int = 100, offset: Int = 0): List<SpotifyPlaylistTrackItem> {
        return api.getPlaylistTracks(id, limit, offset).items
    }

    suspend fun searchTracks(query: String): List<SpotifyTrack> {
        return api.searchTracks(query).tracks.items
    }

    suspend fun getTrackDetail(id: String): SpotifyTrack {
        return api.getTrackDetail(id)
    }

    suspend fun getCurrentUserProfile(): SpotifyUserProfile {
        return api.getCurrentUser()
    }

    /* ------------ LETRAS (API externa) ------------ */
    suspend fun getLyricsFromGenius(track: SpotifyTrack): String {

        val title = cleanTitle(track.name)
        val artist = cleanArtist(track.artists.firstOrNull()?.name ?: "")

        val queries = listOf(
            "$title $artist lyrics",
            "$title lyrics",
            "$artist $title",
            title,
            artist
        )

        for (query in queries) {
            try {
                val result = GeniusApiClient.api.searchSong(
                    query = query,
                    token = geniusToken
                )

                val hit = result.response.hits.firstOrNull()?.result
                val url = hit?.url

                Log.d("LyricsRepo", "🔗 URL encontrada: $url")

                if (url != null) {
                    val lyrics = LyricsScraper.scrape(context, url)

                    if (lyrics != "Letra no disponible.") {
                        return lyrics
                    }
                }

            } catch (e: Exception) {
                Log.e("LyricsRepo", "❌ Error buscando letras: ${e.message}")
            }
        }

        return "Letra no disponible."
    }



    suspend fun getAllPlaylistTracks(id: String): List<SpotifyPlaylistTrackItem> {
        val all = mutableListOf<SpotifyPlaylistTrackItem>()

        // 1️⃣ Primera llamada normal
        var page = api.getPlaylistTracks(id, limit = 100, offset = 0)
        all.addAll(page.items)

        // 2️⃣ Si hay más páginas, seguir
        while (page.next != null) {
            page = api.getNextPage(page.next!!)
            all.addAll(page.items)
        }

        return all
    }


    /* ------------ Traducción ------------- */

    suspend fun translateWithDeepL(text: String, targetLang: String): String {
        if (text.isBlank() || text == "_loading_") return ""

        val clean = cleanBeforeTranslate(text)
        val MAX_CHARS = 4500
        val chunks = clean.chunked(MAX_CHARS)
        val resultado = StringBuilder()

        for ((index, rawChunk) in chunks.withIndex()) {

            val chunk = rawChunk
                .replace("\\", "\\\\")   // Escapa backslashes
                .replace("\"", "\\\"")   // Escapa comillas dobles
                .trim()

            try {
                val client = okhttp3.OkHttpClient()

                val body = okhttp3.FormBody.Builder()
                    .add("auth_key", SpotifyConfig.DEEPL_API_KEY)
                    .add("text", chunk)
                    .add("target_lang", targetLang) // SIN source_lang
                    .build()

                val request = okhttp3.Request.Builder()
                    .url("https://api-free.deepl.com/v2/translate")
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                val json = response.body?.string()

                val translated = Regex("\"text\":\"(.*?)\"")
                    .find(json ?: "")
                    ?.groupValues?.get(1)
                    ?.replace("\\n", "\n")
                    ?.replace("\\\"", "\"")
                    ?.trim()

                if (!translated.isNullOrBlank()) {
                    resultado.append(translated).append("\n\n")
                }

            } catch (e: Exception) {
                Log.e("DeepL", "ERROR chunk ${index+1}: ${e.message}")
            }
        }

        return resultado.toString().trim()
    }


    // Obtener playlists del usuario
    suspend fun getUserPlaylists(limit: Int = 1): SpotifyPlaylistResponse {
        val token = "Bearer ${TokenStore.getValidAccessToken()}"
        return api.getUserPlaylists(limit, token)
    }

    // Top artistas
    suspend fun getTopArtists(): List<SpotifyArtist> {
        val token = "Bearer ${TokenStore.getValidAccessToken()}"
        return api.getTopArtists(token).items
    }

    fun cleanBeforeTranslate(text: String): String {
        return text
            .replace(Regex("\\[.*?]"), "") // elimina cualquier bloque [ ... ]
            .replace(Regex("\\s+"), " ")   // reduce espacios excesivos
            .trim()
    }



}
