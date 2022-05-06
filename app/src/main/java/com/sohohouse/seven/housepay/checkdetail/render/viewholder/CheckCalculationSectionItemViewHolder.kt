package com.sohohouse.seven.housepay.checkdetail.render.viewholder

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.layoutInflater
import com.sohohouse.seven.databinding.ItemCheckCalculationsSectionItemBinding
import com.sohohouse.seven.housepay.checkdetail.CheckDetailValueModel

class CheckCalculationSectionItemViewHolder(
    private val binding: ItemCheckCalculationsSectionItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(parent: ViewGroup): CheckCalculationSectionItemViewHolder {
            return CheckCalculationSectionItemViewHolder(
                ItemCheckCalculationsSectionItemBinding.inflate(
                    parent.layoutInflater(),
                    parent,
                    false
                )
            )
        }
    }

    fun bind(item: CheckDetailValueModel) {
        with(binding) {
            name.text = item.label
            amount.text = item.amount
        }
    }

}