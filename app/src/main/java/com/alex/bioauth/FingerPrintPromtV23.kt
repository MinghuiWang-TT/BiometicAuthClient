package com.alex.bioauth


import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.CancellationSignal
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.finger_print_dialog.view.*
import java.security.Signature

@TargetApi(Build.VERSION_CODES.M)
class FingerPrintPromtV23(
    context: Context,
    private val bioAuthCallback: BiometricManager.BioAuthCallback,
    private val signature: Signature
) : BottomSheetDialog(context, R.style.BottomDialog) {

    var tittle: CharSequence? = null
    var subtitle: CharSequence? = null
    var description: CharSequence? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        );
        val bottomSheetView = layoutInflater.inflate(R.layout.finger_print_dialog, null)
        setContentView(bottomSheetView)
        bottomSheetView.item_title.text = tittle
        bottomSheetView.item_subtitle.text = subtitle
        bottomSheetView.item_description.text = description
        bottomSheetView.btn_cancel.setOnClickListener { _ ->
            cancelAuthentication()
        }

    }

    override fun onStart() {
        super.onStart()
        val cryptoObject = FingerprintManagerCompat.CryptoObject(signature)
        val fingerprintManagerCompat = FingerprintManagerCompat.from(context)

        fingerprintManagerCompat.authenticate(
            cryptoObject, 0, CancellationSignal(),
            object : FingerprintManagerCompat.AuthenticationCallback() {
                override fun onAuthenticationError(errMsgId: Int, errString: CharSequence?) {
                    super.onAuthenticationHelp(errMsgId, errString)
                    bioAuthCallback.onAuthenticationFailed()
                }

                override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence?) {
                    super.onAuthenticationHelp(helpMsgId, helpString)
                    bioAuthCallback.onAuthenticationHelp(helpMsgId, helpString)
                }

                override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
                    super.onAuthenticationSucceeded(result)
                    dismiss()
                    bioAuthCallback.onAuthenticationSucceeded(result!!.cryptoObject.signature)
                }


                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    bioAuthCallback.onAuthenticationFailed()
                }
            }, null
        )
    }


    override fun onBackPressed() {
        super.onBackPressed()
        cancelAuthentication()
    }

    private fun cancelAuthentication() {
        dismiss()
        bioAuthCallback.onAuthenticationCanceled()

    }
}