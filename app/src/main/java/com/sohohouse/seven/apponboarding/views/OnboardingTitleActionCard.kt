package com.sohohouse.seven.apponboarding.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.layoutInflater
import com.sohohouse.seven.databinding.OnboardingTitleCardActionLayoutBinding

class OnboardingTitleActionCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs, R.attr.onboardingCardStyle) {

    val binding = OnboardingTitleCardActionLayoutBinding.inflate(layoutInflater(), this)

    fun setup(
        @StringRes titleRes: Int,
        @StringRes subTitleRes: Int,
        @StringRes buttonTextRes: Int,
        @StringRes dismissTextRes: Int? = null
    ) {
        with(binding) {
            titleCardTitle.text = context.getString(titleRes)
            titleCardSubtitle.text = context.getString(subTitleRes)
            titleCardNextButton.text = context.getString(buttonTextRes)

            titleCardDismissButton.visibility = dismissTextRes?.let {
                titleCardDismissButton.text = context.getString(it)
                View.VISIBLE
            } ?: View.GONE

        }
    }

    fun clicks(onNext: (Any) -> Unit, onDismiss: ((Any) -> Unit)? = null) {
        with(binding.titleCardNextButton) {
            clicks(onNext)
            onDismiss?.let {
                clicks(it)
            }
        }
    }
}