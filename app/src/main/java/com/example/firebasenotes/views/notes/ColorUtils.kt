package com.example.firebasenotes.views.notes

import androidx.compose.ui.graphics.Color

fun parseHexColor(colorHex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: IllegalArgumentException) {
        Color.White
    }
}
