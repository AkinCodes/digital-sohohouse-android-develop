package com.sohohouse.seven.common.views.categorylist

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.getString
import com.sohohouse.seven.databinding.ListFilterHeaderItemBinding

class CategoryTextRecyclerViewHolder(private val binding: ListFilterHeaderItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(model: CategoryAdapterTextItem) {
        binding.title.text = getString(model.stringRes)
    }
}