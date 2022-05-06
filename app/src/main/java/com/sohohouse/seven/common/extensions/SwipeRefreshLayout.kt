package com.sohohouse.seven.common.extensions

import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sohohouse.seven.R

fun SwipeRefreshLayout.applyColorScheme() {
    this.setColorSchemeResources(R.color.black)
    this.getChildAt(0)
        ?.setBackgroundColor(ContextCompat.getColor(this.context, R.color.white))
}