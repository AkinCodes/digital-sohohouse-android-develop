package com.sohohouse.seven.housepay.checkdetail.render.viewholder

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.layoutInflater
import com.sohohouse.seven.common.extensions.setTextOrHide
import com.sohohouse.seven.databinding.ItemCheckNotificationBannerBinding
import com.sohohouse.seven.housepay.checkdetail.NotificationBannerItem

class NotificationBannerViewHolder(
    private val binding: ItemCheckNotificationBannerBinding
) : RecyclerView.ViewHolder(
    binding.root
) {

    companion object {
        fun create(parent: ViewGroup): NotificationBannerViewHolder {
            return NotificationBannerViewHolder(
                ItemCheckNotificationBannerBinding.inflate(
                    parent.layoutInflater(),
                    parent,
                    false
                )
            )
        }
    }

    fun bind(item: NotificationBannerItem) {
        binding.checkBannerTitle.setTextOrHide(item.title)
        binding.checkBannerMessage.text = item.message
        binding.checkBannerCta.text = item.cta
        binding.checkBannerCta.clicks {
            item.action()
        }
    }

}