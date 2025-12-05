package com.example.practicas.oAuth

import com.example.practicas.data.SpotifyConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Field
import retrofit2.http.Headers
import retrofit2.http.POST

interface SpotifyAuthApi {

    @FormUrlEncoded
    @POST("api/token")
    suspend fun exchangeCode(
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String = SpotifyConfig.REDIRECT_URI,
        @Field("client_id") clientId: String = SpotifyConfig.CLIENT_ID,
        @Field("client_secret") clientSecret: String = SpotifyConfig.CLIENT_SECRET
    ): SpotifyTokenResponse

    @FormUrlEncoded
    @POST("api/token")
    suspend fun refreshToken(
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("refresh_token") refreshToken: String,
        @Field("client_id") clientId: String = SpotifyConfig.CLIENT_ID,
        @Field("client_secret") clientSecret: String = SpotifyConfig.CLIENT_SECRET
    ): SpotifyTokenResponse
}

object SpotifyOAuthService {

    private val api: SpotifyAuthApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://accounts.spotify.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyAuthApi::class.java)
    }

    suspend fun exchangeCodeForToken(code: String) =
        api.exchangeCode(code = code)

    suspend fun refreshAccessToken(refreshToken: String) =
        api.refreshToken(refreshToken = refreshToken)
}

data class SpotifyTokenResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int,
    val refresh_token: String?,
    val scope: String?
)
