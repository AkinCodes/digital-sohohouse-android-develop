package com.sohohouse.seven.common.utils

import android.content.res.Resources
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes

interface StringProvider {
    fun getString(@StringRes resId: Int, vararg params: String): String
    fun getString(@StringRes resId: Int?): String
    fun getStringArray(@ArrayRes resId: Int): Array<String>
}

class StringProviderImpl(private val resources: Resources) : StringProvider {
    override fun getString(@StringRes resId: Int?): String {
        return if (resId != null)
            resources.getString(resId)
        else ""
    }

    override fun getString(resId: Int, vararg params: String): String {
        return resources.getString(resId, *params)
    }

    override fun getStringArray(resId: Int): Array<String> {
        return resources.getStringArray(resId)
    }
}