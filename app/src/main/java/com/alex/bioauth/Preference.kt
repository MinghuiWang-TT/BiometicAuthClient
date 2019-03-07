package com.alex.bioauth

import android.content.Context
import android.content.SharedPreferences

class Preference {

    var pref: SharedPreferences? = null

    constructor(context: Context) {
        pref = context.getSharedPreferences(
            context.packageName,
            Context.MODE_PRIVATE
        )
    }

    fun get(key: String, default: String): String? {
        return pref!!.getString(key, default)
    }

    fun put(key: String, value: String) {
        pref!!.edit().putString(key, value).commit()
    }


    fun remove(key: String) {
        pref!!.edit().remove(key).commit()
    }

    companion object {
        private var preference: Preference? = null

        fun getInstance(context: Context): Preference? {
            if (preference == null) {
                preference = Preference(context)
            }
            return preference
        }
    }
}