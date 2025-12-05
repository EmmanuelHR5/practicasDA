package com.example.practicas.oAuth

import android.content.Context
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object TokenStore {

    private const val PREF = "spotify_tokens"
    private var appContext: Context? = null
    private val mutex = Mutex()

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private fun sp() = appContext!!.getSharedPreferences(PREF, Context.MODE_PRIVATE)

    suspend fun saveTokens(
        access: String,
        refresh: String?,
        expiresIn: Int
    ) = mutex.withLock {
        val now = System.currentTimeMillis()
        sp().edit().apply {
            putString("access_token", access)
            if (refresh != null) putString("refresh_token", refresh)
            putLong("expires_at", now + expiresIn * 1000)
            apply()
        }
    }

    fun getAccessToken(): String? =
        sp().getString("access_token", null)

    fun getRefreshToken(): String? =
        sp().getString("refresh_token", null)

    fun isAccessTokenExpired(): Boolean {
        val expiresAt = sp().getLong("expires_at", 0L)
        return System.currentTimeMillis() >= expiresAt
    }

    suspend fun getValidAccessToken(): String? = mutex.withLock {
        val access = getAccessToken()
        val refresh = getRefreshToken()

        if (refresh == null) return null

        if (!isAccessTokenExpired()) return access

        val response = SpotifyOAuthService.refreshAccessToken(refresh)
        saveTokens(
            response.access_token,
            refresh,
            response.expires_in
        )
        response.access_token
    }

    fun clear() {
        sp().edit().clear().apply()
    }

}
