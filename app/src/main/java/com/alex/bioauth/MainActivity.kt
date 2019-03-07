package com.alex.bioauth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import butterknife.ButterKnife

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        var fragment = EnrollFragment.crateFragment()
        setContainerFragment(fragment, EnrollFragment.TAG, true)

        Rest.init("http://10.0.2.2:8877/auth/")
    }

    fun setContainerFragment(fragment: Fragment?, tag: String, cleanBackStack: Boolean) {
        val fragmentManager = supportFragmentManager
        if (cleanBackStack) {
            val index = fragmentManager.backStackEntryCount
            for (i in index - 1 downTo 0) {
                fragmentManager.popBackStack()
            }
        }
        fragmentManager.beginTransaction()
            .replace(R.id.content, fragment!!, tag)
            .addToBackStack(tag)
            .commit()
    }
}
