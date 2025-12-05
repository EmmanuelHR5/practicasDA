package com.example.practicas.network

import com.example.practicas.model.GeniusSearchResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface GeniusApiService {

    @GET("search")
    suspend fun searchSong(
        @Query("q") query: String,
        @Header("Authorization") token: String
    ): GeniusSearchResponse
}
