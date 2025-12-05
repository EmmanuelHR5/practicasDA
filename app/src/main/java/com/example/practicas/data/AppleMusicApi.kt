package com.example.practicas.data

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

data class AppleSearchResponse(
    val results: AppleResults
)

data class AppleResults(
    val songs: AppleSongs?
)

data class AppleSongs(
    val data: List<AppleSong>
)

data class AppleSong(
    val attributes: AppleSongAttributes
)

data class AppleSongAttributes(
    val name: String,
    val artistName: String,
    val previewUrl: String?
)

interface AppleMusicApi {

    @GET("v1/catalog/{country}/search")
    suspend fun searchSong(
        @Header("Authorization") token: String,
        @Path("country") country: String = "mx",
        @Query("term") query: String,
        @Query("limit") limit: Int = 1,
        @Query("types") types: String = "songs"
    ): AppleSearchResponse
}
