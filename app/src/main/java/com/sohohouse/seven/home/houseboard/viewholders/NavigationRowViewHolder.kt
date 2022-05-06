package com.sohohouse.seven.home.houseboard.viewholders

import android.widget.TextView
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.ItemNavigationRowBinding

const val MEDIUM_NAVIGATION_ROW_LAYOUT = R.layout.item_navigation_row

class NavigationRowViewHolder(private val binding: ItemNavigationRowBinding) :
    NavigationRowViewHolderBase(binding.root) {

    override val textView: TextView
        get() = binding.text
}