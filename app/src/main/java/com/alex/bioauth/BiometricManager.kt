package com.alex.bioauth

import android.annotation.TargetApi
import android.content.Context
import android.content.DialogInterface
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.CancellationSignal
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import java.security.Signature


class BiometricManager {
    var tittle: CharSequence? = null
    var subtitle: CharSequence? = null
    var description: CharSequence? = null

    @TargetApi(Build.VERSION_CODES.M)
    fun authenticate(context: Context, signature: Signature?, callback: BioAuthCallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            authenticateV28(context, signature, callback)
        } else {
            authenticateV23(context, signature, callback)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun authenticateV28(context: Context, signature: Signature?, callback: BioAuthCallback) {
        val biometricPrompt = BiometricPrompt.Builder(context)
            .setDescription(description)
            .setTitle(tittle)
            .setSubtitle(subtitle)
            .setNegativeButton("Cancel", ContextCompat.getMainExecutor(context),
                DialogInterface.OnClickListener { dialog, i ->
                    dialog.cancel()
                })
            .build()

        val cryptoObject = BiometricPrompt.CryptoObject(signature)
        val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                super.onAuthenticationError(errorCode, errString)
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)
                callback.onAuthenticationSucceeded(result!!.cryptoObject.signature)

            }

            override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
                super.onAuthenticationHelp(helpCode, helpString)
                callback.onAuthenticationHelp(helpCode, helpString)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                callback.onAuthenticationFailed()
            }
        }
        val cancellationSignal = CancellationSignal();
        cancellationSignal.setOnCancelListener { authenticationCallback.onAuthenticationFailed() }
        biometricPrompt.authenticate(
            cryptoObject,
            cancellationSignal,
            ContextCompat.getMainExecutor(context),
            authenticationCallback
        )
    }


    @TargetApi(Build.VERSION_CODES.M)
    fun authenticateV23(context: Context, signature: Signature?, callback: BioAuthCallback) {
        val fingerPrintPromt = FingerPrintPromtV23(context, callback, signature!!)
        fingerPrintPromt.tittle = tittle
        fingerPrintPromt.subtitle = subtitle
        fingerPrintPromt.description = description
        fingerPrintPromt.show()
    }

    interface BioAuthCallback {
        fun onAuthenticationSucceeded(signature: Signature?)
        fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?)
        fun onAuthenticationFailed()
        fun onAuthenticationCanceled()
    }

}