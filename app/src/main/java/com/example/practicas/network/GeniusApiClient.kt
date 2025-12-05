package com.example.practicas.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GeniusApiClient {

    val api: GeniusApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.genius.com/")
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeniusApiService::class.java)
    }
}
