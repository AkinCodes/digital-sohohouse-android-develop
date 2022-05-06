package com.sohohouse.seven.connect.filter.adapter

import android.view.View
import com.sohohouse.seven.R
import com.sohohouse.seven.base.GenericAdapter
import com.sohohouse.seven.connect.filter.base.FilterItem
import com.sohohouse.seven.databinding.ViewHolderListFilterItemBinding

class FilterItemViewHolder(
    itemView: View,
    private val listener: (FilterItem) -> Unit
) : GenericAdapter.ViewHolder<FilterItem>(itemView) {

    private val binding = ViewHolderListFilterItemBinding.bind(itemView)

    override fun bind(item: FilterItem) = binding.run {
        pillView.label = item.title
        pillView.isEnabled = item.enabled
        pillView.isActivated = item.selected
        pillView.setActionButton(if (item.removable) R.drawable.ic_close else 0)

        root.setOnClickListener {
            item.selected = item.selected.not()
            pillView.isActivated = item.selected

            listener(item)
        }
    }
}