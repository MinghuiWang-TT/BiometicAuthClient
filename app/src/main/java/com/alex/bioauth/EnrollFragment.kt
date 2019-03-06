package com.alex.bioauth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Optional
import butterknife.Unbinder


class EnrollFragment : Fragment() {

    var unbinder: Unbinder? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.enroll_fragment, container, false)
        unbinder = ButterKnife.bind(this, rootView)
        return rootView
    }

    override fun onDestroy() {
        super.onDestroy()
        if (unbinder != null) {
            unbinder!!.unbind()
        }
    }

    @Optional
    @OnClick(R.id.btn_enroll)
    fun onBtnClicked() {
    }

    companion object {
        val TAG = EnrollFragment::class.java.simpleName

        public fun crateFragment(): Fragment {
            return EnrollFragment()
        }
    }
}