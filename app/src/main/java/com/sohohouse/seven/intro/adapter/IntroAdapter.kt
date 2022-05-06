package com.sohohouse.seven.intro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.sohohouse.seven.base.GenericAdapter
import com.sohohouse.seven.databinding.ViewHolderOnboardingIntroductionBinding
import com.sohohouse.seven.databinding.ViewHolderOnboardingWelcomeBinding

class IntroAdapter : GenericAdapter<IntroItem>() {

    var lineCount: Int = 0

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<IntroItem> {
        return when (viewType) {
            ITEM_TYPE_LANDING -> {
                createIntroLandingViewHolder(parent)
            }
            ITEM_TYPE_GUIDELINES -> {
                createIntroGuideViewHolder(parent)
            }
            else -> throw IllegalStateException()
        } as ViewHolder<IntroItem>
    }

    private fun createIntroLandingViewHolder(parent: ViewGroup) = IntroLandingViewHolder(
        ViewHolderOnboardingWelcomeBinding.inflate(
            getLayoutInflater(parent), parent, false
        )
    )

    private fun createIntroGuideViewHolder(parent: ViewGroup) = IntroGuideViewHolder(
        ViewHolderOnboardingIntroductionBinding.inflate(
            getLayoutInflater(parent), parent, false
        )
    )

    private fun getLayoutInflater(parent: ViewGroup) = LayoutInflater.from(
        parent.context
    )

    override fun onBindViewHolder(holder: ViewHolder<IntroItem>, position: Int) {
        super.onBindViewHolder(holder, position)

        (holder as? IntroGuideViewHolder)?.setLineCount(lineCount)
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is IntroLanding -> ITEM_TYPE_LANDING
            else -> ITEM_TYPE_GUIDELINES
        }
    }

    companion object {
        private const val ITEM_TYPE_LANDING = 0
        private const val ITEM_TYPE_GUIDELINES = 1
    }
}