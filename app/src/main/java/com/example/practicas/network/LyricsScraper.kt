package com.example.practicas.network

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

object LyricsScraper {

    private val client = OkHttpClient()

    suspend fun scrape(context: Context, url: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val req = Request.Builder()
                    .url(url)
                    .header("User-Agent", "Mozilla/5.0 (Android)")
                    .build()

                val res = client.newCall(req).execute()
                val html = res.body?.string() ?: return@withContext ""

                // ---------- MÉTODO 1: BUSCAR <div data-lyrics-container> ----------
                val doc = Jsoup.parse(html)
                val containers = doc.select("div[data-lyrics-container='true']")

                if (containers.isNotEmpty()) {
                    return@withContext containers.joinToString("\n\n") { it.text() }
                }

                // ---------- MÉTODO 2: Nuevo selector de Genius ----------
                val paragraphs = doc.select("div.Lyrics__Container-sc-1ynbvzw-6")
                if (paragraphs.isNotEmpty()) {
                    return@withContext paragraphs.joinToString("\n\n") { it.text() }
                }

                // ---------- MÉTODO 3: fallback universal ----------

                return@withContext doc.text()

            } catch (e: Exception) {
                return@withContext ""
            }
        }
    }
}
