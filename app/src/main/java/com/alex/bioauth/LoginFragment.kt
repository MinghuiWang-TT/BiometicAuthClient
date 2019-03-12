package com.alex.bioauth

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.alex.bioauth.model.AuthenticationResponse
import com.alex.bioauth.model.Challenge
import com.alex.bioauth.model.ChallengeResponse
import kotlinx.android.synthetic.main.login_fragment.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.security.Signature

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

    private fun onFingerPrintClicked(view: View) {
        if (!TextUtils.isEmpty(user_name_edit.text)) {
            mSubscriptions.add(
                Rest.getChallenge(user_name_edit.text.toString()).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ response ->
                        if (response.errorBody() != null) {
                            return@subscribe
                        }
                        onChallengeRetrieved(response.body()!!)

                    }, { it ->
                        android.util.Log.e("Alex", it.message, it)

                    })
            )
        }
    }

    private fun onChallengeRetrieved(challenge: Challenge) {
        val biometricManager = BiometricManager()
        biometricManager.tittle = "Authentication"
        biometricManager.subtitle = "Please scan your fingerprint"
        biometricManager.description = "Scan your fingerprint to see your secret code"

        val payload = challenge.nonce.toString() + challenge.challenge + CryptoUtil.getSalt()
        val signature = CryptoUtil.initSign(challenge.userName!!)

        biometricManager.authenticate(context!!, signature, object : BiometricManager.BioAuthCallback {
            override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
                android.util.Log.e("Alex", " onAuthenticationHelp helpCode: " + helpCode + " helpString:" + helpString)
            }

            override fun onAuthenticationFailed() {
                android.util.Log.e("Alex", " onAuthenticationFailed ")
            }

            override fun onAuthenticationCanceled() {
                android.util.Log.e("Alex", " onAuthenticationCanceled ")
            }

            override fun onAuthenticationSucceeded(signature: Signature?) {
                var singedMessage = CryptoUtil.sign(
                    signature!!,
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

        })
    }

    private fun onChallengeResponse(authenticationResponse: AuthenticationResponse) {
        if (authenticationResponse.status.equals("SUCCESS")) {
            result.text = "Secret code is:" + authenticationResponse.secret
            result.visibility = View.VISIBLE
        }
    }

    companion object {
        val TAG = LoginFragment::class.java.simpleName

        public fun crateFragment(): Fragment {
            return LoginFragment()
        }
    }
}