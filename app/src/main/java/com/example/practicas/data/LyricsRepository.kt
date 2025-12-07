package com.example.practicas.data

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.practicas.model.*
import com.example.practicas.network.GeniusApiClient
import com.example.practicas.network.LyricsScraper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlin.math.max

class LyricsRepository(private val context: Context) {

    private val token =
        "Bearer S92bWJpKznKQqq4ODqnm0M9jPBTtVdxEq4UrgiXnmER1lfNfM06YTQaBUan170MS"

    // ============================================================
    //  BUSCADOR PRINCIPAL (PRO)
    // ============================================================
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getLyrics(track: SpotifyTrack): String {

        val qTitle = normalizeTitle(track.name)
        val qArtist = normalizeArtist(track.artists.first().name)

        val queries = listOf(
            "$qTitle $qArtist lyrics",
            "$qTitle lyrics",
            "$qArtist $qTitle",
            qTitle,
            "$qArtist $qTitle audio",
            "$qArtist $qTitle letra"
        )

        for (q in queries) {

            Log.d("LyricsAPI", "🔍 Buscando: $q")

            try {
                val response = GeniusApiClient.api.searchSong(q, token)

                // Buscar la mejor coincidencia REAL
                val match = response.response.hits
                    .map { it.result }
                    .maxByOrNull { res ->

                        val (gArtist, gTitle) = extractFields(res)

                        val score = matchScore(
                            qTitle,
                            qArtist,
                            normalizeTitle(gTitle),
                            normalizeArtist(gArtist)
                        )

                        debugMatch(qTitle, qArtist, gTitle, gArtist, score)

                        score
                    }
                    ?.takeIf { res ->
                        val (a, t) = extractFields(res)
                        matchScore(
                            qTitle, qArtist,
                            normalizeTitle(t), normalizeArtist(a)
                        ) >= 0.55
                    }

                if (match != null) {

                    Log.d("LyricsAPI", "🔗 URL candidata: ${match.url}")

                    // Intentar API OFICIAL
                    val fromApi = fetchLyricsGeniusApi(match.id)
                    if (!fromApi.isNullOrBlank()) {
                        return cleanLyrics(fromApi)
                    }

                    // Scraper fallback
                    val scraped = withContext(Dispatchers.Main) {
                        LyricsScraper.scrape(context, match.url)
                    }

                    if (scraped.isNotBlank()) {
                        return cleanLyrics(scraped)
                    }
                }

            } catch (e: Exception) {
                Log.e("LyricsAPI", "❌ Error: ${e.message}")
            }
        }

        return "Letra no disponible."
    }

    // ============================================================
    //  EXTRAER ARTISTA Y TÍTULO DE GENIUS (ROBUSTO)
    // ============================================================

    private fun extractFields(res: GeniusResult): Pair<String, String> {

        val artist = res.primary_artist?.name
            ?: res.artist_names
            ?: res.full_title?.split("–")?.getOrNull(0)?.trim()
            ?: ""

        val title = res.title
            ?: res.full_title?.split("–")?.getOrNull(1)?.trim()
            ?: ""

        return Pair(artist, title)
    }

    // ============================================================
    //  NORMALIZADORES PRO
    // ============================================================

