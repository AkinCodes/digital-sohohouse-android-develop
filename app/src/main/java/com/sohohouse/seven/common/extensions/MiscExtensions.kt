package com.sohohouse.seven.common.extensions

import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.AttrRes

fun Triple<List<*>, List<*>, List<*>>.isEmpty() =
    first.isEmpty() && second.isEmpty() && third.isEmpty()

fun Resources.Theme.getAttributeColor(@AttrRes resId: Int): Int {
    val typedValue = TypedValue()
    resolveAttribute(resId, typedValue, true)
    return typedValue.data
}