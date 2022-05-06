package com.sohohouse.seven.common.views

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import com.sohohouse.seven.R

class RoundRectOutlineProvider : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
        val radius = view.resources.getDimensionPixelOffset(R.dimen.home_content_corner_radius)
        outline.setRoundRect(0, 0, view.width, (view.height + radius), radius.toFloat())
    }
}