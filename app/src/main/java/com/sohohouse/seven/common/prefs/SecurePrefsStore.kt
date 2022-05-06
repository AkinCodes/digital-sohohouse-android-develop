package com.sohohouse.seven.common.prefs

import android.content.Context
import android.content.SharedPreferences
import devliving.online.securedpreferencestore.DefaultRecoveryHandler
import devliving.online.securedpreferencestore.SecuredPreferenceStore

class SecurePrefsStore constructor(context: Context) : BasePrefsStore() {

    private val _sharedPrefs: SecuredPreferenceStore

    companion object {
        private const val SECURE_PREF_FILE_NAME = "SPS_file"
        private const val TAG = "SecurePrefs"
    }

    init {
        SecuredPreferenceStore.init(
            context.applicationContext,
            SECURE_PREF_FILE_NAME,
            null,
            null,
            DefaultRecoveryHandler()
        )
        _sharedPrefs = SecuredPreferenceStore.getSharedInstance()
    }

    override fun clear() {
        _sharedPrefs.edit().clear().apply()
    }

    override val sharedPrefs: SharedPreferences
        get() = _sharedPrefs

    override val tag: String
        get() = TAG
}