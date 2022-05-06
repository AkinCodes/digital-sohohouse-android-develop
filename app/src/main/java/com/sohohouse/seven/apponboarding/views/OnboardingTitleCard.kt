package com.sohohouse.seven.apponboarding.views

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.layoutInflater
import com.sohohouse.seven.databinding.OnboardingTitleCardLayoutBinding

class OnboardingTitleCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs, R.attr.onboardingCardStyle) {

    val binding = OnboardingTitleCardLayoutBinding.inflate(layoutInflater(), this)

    fun setup(@StringRes titleRes: Int, @StringRes subTitleRes: Int) {
        with(binding) {
            titleCardTitle.text = context.getString(titleRes)
            titleCardSubtitle.text = context.getString(subTitleRes)
        }
    }

}