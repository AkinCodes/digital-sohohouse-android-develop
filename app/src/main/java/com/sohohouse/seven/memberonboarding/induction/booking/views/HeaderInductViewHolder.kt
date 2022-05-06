package com.sohohouse.seven.memberonboarding.induction.booking.views

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.databinding.OnboardingIntroHeaderLayoutBinding
import com.sohohouse.seven.memberonboarding.induction.booking.HeaderInductItem
import com.sohohouse.seven.memberonboarding.induction.booking.IntroductionAdapterListener

class HeaderInductViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    companion object {
        const val LAYOUT = R.layout.onboarding_intro_header_layout
    }

    private val binding = OnboardingIntroHeaderLayoutBinding.bind(view)

    fun bind(
        adapterItem: HeaderInductItem,
        introductionAdapterListener: IntroductionAdapterListener
    ) {
        with(binding) {
            introHeaderHouseName.text = adapterItem.name
            introHeaderHouseImage.setImageFromUrl(adapterItem.imageURL)
            with(backButton) {
                if (adapterItem.isPlanner) {
                    setVisible()
                    clicks { introductionAdapterListener.onBackButtonPressed() }
                } else setGone()
            }
        }
    }

}