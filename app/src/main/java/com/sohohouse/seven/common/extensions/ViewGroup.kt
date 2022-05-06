package com.sohohouse.seven.common.extensions

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.view.forEach

fun ViewGroup.setDirectChildrenEnabled(enable: Boolean, changeAlpha: Boolean) {
    for (i in 0 until childCount) {
        val child: View = getChildAt(i)
        child.setEnabledWithAlpha(enable, changeAlpha)
    }
}

fun ViewGroup.setDeepChildrenEnabled(enable: Boolean, changeAlpha: Boolean) {
    if (changeAlpha) {
        this.alpha = if (enable) 1f else 0.5f
    }
    deepForEach {
        isEnabled = enable
    }
}

fun ViewGroup.deepForEach(function: View.() -> Unit) {
    this.forEach { child ->
        child.function()
        if (child is ViewGroup) {
            child.deepForEach(function)
        }
    }
}

fun ViewGroup.inflateLayout(@LayoutRes resId: Int): View {
    return layoutInflater().inflate(resId, this, false)
}