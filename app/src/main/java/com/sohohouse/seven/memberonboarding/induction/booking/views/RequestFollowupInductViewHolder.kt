package com.sohohouse.seven.memberonboarding.induction.booking.views

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.databinding.OnboardingIntroRequestFollowupLayoutBinding

interface RequestFollowupListener {
    fun requestFollowupClicked()
}

class RequestFollowupIntroViewHolder(private val binding: OnboardingIntroRequestFollowupLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(listener: RequestFollowupListener) {
        binding.followupContact.clicks { listener.requestFollowupClicked() }
    }

}