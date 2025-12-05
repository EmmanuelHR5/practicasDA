package com.example.practicas.apple

import android.util.Base64
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec

object AppleJwtSigner {

    fun sign(data: ByteArray, privateKeyPem: String): ByteArray {
        val privateKey = loadPrivateKey(privateKeyPem)
        val signature = Signature.getInstance("SHA256withECDSA")
        signature.initSign(privateKey)
        signature.update(data)
        return signature.sign()
    }

    private fun loadPrivateKey(pem: String): PrivateKey {
        val cleaned = pem
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\s".toRegex(), "")

        val encoded = Base64.decode(cleaned, Base64.DEFAULT)
        val keySpec = PKCS8EncodedKeySpec(encoded)
        val kf = KeyFactory.getInstance("EC")
        return kf.generatePrivate(keySpec)
    }
}
