package com.example.practicas

import androidx.compose.ui.graphics.Color

data class Equipo(
    val nombre: String,
    val ciudad: String,
    val fundacion: String,
    val jugadorMasImportante: String,
    val trofeos: String,
    val descripcion: String,
    val logo: Int
)

data class Conferencia(
    val nombre: String,
    val equipos: List<Equipo>,
    val colorFondo: Color
)