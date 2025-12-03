
package com.example.firebasenotes.model

import com.google.firebase.firestore.PropertyName

data class NotesState(

    var idDoc: String = "",

    var emailUser: String = "",

    var title: String = "",
    var note: String = "",
    var date: String = "",

    @get:PropertyName("isFavorite")
    @set:PropertyName("isFavorite")
    var favorite: Boolean = false,

    var colorHex: String = "#FFFFFF",

    var category: String = "General",

    var phone: String = "",

    @get:PropertyName("IsDeleted")
    @set:PropertyName("IsDeleted")
    var isDeleted: Boolean = false
)

