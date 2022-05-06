package com.sohohouse.seven.intro

import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2

class PageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        if (page !is ViewGroup) return

        val target = (page as? ViewGroup)?.getChildAt(1) ?: return
        val pageWidth = target.width

        when {
            position < -1 -> { // [-Infinity,-1] This page is way off-screen to the left.
                target.alpha = 0f
            }
            position <= 0 -> { // [-1,0] Use the default slide transition when moving to the left page
                target.alpha = 1 + position
                target.translationX = pageWidth * -position
            }
            position <= 1 -> { // (0,1] Fade the page out.
                target.alpha = 1 - position
                target.translationX = pageWidth * -position
            }
            else -> { // (1,+Infinity]
                target.alpha = 0f
            }
        }
    }
}