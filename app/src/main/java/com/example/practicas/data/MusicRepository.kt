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

        val MAX_CHARS = 4500   // DeepL Free seguro

        // 1️⃣ Dividir texto en chunks
        val chunks = text.chunked(MAX_CHARS)

        val resultado = StringBuilder()

        for ((index, chunk) in chunks.withIndex()) {

            Log.d("DeepL", "📦 Traduciendo chunk ${index + 1}/${chunks.size} (${chunk.length} chars)")

            try {
                val client = okhttp3.OkHttpClient()

                val reqBody = okhttp3.FormBody.Builder()
                    .add("auth_key", SpotifyConfig.DEEPL_API_KEY)
                    .add("text", chunk)
                    .add("source_lang", "EN")
                    .add("target_lang", targetLang)
                    .build()

                Log.d("DeepL", "🔑 API KEY usada: ${SpotifyConfig.DEEPL_API_KEY}")
                Log.d("DeepL", "🌐 URL endpoint: https://api-free.deepl.com/v2/translate")
                Log.d("DeepL", "📤 BODY:\n${reqBody.toString()}")

                val request = okhttp3.Request.Builder()
                    .url("https://api-free.deepl.com/v2/translate")
                    .post(reqBody)
                    .build()

                val response = client.newCall(request).execute()
                val json = response.body?.string()

                Log.d("DeepL", "📩 Respuesta chunk ${index+1}: $json")

                val translated = Regex("\"text\":\"(.*?)\"")
                    .find(json ?: "")
                    ?.groupValues?.get(1)
                    ?.replace("\\n", "\n")
                    ?.replace("\\\"", "\"")
                    ?.trim()

                if (!translated.isNullOrBlank()) {
                    resultado.append(translated).append("\n\n")
                } else {
                    Log.e("DeepL", "❌ Chunk ${index + 1} devolvió vacío")
                }

            } catch (e: Exception) {
                Log.e("DeepL", "💥 EXCEPCIÓN: ${e::class.java.name} -> ${e.message}")
            }
        }

        return resultado.toString().trim()
    }



}
