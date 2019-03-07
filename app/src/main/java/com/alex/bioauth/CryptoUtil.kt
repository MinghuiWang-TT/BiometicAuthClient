package com.alex.bioauth

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProperties.DIGEST_SHA256
import android.security.keystore.KeyProperties.PURPOSE_SIGN
import androidx.annotation.RequiresApi
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.Signature
import java.security.spec.RSAKeyGenParameterSpec


private const val ANDROID_KEY_STORE = "AndroidKeyStore"

class CryptoUtil {

    companion object {
        private val keyStore: KeyStore by lazy { KeyStore.getInstance(ANDROID_KEY_STORE) }
        private val keyGenerator by lazy {
            KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA,
                "AndroidKeyStore"
            )
        }
        private val rsaSpec = RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4)

        @RequiresApi(Build.VERSION_CODES.M)
        fun createKeyPaire(name: String) {
            keyStore.load(null)
            keyGenerator.initialize(
                KeyGenParameterSpec.Builder(name, PURPOSE_SIGN)
                    .setKeySize(2048)
                    .setDigests(DIGEST_SHA256)
                    .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                    .setUserAuthenticationRequired(true)
                    .build()
            )
            keyGenerator.generateKeyPair()

        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun getPublicKey(name: String): String {
            keyStore.load(null)
            val publicKey = keyStore.getCertificate(name).publicKey
            return java.util.Base64.getEncoder().encodeToString(publicKey.encoded);
        }


        fun initSign(keyName: String): Signature? {
            keyStore.load(null)
            val privateKey = keyStore.getKey(keyName, null) as PrivateKey
            val signature: Signature = Signature.getInstance("SHA256withRSA")
            signature.initSign(privateKey)
            return signature
        }


        @RequiresApi(Build.VERSION_CODES.O)
        fun sign(signature: Signature, payload: String): String {
            signature.update(payload.toByteArray(Charsets.UTF_8))
            val signatureBytes = signature.sign()
            return java.util.Base64.getEncoder().encodeToString(signatureBytes);
        }

        fun getSalt(): String {
            return "GouDan"
        }
    }
}