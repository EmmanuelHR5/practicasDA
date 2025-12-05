package com.example.practicas.data

import com.example.practicas.oAuth.SpotifyOAuthService
import com.example.practicas.oAuth.TokenStore
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class SpotifyUserAuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        // Obtener tokens SIN context (correcto con tu TokenStore actual)
        var token = TokenStore.getAccessToken()
        val refresh = TokenStore.getRefreshToken()

        if (token.isNullOrEmpty()) {
            throw IllegalStateException("Spotify access token missing.")
        }

        // Construir petición original
        var request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        var response = chain.proceed(request)

        // ✔ Si Spotify dice 401 → token inválido / expirado
        if (response.code == 401 && !refresh.isNullOrEmpty()) {

            response.close()

            //  Refrescar token (suspend → runBlocking)
            val newTokenResponse = runBlocking {
                SpotifyOAuthService.refreshAccessToken(refresh)
            }

            //  Guardar token renovado EN TokenStore
            runBlocking {
                TokenStore.saveTokens(
                    newTokenResponse.access_token,
                    newTokenResponse.refresh_token ?: refresh,
                    newTokenResponse.expires_in
                )
            }

            token = newTokenResponse.access_token

            //  Reintentar la petición con token nuevo
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()

            response = chain.proceed(newRequest)
        }

        return response
    }
}
