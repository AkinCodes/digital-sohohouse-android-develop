package com.sohohouse.seven.common.views

import android.content.Context
import android.graphics.drawable.ColorDrawable
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import androidx.core.view.animation.PathInterpolatorCompat
import android.util.AttributeSet
import com.sohohouse.seven.R

class CustomCollapsingToolbarLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CollapsingToolbarLayout(context, attrs, defStyleAttr) {

    init {
        this.contentScrim = ColorDrawable(context.getColor(R.color.cod_gray))
        this.scrimAnimationDuration = 150
        this.isTitleEnabled = false
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val params = layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
                AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED or
                AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
        params.scrollInterpolator = PathInterpolatorCompat.create(
            0.9f, 0.6f, 0.7f, 0.8f
        )
    }
}