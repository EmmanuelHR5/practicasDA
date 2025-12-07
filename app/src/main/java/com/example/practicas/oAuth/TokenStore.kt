package com.example.practicas.oAuth

import android.content.Context
import android.util.Log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object TokenStore {

    private const val PREF = "spotify_tokens"
    private var appContext: Context? = null
    private val mutex = Mutex()

    fun init(context: Context) {
        appContext = context.applicationContext
        Log.d("TokenStore", "⚙️ TokenStore inicializado")
    }

    private fun sp() = appContext!!.getSharedPreferences(PREF, Context.MODE_PRIVATE)

    suspend fun saveTokens(
        access: String,
        refresh: String?,
        expiresIn: Int
    ) = mutex.withLock {
        val now = System.currentTimeMillis()
        val expiresAt = now + expiresIn * 1000

        sp().edit().apply {
            putString("access_token", access)
            if (refresh != null) putString("refresh_token", refresh)
            putLong("expires_at", expiresAt)
            apply()
        }

        Log.d("TokenStore", """
            🔐 TOKEN GUARDADO:
            access_token = $access
            refresh_token = $refresh
            expires_at = $expiresAt (${expiresIn}s)
        """.trimIndent())
    }

    fun getAccessToken(): String? {
        val token = sp().getString("access_token", null)
        Log.d("TokenStore", "➡️ getAccessToken(): $token")
        return token
    }

    fun getRefreshToken(): String? {
        val token = sp().getString("refresh_token", null)
        Log.d("TokenStore", "➡️ getRefreshToken(): $token")
        return token
    }

    fun isAccessTokenExpired(): Boolean {
        val expiresAt = sp().getLong("expires_at", 0L)
        val expired = System.currentTimeMillis() >= expiresAt
        Log.d("TokenStore", "⏳ ¿Token expirado?: $expired")
        return expired
    }

    suspend fun getValidAccessToken(): String? = mutex.withLock {

        Log.d("TokenStore", "🔎 getValidAccessToken() llamado")

        val access = getAccessToken()
        val refresh = getRefreshToken()

        if (refresh == null) {
            Log.e("TokenStore", "❌ No hay refresh token almacenado")
            return null
        }

        if (access != null && !isAccessTokenExpired()) {
            Log.d("TokenStore", "✔️ Token válido: $access")
            return access
        }

        Log.w("TokenStore", "♻️ Token expirado, refrescando...")

        return try {
            val response = SpotifyOAuthService.refreshAccessToken(refresh)

            saveTokens(
                response.access_token,
                refresh,
                response.expires_in
            )

            Log.d("TokenStore", "✨ Nuevo access_token: ${response.access_token}")
            response.access_token

        } catch (e: Exception) {
            Log.e("TokenStore", "❌ Error refrescando token: ${e.message}")
            null
        }
    }

    fun clear() {
        sp().edit().clear().apply()
        Log.w("TokenStore", "🧹 Tokens eliminados")
    }
}
