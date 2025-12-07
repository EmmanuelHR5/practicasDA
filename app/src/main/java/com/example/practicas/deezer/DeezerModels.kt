package com.example.practicas.deezer

import com.squareup.moshi.Json

data class DeezerSearchResponse(
    val data: List<DeezerTrack> = emptyList()
)

data class DeezerTrack(
    val id: Long,
    val title: String,
    val preview: String?,
    val artist: DeezerArtist,
    val album: DeezerAlbum
)

data class DeezerArtist(
    val id: Long? = null,
    val name: String? = null
)

data class DeezerAlbum(
    val id: Long? = null,
    val title: String? = null,
    val cover: String? = null,
    @Json(name = "cover_big") val coverBig: String? = null
)
