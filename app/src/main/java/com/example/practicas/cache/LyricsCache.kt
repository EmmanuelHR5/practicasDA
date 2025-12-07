package com.example.practicas.cache

import android.content.Context
import java.io.File

object LyricsCache {

    private fun getCacheDir(context: Context): File {
        val dir = File(context.filesDir, "lyrics_cache")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun getCachedLyrics(context: Context, trackId: String, translated: Boolean): String? {
        val fileName = if (translated)
            "${trackId}_translated.txt"
        else
            "${trackId}_original.txt"

        val file = File(getCacheDir(context), fileName)
        return if (file.exists()) file.readText() else null
    }

    fun saveLyrics(context: Context, trackId: String, translated: Boolean, text: String) {
        val fileName = if (translated)
            "${trackId}_translated.txt"
        else
            "${trackId}_original.txt"

        val file = File(getCacheDir(context), fileName)
        file.writeText(text)
    }
}
