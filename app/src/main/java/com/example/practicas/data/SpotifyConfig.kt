// com/example/spotifymusicapp/data/SpotifyConfig.kt
package com.example.practicas.data

import com.example.practicas.oAuth.TokenStore

object SpotifyConfig {
    const val CLIENT_ID = "456672711aac45e2b7e86f65c8292a06"
    const val CLIENT_SECRET = "9c3498bcf18d46fe824d5b59d7755960"

    const val REDIRECT_URI = "com.example.practicas://auth"

    const val SCOPES =
        "playlist-read-private playlist-read-collaborative user-read-email"

    // PLAYLISTS
    const val PLAYLIST_SHE_IS_JUST_A_GIRL = "6HZ4fe1GTnWxnRtEckm5jm"
    const val PLAYLIST_GYM_TRAINING = "77E0vwfdvfAewRko2naipw"
    const val PLAYLIST_NOSTALGIC_HITS = "5wcoLsZw0Rp1rsB3ydxeJE"

    const val PLAYLIST_idk_imbored = "5cLyThyQIelLoSUi2fJ6nY"

    const val PLAYLIST_FAVORITO = "6PqRRuB8cwJ6IoTlUzyCpX"

    //DEEPL, traduccion
    const val DEEPL_API_KEY = "30e5fc0f-33de-4f1a-8c0e-84d44c2b5096:fx"

    suspend fun getValidAccessToken(): String? {
        // wrapper a TokenStore para no tener que pasar Context por todos lados
        return TokenStore.getValidAccessToken()
    }
}

