package com.alex.bioauth

import android.view.View
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import butterknife.Unbinder
import rx.subscriptions.CompositeSubscription

open class BaseFragment : Fragment() {

    protected var mSubscriptions = CompositeSubscription()
    protected var unbinder: Unbinder? = null

    protected fun bindView(view: View) {
        unbinder = ButterKnife.bind(this, view)
    }

    fun getMainActivity(): MainActivity {
        return activity as MainActivity
    }

    override fun onDestroy() {
        super.onDestroy()
        mSubscriptions.clear()
        if (unbinder != null) {
            unbinder!!.unbind()
        }
    }
}