package com.sohohouse.seven.common.prefs

import android.content.Context
import android.content.SharedPreferences

class DefaultPrefsStore(context: Context) : BasePrefsStore() {

    companion object {
        private const val FILE_NAME = "PrefsFile"
        private const val TAG = "DefaultPrefs"
    }

    override val sharedPrefs: SharedPreferences
        get() = _sharedPrefs

    override fun clear() {
        _sharedPrefs.edit().clear().apply()
    }

    private val _sharedPrefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    override val tag: String
        get() = TAG
}