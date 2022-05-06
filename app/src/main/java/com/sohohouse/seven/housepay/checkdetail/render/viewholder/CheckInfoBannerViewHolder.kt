package com.sohohouse.seven.housepay.checkdetail.render.viewholder

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.layoutInflater
import com.sohohouse.seven.databinding.ItemCheckInfoBannerBinding
import com.sohohouse.seven.housepay.checkdetail.CheckBannerInfo

class CheckInfoBannerViewHolder(
    private val binding: ItemCheckInfoBannerBinding
) : RecyclerView.ViewHolder(
    binding.root
) {

    companion object {
        fun create(parent: ViewGroup): CheckInfoBannerViewHolder {
            return CheckInfoBannerViewHolder(
                ItemCheckInfoBannerBinding.inflate(
                    parent.layoutInflater(),
                    parent,
                    false
                )
            )
        }
    }

    init {
        itemView.clicks {
            item?.onClick?.invoke()
        }
    }

    private var item: CheckBannerInfo? = null

    fun bind(item: CheckBannerInfo) {
        this.item = item
        with(binding) {
            title.text = item.title
            subtitle.text = item.subtitle
        }
    }

}