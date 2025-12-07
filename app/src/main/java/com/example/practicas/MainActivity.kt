package com.example.practicas

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.practicas.navigation.AppNavigation
import com.example.practicas.viewmodel.MusicViewModel
import com.example.practicas.ui.theme.AppTheme
import com.example.practicas.viewmodel.SpotifyOAuthViewModel
import com.example.practicas.views.HomeScreen
import com.example.practicas.views.LoginSpotifyScreen

class MainActivity : ComponentActivity() {

    private lateinit var oauthVM: SpotifyOAuthViewModel
    private lateinit var musicVM: MusicViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        oauthVM = ViewModelProvider(this)[SpotifyOAuthViewModel::class.java]
        musicVM = ViewModelProvider(this)[MusicViewModel::class.java]

        handleRedirect(intent)

        setContent {

            var isDark by remember { mutableStateOf(true) }

            AppTheme(darkTheme = isDark) {
                val navController = rememberNavController()

                LaunchedEffect(Unit) {
                    oauthVM.checkAuthState()
                }

                val isLoggedIn by oauthVM.isAuthenticated.collectAsState()

                LaunchedEffect(isLoggedIn) {
                    if (isLoggedIn) {
                        musicVM.loadUserProfile()
                    }
                }

                if (!isLoggedIn) {
                    LoginSpotifyScreen(oauthVM)

                } else {
                    AppNavigation(
                        navController = navController,
                        musicVM = musicVM,
                        onToggleTheme = { isDark = !isDark },
                        onLogout = {
                            // 1) Marcamos al ViewModel como NO autenticado
                            oauthVM.isAuthenticated.value = false
                        }
                    )
                }
            }

        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleRedirect(intent)
    }

    private fun handleRedirect(intent: Intent) {
        val data = intent.data ?: return
        if (data.scheme == "com.example.practicas" && data.host == "auth") {
            val code = data.getQueryParameter("code")
            if (!code.isNullOrEmpty()) {
                oauthVM.handleAuthCode(code)
            }
        }
    }
}
