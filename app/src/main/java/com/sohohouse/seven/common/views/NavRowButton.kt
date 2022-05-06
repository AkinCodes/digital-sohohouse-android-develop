package com.sohohouse.seven.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.databinding.NavRowLayoutBinding

class NavRowButton @JvmOverloads constructor(con: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(con, attrs, R.attr.navRowButtonStyle) {

    private val binding = NavRowLayoutBinding.inflate(LayoutInflater.from(con), this)

    fun setup(
        @DrawableRes drawableRes: Int?, @StringRes stringRes: Int?,
        hideTopDivider: Boolean = false, hideBottomDivider: Boolean = false
    ) = with(binding) {
        if (drawableRes != null && drawableRes != 0) {
            icon.setImageResource(drawableRes)
        } else {
            icon.visibility = GONE
        }
        stringRes?.let { label.setText(it) }

        topDivider.setVisible(!hideTopDivider)
        bottomDivider.setVisible(!hideBottomDivider)
    }

    fun setClickListener(onNext: (Any) -> Unit) {
        this.clicks(onNext)
    }
}