package com.sohohouse.seven.houseboard.list.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.databinding.HouseBoardPostNoteLayoutBinding

class HouseBoardPostNoteViewHolder(val binding: HouseBoardPostNoteLayoutBinding) : RecyclerView.ViewHolder(binding.root)

fun HouseBoardPostNoteViewHolder.bindOnboardingData(imgRes: Int) {
    binding.profileImage.setImageResource(imgRes)
}