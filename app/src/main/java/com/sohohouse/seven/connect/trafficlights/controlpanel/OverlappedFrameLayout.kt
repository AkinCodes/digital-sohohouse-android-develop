package com.sohohouse.seven.connect.trafficlights.controlpanel

import android.content.Context
import android.content.res.ColorStateList
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.getAttributeColor

class OverlappedFrameLayout(
    context: Context
) : FrameLayout(context) {

    init {
        layoutParams = MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        setBackgroundResource(R.drawable.bkg_oval)
        backgroundTintList =
            ColorStateList.valueOf(getAttributeColor(R.attr.colorBackgroundPrimary))
        setPadding(
            resources.getDimensionPixelOffset(R.dimen.dp_6),
            0,
            resources.getDimensionPixelOffset(R.dimen.dp_6),
            0
        )
    }

    constructor(
        currentIndex: Int,
        totalCount: Int,
        context: Context
    ) : this(context) {
        elevation = (totalCount - currentIndex).toFloat()
        if (currentIndex > 0) {
            updateLayoutParams<MarginLayoutParams> {
                marginStart = -resources.getDimensionPixelOffset(R.dimen.dp_13)
            }
        }
    }

}