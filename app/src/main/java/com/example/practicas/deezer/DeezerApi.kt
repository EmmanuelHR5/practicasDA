package com.example.practicas.deezer

import retrofit2.http.GET
import retrofit2.http.Query

interface DeezerApi {

    @GET("search")
    suspend fun searchTrack(
        @Query("q") query: String
    ): DeezerSearchResponse
}
