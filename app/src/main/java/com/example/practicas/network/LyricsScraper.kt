package com.example.practicas.network

import android.content.Context
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jsoup.Jsoup
import kotlin.coroutines.resume

object LyricsScraper {

    suspend fun scrape(context: Context, url: String): String {
        Log.d("LyricsScraper", "👉 Cargando WebView para: $url")

        return suspendCancellableCoroutine { continuation ->

            val webView = WebView(context)
            webView.settings.javaScriptEnabled = true
            webView.settings.domStorageEnabled = true
            webView.settings.loadsImagesAutomatically = false
            webView.settings.userAgentString =
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                        "(KHTML, like Gecko) Chrome/125 Safari/537.36"

            webView.webViewClient = object : WebViewClient() {

                override fun onPageFinished(view: WebView?, finishedUrl: String?) {
                    if (finishedUrl == null) return

                    Log.d("LyricsScraper", "✔ Página cargada: $finishedUrl — ejecutando JS")

                    webView.evaluateJavascript(
                        "(function(){ return document.documentElement.outerHTML; })();"
                    ) { htmlRaw ->

                        try {
                            val html = htmlRaw
                                .replace("\\u003C", "<")
                                .replace("\\n", "\n")
                                .replace("\\\"", "\"")

                            val doc = Jsoup.parse(html)

                            // 1️⃣ SELECTOR MODERNO
                            val containers = doc.select("div[class*=Lyrics__Container]")
                            if (containers.isNotEmpty()) {
                                // 1️⃣ Extraer solo contenedores válidos (evitar traducciones/metadata)
                                val containers = doc.select("div[class*=Lyrics__Container]")
                                    .filter { el ->
                                        !el.text().contains("Translations") &&
                                                !el.text().contains("Contributors") &&
                                                !el.text().contains("Lyrics ©") &&
                                                !el.text().contains("Read More")
                                    }

// 2️⃣ Convertir cada bloque en texto con saltos reales
                                val finalLyrics = StringBuilder()

                                for (container in containers) {
                                    // con html() conservamos saltos <br>
                                    val html = container.html()

                                    // convertir <br> a saltos de línea reales
                                    var text = html
                                        .replace("<br>", "\n")
                                        .replace("<br/>", "\n")
                                        .replace("<br />", "\n")

                                    // quitar etiquetas HTML sobrantes
                                    text = Jsoup.parse(text).text()

                                    // agregar doble salto para separar estrofas
                                    finalLyrics.append(text).append("\n\n")
                                }

                                val lyrics = finalLyrics.toString().trim()

                                if (lyrics.isNotBlank()) {
                                    continuation.resume(lyrics)
                                    return@evaluateJavascript
                                }

                            }

                            // 2️⃣ SELECTOR ANTIGUO
                            val oldContainers = doc.select("div[data-lyrics-container=true]")
                            if (oldContainers.isNotEmpty()) {
                                val text = oldContainers.joinToString("\n") { it.text() }
                                if (text.isNotBlank()) {
                                    continuation.resume(text)
                                    return@evaluateJavascript
                                }
                            }

                            continuation.resume("Letra no disponible.")

                        } catch (e: Exception) {
                            Log.e("LyricsScraper", "💥 Error procesando HTML: ${e.message}")
                            continuation.resume("Letra no disponible.")
                        }
                    }
                }
            }

            webView.loadUrl(url)

            // Si la coroutine se cancela
            continuation.invokeOnCancellation {
                webView.destroy()
            }
        }
    }
}
