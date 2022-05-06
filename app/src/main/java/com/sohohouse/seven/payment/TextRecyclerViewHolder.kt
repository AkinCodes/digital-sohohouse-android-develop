package com.sohohouse.seven.payment

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.getString
import com.sohohouse.seven.databinding.ListPaymentDescriptionItemBinding

class TextRecyclerViewHolder(private val binding: ListPaymentDescriptionItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(description: Int) {
        binding.text.text = getString(description)
    }

}