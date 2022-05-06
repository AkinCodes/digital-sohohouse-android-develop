package com.sohohouse.seven.common.extensions

import android.os.Bundle
import androidx.core.os.bundleOf
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.connect.filter.base.Filter
import java.lang.StringBuilder

fun Bundle.print(): String {
    return StringBuilder().apply {
        keySet().forEach {
            append(get(it))
        }
    }.toString()
}

inline fun <reified T> Bundle.getParcelableTypedArray(key: String): Array<T>? {
    return getParcelableArray(key)
        ?.map { it as T }
        ?.toTypedArray()
}

fun Bundle.toMap(): Map<String, Any?> {
    return HashMap<String, Any?>().apply {
        this@toMap.keySet().forEach {
            put(it, this@toMap.get(it))
        }
    }
}

inline fun <reified T> Map<String, Any?>.getAs(key: String): T? {
    return get(key) as? T?
}