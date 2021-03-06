package com.alex.bioauth

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.alex.bioauth.model.User
import kotlinx.android.synthetic.main.enroll_fragment.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class EnrollFragment : BaseFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.enroll_fragment, container, false)
        bindView(rootView);
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_enroll.setOnClickListener(this@EnrollFragment::onEnrollClicked)
        btn_login.setOnClickListener {
            getMainActivity().setContainerFragment(
                LoginFragment.crateFragment(),
                LoginFragment.TAG,
                false
            )
        }
    }

    private fun onEnrollClicked(view: View) {
        if (!TextUtils.isEmpty(user_name_edit.text) && !TextUtils.isEmpty(secret_edit.text)) {
            val user = User();
            val userName = user_name_edit.text.toString()
            val secret = secret_edit.text.toString()
            user.userName = userName
            user.secret = secret
            user.publicKey = createPublicKey(userName)
            mSubscriptions.add(
                Rest.createUser(user).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ response ->
                        if (response.errorBody() != null) {
                            onEnrollFail(userName)
                            return@subscribe
                        }
                        onEnrollSuccess(response.body()!!)
                    }, { it ->
                        android.util.Log.e("Alex", it.message, it)
                        onEnrollFail(userName)
                    })
            )
        } else {
            Toast.makeText(context, "User name or secret is empty please check", Toast.LENGTH_LONG).show()
        }
    }

    private fun createPublicKey(userName: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CryptoUtil.createKeyPair(userName)
            CryptoUtil.getPublicKey(userName)
        } else {
            ""
        }
    }

    private fun onEnrollSuccess(user: User) {
        Toast.makeText(context, "User :" + user.userName + "Enroll success", Toast.LENGTH_LONG).show()
    }

    private fun onEnrollFail(userName: String) {
        Toast.makeText(context, "User :" + userName + "Login failed", Toast.LENGTH_LONG).show()
    }

    companion object {
        val TAG = EnrollFragment::class.java.simpleName!!

        public fun crateFragment(): Fragment {
            return EnrollFragment()
        }
    }
}