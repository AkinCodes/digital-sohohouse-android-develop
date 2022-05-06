package com.sohohouse.seven.common.views

import android.os.SystemClock
import android.view.View

class ThrottledClickListener(
    private val onClicked: (View) -> Unit,
    private var defaultInterval: Int = 1000
) : View.OnClickListener {

    private var lastTimeClicked: Long = 0

    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) return

        lastTimeClicked = SystemClock.elapsedRealtime()

        onClicked(v)
    }

}