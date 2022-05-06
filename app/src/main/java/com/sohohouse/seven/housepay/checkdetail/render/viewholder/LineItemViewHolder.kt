package com.sohohouse.seven.housepay.checkdetail.render.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.utils.CurrencyUtils
import com.sohohouse.seven.databinding.TabLineItemBinding
import com.sohohouse.seven.network.core.models.housepay.LineItemDTO

class LineItemViewHolder private constructor(private val binding: TabLineItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(parent: ViewGroup): LineItemViewHolder {
            return LineItemViewHolder(
                TabLineItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    fun bind(lineItem: LineItemDTO) {
        with(binding) {
            quantity.text = lineItem.quantity?.toString()
            name.text = lineItem.name
            val cents = lineItem.cents?.times(lineItem.quantity ?: 0) ?: 0
            price.text = CurrencyUtils.getFormattedPrice(cents, showCurrency = false)
        }
    }
}

