package com.sohohouse.seven.common.design.textblock

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.databinding.TextBlockSmallHeaderBinding

class SmallTextBlockViewHolder(private val binding: TextBlockSmallHeaderBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: TextBlock) {
        binding.label.setText(item.titleRes)
    }
}