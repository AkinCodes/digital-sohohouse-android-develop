package com.sohohouse.seven.common.utils

class EmptyStringProvider : StringProvider {
    override fun getString(resId: Int, vararg params: String): String {
        return ""
    }

    override fun getString(resId: Int?): String {
        return ""
    }

    override fun getStringArray(resId: Int): Array<String> {
        return emptyArray()
    }
}