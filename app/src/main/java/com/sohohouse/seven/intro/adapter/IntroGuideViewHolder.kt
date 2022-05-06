package com.sohohouse.seven.intro.adapter

import com.sohohouse.seven.base.GenericAdapter.ViewHolder
import com.sohohouse.seven.databinding.ViewHolderOnboardingIntroductionBinding

class IntroGuideViewHolder(private val binding: ViewHolderOnboardingIntroductionBinding) :
    ViewHolder<IntroGuide>(binding.root) {

    override fun bind(item: IntroGuide) {
        with(binding) {
            header.setText(item.header)
            description.setText(item.description)
            image.setImageResource(item.image ?: return)
        }
    }

    fun setLineCount(lineCount: Int) {
        binding.description.setLines(lineCount)
    }
}
