package com.sohohouse.seven.houseboard.list.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.getString
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.databinding.HouseBoardCheckInComponentBinding

class HouseBoardCheckInViewHolder(val binding: HouseBoardCheckInComponentBinding) :
    RecyclerView.ViewHolder(binding.root) {
    init {
        binding.postAuthorImage.clipToOutline = true
    }

    private fun setAuthorName(name: String) {
        binding.postAuthorName.text = name
    }

    private fun setPostContent(content: String) {
        binding.postContent.text = content
    }

    private fun setUserImage(imageUrl: String) {
        binding.postAuthorImage.setImageFromUrl(imageUrl, R.drawable.ic_profile, true)
    }

}

fun HouseBoardCheckInViewHolder.bindOnboardingData(
    imgRes: Int,
    nameLabel: Int,
    timeLabel: Int,
    content: Int
) = with(binding) {
    postAuthorImage.setImageResource(imgRes)
    postAuthorName.text = getString(nameLabel)
    checkInTime.text = getString(timeLabel)
    postContent.text = getString(content)
}