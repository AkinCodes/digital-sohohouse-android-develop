package com.sohohouse.seven.perks.details.adapter

import com.sohohouse.seven.R
import com.sohohouse.seven.base.GenericAdapter.ViewHolder
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.common.extensions.setLinkableHtml
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.databinding.PerksDetailBodyLayoutBinding

class PerksDetailBodyViewHolder(private val binding: PerksDetailBodyLayoutBinding) :
    ViewHolder<PerksDetailBody>(binding.root) {

    override fun bind(item: PerksDetailBody) {
        val body = item.bodyItem

        with(binding) {
            bodyTextBlock.setLinkableHtml(body.textBlock)
            bodyTextBlock.setVisible(body.textBlock.isNotEmpty())

            bodyTitle.text = body.bodyImageTitle
            bodyTitle.setVisible(body.bodyImageTitle.isNotEmpty())

            bodyImage.setImageFromUrl(
                url = body.bodyImageUrlLargePng,
                isCenterCrop = false,
                placeholder = R.drawable.placeholder
            )
            bodyImage.setVisible(body.bodyImageUrlLargePng?.isNotEmpty() == true)

            bodyImageCaption.text = body.bodyImageCaption
            bodyImageCaption.setVisible(body.bodyImageCaption.isNotEmpty())

            bodyPullquote.text = body.bodyPullQuote
            bodyPullquote.setVisible(body.bodyPullQuote.isNotEmpty())
        }
    }

}