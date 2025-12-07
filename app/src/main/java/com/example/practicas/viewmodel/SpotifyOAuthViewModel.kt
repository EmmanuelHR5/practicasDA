package com.example.practicas.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicas.data.SpotifyConfig
import com.example.practicas.oAuth.SpotifyOAuthService
import com.example.practicas.oAuth.TokenStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SpotifyOAuthViewModel(app: Application) : AndroidViewModel(app) {

    val isAuthenticated = MutableStateFlow(false)

    init {
        TokenStore.init(app)
        viewModelScope.launch {
            isAuthenticated.value = TokenStore.getAccessToken() != null
        }
    }

    fun handleAuthCode(code: String) {
        viewModelScope.launch {
            val token = SpotifyOAuthService.exchangeCodeForToken(code)
            TokenStore.saveTokens(
                token.access_token,
                token.refresh_token,
                token.expires_in
            )
            isAuthenticated.value = true
        }
    }

    fun checkAuthState() {
        isAuthenticated.value = TokenStore.getAccessToken() != null
    }

    fun logout() {
        TokenStore.clear()
        isAuthenticated.value = false
    }
    fun startOAuth(activity: Activity) {

        val scopes = SpotifyConfig.scopes.joinToString(" ")
        val encodedScopes = Uri.encode(scopes)
        val encodedRedirect = Uri.encode(SpotifyConfig.REDIRECT_URI)

        val url =
            "https://accounts.spotify.com/authorize" +
                    "?client_id=${SpotifyConfig.CLIENT_ID}" +
                    "&response_type=code" +
                    "&redirect_uri=$encodedRedirect" +
                    "&scope=$encodedScopes"

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        activity.startActivity(intent)
    }


}
