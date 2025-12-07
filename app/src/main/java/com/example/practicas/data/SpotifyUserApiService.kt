package com.example.practicas.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SpotifyUserApiService {

    fun create(): SpotifyApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(SpotifyUserAuthInterceptor()) // ← YA NO RECIBE CONTEXT
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.spotify.com/v1/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyApi::class.java)
    }
}
