package com.sohohouse.seven.perks.details.adapter

import com.sohohouse.seven.R
import com.sohohouse.seven.base.GenericAdapter.ViewHolder
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.databinding.ViewHolderPerksDetailHeaderImageBinding

class PerksDetailHeaderImageViewHolder(private val binding: ViewHolderPerksDetailHeaderImageBinding) :
    ViewHolder<PerksDetailHeaderImage>(binding.root) {

    override fun bind(item: PerksDetailHeaderImage) {
        binding.image.setImageFromUrl(item.imageUrl, R.drawable.placeholder)
    }
}