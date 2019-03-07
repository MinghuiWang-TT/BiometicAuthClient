package com.alex.bioauth

import android.content.DialogInterface
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.alex.bioauth.model.AuthenticationResponse
import com.alex.bioauth.model.Challenge
import com.alex.bioauth.model.ChallengeResponse
import kotlinx.android.synthetic.main.login_fragment.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class LoginFragment : BaseFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.login_fragment, container, false)
        bindView(rootView);
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_fingerprint.setOnClickListener(this@LoginFragment::onFingerPrintClicked)
    }

    fun onFingerPrintClicked(view: View) {
        if (!TextUtils.isEmpty(user_name_edit.text)) {
            mSubscriptions.add(
                Rest.getChallenge(user_name_edit.text.toString()).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ response ->
                        if (response.errorBody() != null) {
                            return@subscribe
                        }
                        onChallengeRetrived(response.body()!!)

                    }, { it ->
                        android.util.Log.e("Alex", it.message, it)

                    })
            )
        }
    }

    private fun onChallengeRetrived(challenge: Challenge) {
        val payload = challenge.nonce.toString() + challenge.challenge + CryptoUtil.getSalt()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val biometricPrompt = BiometricPrompt.Builder(context)
                .setDescription("Description")
                .setTitle("Title")
                .setSubtitle("Subtitle")
                .setNegativeButton("Cancel", ContextCompat.getMainExecutor(context),
                    DialogInterface.OnClickListener { dialog, i ->
                        dialog.cancel()
                    })
                .build()

            val signature = CryptoUtil.initSign(challenge.userName!!)
            val cryptoObject = BiometricPrompt.CryptoObject(signature)
            val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                    super.onAuthenticationError(errorCode, errString)
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                    super.onAuthenticationSucceeded(result)
                    var singedMessage = CryptoUtil.sign(
                        result!!.cryptoObject.signature,
                        challenge.nonce.toString() + challenge.challenge + CryptoUtil.getSalt()
                    )
                    var challengeResponse = ChallengeResponse()
                    challengeResponse.challengeId = challenge.id
                    challengeResponse.payload = singedMessage
                    mSubscriptions.add(
                        Rest.responseChallenge(user_name_edit.text.toString(), challengeResponse)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ response ->
                                if (response.errorBody() != null) {
                                    return@subscribe
                                }

                                onChallengeResponse(response.body()!!)
                            }, { it ->
                                android.util.Log.e("Alex", it.message, it)

                            })
                    )
                }

                override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
                    super.onAuthenticationHelp(helpCode, helpString)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
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
    }

    private fun onChallengeResponse(authenticationResponse: AuthenticationResponse) {
        if (authenticationResponse.status.equals("SUCCESS")) {
            result.text = "Secret code is:" + authenticationResponse.secret
            result.visibility = View.VISIBLE
        }
    }

    fun onFail(message: String) {

    }

    companion object {
        val TAG = LoginFragment::class.java.simpleName

        public fun crateFragment(): Fragment {
            return LoginFragment()
        }
    }
}