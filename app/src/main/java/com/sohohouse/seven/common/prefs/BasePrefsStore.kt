package com.sohohouse.seven.common.prefs

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

abstract class BasePrefsStore {

    companion object {
        private const val DEFAULT_VALUE_STRING = ""
        private const val DEFAULT_VALUE_INT = -1

        private inline fun <reified T> Gson.fromJson(json: String) =
            this.fromJson<T>(json, object : TypeToken<T>() {}.type)
    }

    protected abstract val sharedPrefs: SharedPreferences
    protected abstract val tag: String

    private val gson = Gson()

    val isEmpty: Boolean
        get() = sharedPrefs.all.isEmpty()

    fun contains(key: String): Boolean {
        return sharedPrefs.contains(getTaggedKey(key))
    }

    fun getBooleanIfContains(key: String): Boolean? {
        return if (contains(key)) {
            getBoolean(key)
        } else {
            null
        }
    }

    fun getString(key: String): String {
        return sharedPrefs.getString(getTaggedKey(key), DEFAULT_VALUE_STRING)
            ?: DEFAULT_VALUE_STRING
    }

    fun putString(key: String, value: String) {
        sharedPrefs.edit().putString(getTaggedKey(key), value).apply()
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPrefs.getInt(getTaggedKey(key), defaultValue)
    }

    fun putInt(key: String, value: Int) {
        sharedPrefs.edit().putInt(getTaggedKey(key), value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPrefs.getBoolean(getTaggedKey(key), defaultValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        sharedPrefs.edit().putBoolean(getTaggedKey(key), value).apply()
    }

    fun getStringList(key: String): List<String>? {
        return gson.fromJson(
            sharedPrefs.getString(getTaggedKey(key), DEFAULT_VALUE_STRING)
                ?: DEFAULT_VALUE_STRING
        )
    }

    fun putStringList(key: String, value: List<String>?) {
        sharedPrefs.edit().putString(getTaggedKey(key), gson.toJson(value)).apply()
    }

    fun remove(key: String) {
        sharedPrefs.edit().remove(getTaggedKey(key)).apply()
    }

    private fun getTaggedKey(key: String) = "$tag.$key"

    abstract fun clear()

}