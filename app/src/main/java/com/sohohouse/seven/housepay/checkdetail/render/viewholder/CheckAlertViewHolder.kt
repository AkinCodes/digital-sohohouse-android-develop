package com.sohohouse.seven.housepay.checkdetail.render.viewholder

import android.text.method.LinkMovementMethod
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.layoutInflater
import com.sohohouse.seven.databinding.ItemHouseCreditAlertBinding
import com.sohohouse.seven.housepay.checkdetail.HouseCreditAlertInfo

class CheckAlertViewHolder(
    private val binding: ItemHouseCreditAlertBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(parent: ViewGroup): CheckAlertViewHolder {
            return CheckAlertViewHolder(
                ItemHouseCreditAlertBinding.inflate(
                    parent.layoutInflater(),
                    parent,
                    false
                )
            )
        }
    }

    private var item: HouseCreditAlertInfo? = null

    init {
        binding.housepayAlertCta.clicks {
            item?.let { it.onClick() }
        }
        binding.housepayAlertText.movementMethod = LinkMovementMethod.getInstance();
    }

    fun bind(item: HouseCreditAlertInfo) {
        this.item = item
        binding.housepayAlertText.text = item.message
        binding.housepayAlertCta.text = item.cta
    }
}
