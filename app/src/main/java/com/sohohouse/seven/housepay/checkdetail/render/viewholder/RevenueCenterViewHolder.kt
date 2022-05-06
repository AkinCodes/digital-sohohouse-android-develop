package com.sohohouse.seven.housepay.checkdetail.render.viewholder

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.layoutInflater
import com.sohohouse.seven.databinding.ItemRevenueCenterBinding

class RevenueCenterViewHolder(
    private val binding: ItemRevenueCenterBinding
) : RecyclerView.ViewHolder(
    binding.root
) {

    companion object {
        fun create(parent: ViewGroup): RevenueCenterViewHolder {
            return RevenueCenterViewHolder(
                ItemRevenueCenterBinding.inflate(parent.layoutInflater())
            )
        }
    }

    fun bind(name: String) {
        binding.revenueCenter.text = name
    }
}