package com.example.practicas.apple

import com.example.practicas.data.AppleMusicApi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object AppleMusicClient {

    val api: AppleMusicApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.music.apple.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppleMusicApi::class.java)
    }
}
