package com.sohohouse.seven.housepay.checkdetail.render.viewholder

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.layoutInflater
import com.sohohouse.seven.databinding.ItemCheckOrderTotalBinding
import com.sohohouse.seven.housepay.checkdetail.CheckDetailValueModel
import com.sohohouse.seven.housepay.checkdetail.CheckItem

class OrderTotalViewHolder(
    private val binding: ItemCheckOrderTotalBinding
) : RecyclerView.ViewHolder(
    binding.root
) {
    companion object {
        fun create(parent: ViewGroup): OrderTotalViewHolder {
            return OrderTotalViewHolder(
                ItemCheckOrderTotalBinding.inflate(
                    parent.layoutInflater(),
                    parent,
                    false
                )
            )
        }
    }

    fun bind(item: CheckDetailValueModel) {
        binding.orderTotalLabel.text = item.label
        binding.amount.text = item.amount
    }

}