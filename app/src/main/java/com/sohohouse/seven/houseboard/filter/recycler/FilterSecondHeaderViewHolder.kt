package com.sohohouse.seven.houseboard.filter.recycler

import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.getString
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.databinding.FilterSecondHeaderBinding

class FilterSecondHeaderViewHolder(private val binding: FilterSecondHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(@StringRes headerStringRes: Int?) = with(binding) {
        if (headerStringRes != null) {
            text.setVisible()
            text.text = getString(headerStringRes)
        } else {
            text.setGone()
        }
    }
}