    private fun normalizeTitle(title: String): String =
        title.lowercase()
            .replace(Regex("\\(.*?\\)"), "")
            .replace(Regex("\\[.*?\\]"), "")
            .replace(Regex("feat\\.?|ft\\.?"), "")
            .replace(Regex("remix|mix"), "")
            .replace(Regex("[^a-z0-9 ]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

    private fun normalizeArtist(artist: String): String =
        artist.lowercase()
            .replace(Regex("[^a-z0-9 ]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

    // ============================================================
    //  MATCHER PRO (Puntaje total)
    // ============================================================

    private fun matchScore(
        qTitle: String,
        qArtist: String,
        gTitle: String,
        gArtist: String
    ): Double {


        val ga = gArtist.lowercase()
        val qa = qArtist.lowercase()

        // 🔥 DESCARTE DURO: si no coincide ninguna palabra del artista
        val artistWords = qa.split(" ")
        if (artistWords.none { ga.contains(it) }) {
            return -999.0   // invalida cualquier coincidencia
        }

        val titleSim = levenshteinSimilarity(qTitle, gTitle)
        val artistSim = levenshteinSimilarity(qArtist, gArtist)

        val firstQA = qa.split(" ").firstOrNull() ?: ""
        val firstGA = ga.split(" ").firstOrNull() ?: ""

        val penalty =
            if (firstQA == firstGA && qa != ga) 0.25 else 0.0

        return (titleSim * 0.7 + artistSim * 0.3) - penalty
    }


    // ============================================================
    //  DEBUG MATCH (PRO)
    // ============================================================

    private fun debugMatch(
        qTitle: String,
        qArtist: String,
        gTitle: String,
        gArtist: String,
        score: Double
    ) {

        val tSim = levenshteinSimilarity(qTitle, normalizeTitle(gTitle))
        val aSim = levenshteinSimilarity(qArtist, normalizeArtist(gArtist))

    }

    // ============================================================
    //  LEVENSHTEIN SIMILARITY
    // ============================================================

    private fun levenshteinSimilarity(a: String, b: String): Double {
        if (a.isEmpty() || b.isEmpty()) return 0.0

        val dp = Array(a.length + 1) { IntArray(b.length + 1) }

        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j

        for (i in 1..a.length) {
            for (j in 1..b.length) {
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + if (a[i - 1] == b[j - 1]) 0 else 1
                )
            }
        }

        val dist = dp[a.length][b.length]
        return 1.0 - dist.toDouble() / max(a.length, b.length)
    }

    // ============================================================
    //  API GENIUS (markdown → texto limpio)
    // ============================================================

    private fun fetchLyricsGeniusApi(songId: Int): String? {
        return try {
            val req = Request.Builder()
                .url("https://genius.com/api/songs/$songId?text_format=plain")
                .addHeader("Authorization", token)
                .build()

            val res = OkHttpClient().newCall(req).execute()
            val txt = res.body?.string() ?: return null

            val song = JSONObject(txt)
                .optJSONObject("response")
                ?.optJSONObject("song") ?: return null

            song.optString("lyrics_markdown")
                .takeIf { it.isNotBlank() }
                ?: song.optString("lyrics")
                    .takeIf { it.isNotBlank() }

        } catch (e: Exception) {
            null
        }
    }

    // ============================================================
    //  LIMPIEZA FINAL DE LETRA
    // ============================================================

    private fun cleanLyrics(raw: String): String {
        var text = raw

        // 1) Quitar títulos como "CAMBIANDO LA PIEL Lyrics"
        text = text.replace(
            Regex("(?i)^.*?\\s+lyrics\\b", RegexOption.MULTILINE),
            ""
        )

        // 2) Quitar encabezados tipo [Letra de...]
        text = text.replace(
            Regex("\\[\\s*letra de[^]]*\\]", RegexOption.IGNORE_CASE),
            ""
        )

        // 3) Quitar encabezados tipo Lyrics / Letra / Traducción / Snippet cuando son líneas solas
        text = text.replace(
            Regex("(?im)^(lyrics|letra|snippet|traducción|translation).*?$"),
            ""
        )

        // 4) Mantener versos pero limpiar anotaciones
        text = text.replace(
            Regex("\\[(intro|verso|verse|pre-chorus|pre-estribillo|chorus|estribillo|bridge|outro)[^]]*\\]",
                RegexOption.IGNORE_CASE
            ),
            ""
        )

        // 5) Limpiar saltos excesivos
        text = text.replace(Regex("\\n{3,}"), "\n\n")

        // 6) Limpiar espacios dobles
        text = text.replace(Regex(" {2,}"), " ")

        return text.trim()
    }


}
