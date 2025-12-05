package com.example.practicas.oAuth

import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.minutes

object TokenRefresher {

    private var job: Job? = null

    fun start() {
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                delay(55.minutes)
                // Esto forzará un refresh si ya caducó
                TokenStore.getValidAccessToken()
            }
        }
    }

    fun stop() {
        job?.cancel()
    }
}
