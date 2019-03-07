package com.alex.bioauth

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Preference.getInstance(applicationContext)
    }
}