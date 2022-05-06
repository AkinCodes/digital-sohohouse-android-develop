package com.sohohouse.seven.perks.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.sohohouse.seven.base.GenericAdapter
import com.sohohouse.seven.databinding.PerksDetailBodyLayoutBinding
import com.sohohouse.seven.databinding.PerksDetailHeaderCardLayoutBinding
import com.sohohouse.seven.databinding.ViewHolderPerksDetailHeaderImageBinding
import com.sohohouse.seven.databinding.ViewHolderPerksDetailMorePerksBinding

class PerksDetailAdapter : GenericAdapter<PerksDetailItem>() {

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<PerksDetailItem> {
        return when (viewType) {
            PerksDetailItem.ITEM_TYPE_HEADER_IMAGE -> {
                PerksDetailHeaderImageViewHolder(
                    ViewHolderPerksDetailHeaderImageBinding.inflate(
                        getLayoutInflater(parent), parent, false
                    )
                )
            }
            PerksDetailItem.ITEM_TYPE_HEADER -> {
                PerksDetailHeaderViewHolder(
                    PerksDetailHeaderCardLayoutBinding.inflate(
                        getLayoutInflater(parent), parent, false
                    )
                )
            }
            PerksDetailItem.ITEM_TYPE_BODY -> {
                PerksDetailBodyViewHolder(
                    PerksDetailBodyLayoutBinding.inflate(
                        getLayoutInflater(parent), parent, false
                    )
                )
            }
            PerksDetailItem.ITEM_TYPE_MORE_PERKS -> {
                MorePerksViewHolder(
                    ViewHolderPerksDetailMorePerksBinding.inflate(
                        getLayoutInflater(parent), parent, false
                    )
                )
            }
            else -> throw IllegalStateException("Unknown viewType of $viewType")
        } as ViewHolder<PerksDetailItem>
    }

    private fun getLayoutInflater(parent: ViewGroup) = LayoutInflater.from(parent.context)

    override fun getItemViewType(position: Int) = items[position].itemType

}