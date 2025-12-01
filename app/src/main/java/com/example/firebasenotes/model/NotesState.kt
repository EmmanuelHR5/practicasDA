package com.example.firebasenotes.model

import com.google.firebase.firestore.PropertyName

data class NotesState(
    var emailUser: String = "",
    var title: String = "",
    var note: String = "",
    var date: String = "",
    var idDoc: String = "",

    // Campo en Firestore: "isFavorite"
    // Propiedad en Kotlin: favorite
    @get:PropertyName("isFavorite")
    @set:PropertyName("isFavorite")
    var favorite: Boolean = false,

    var colorHex: String = "#FFFFFF"
)


