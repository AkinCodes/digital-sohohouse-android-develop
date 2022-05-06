package com.sohohouse.seven.common.extensions

import androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.Behavior

fun AppBarLayout.isAppbarExpanded(): Boolean {
    val behavior = (layoutParams as? LayoutParams)?.behavior
    return if (behavior is Behavior) behavior.topAndBottomOffset != 0 else false
}