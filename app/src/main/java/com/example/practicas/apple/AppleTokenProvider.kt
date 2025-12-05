package com.example.practicas.apple

import android.util.Base64

object AppleTokenProvider {

    private const val TEAM_ID = "TU_TEAM_ID"
    private const val KEY_ID = "TU_KEY_ID"
    private const val PRIVATE_KEY = """
         -----BEGIN PRIVATE KEY-----
        TU_LLAVE_PRIVADA_AQUI
        -----END PRIVATE KEY-----
        """

    private var token: String? = null
    private var expiresAt: Long = 0

    fun getToken(): String {
        val now = System.currentTimeMillis()

        if (token != null && now < expiresAt) return token!!

        val header = Base64.encodeToString(
            """{"alg":"ES256","kid":"$KEY_ID"}""".toByteArray(),
            Base64.NO_WRAP
        )

        val iat = now / 1000
        val exp = iat + (60L * 60 * 24 * 30) // 30 dias

        val claims = Base64.encodeToString(
            """{"iss":"$TEAM_ID","iat":$iat,"exp":$exp}""".toByteArray(),
            Base64.NO_WRAP
        )

        val payload = "$header.$claims"
        val signature = AppleJwtSigner.sign(payload.toByteArray(), PRIVATE_KEY)
        val signatureEncoded = Base64.encodeToString(signature, Base64.NO_WRAP)

        token = "$payload.$signatureEncoded"
        expiresAt = now + (1000L * 60 * 60 * 24 * 30)

        return token!!
    }
}
