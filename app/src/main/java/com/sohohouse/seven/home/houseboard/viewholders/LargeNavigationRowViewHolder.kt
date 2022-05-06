package com.sohohouse.seven.home.houseboard.viewholders

import android.widget.TextView
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.ItemLargeNavigationRowBinding

const val LARGE_NAVIGATION_ROW_LAYOUT = R.layout.item_large_navigation_row

class LargeNavigationRowViewHolder(private val binding: ItemLargeNavigationRowBinding) :
    NavigationRowViewHolderBase(binding.root) {
    override val textView: TextView
        get() = binding.text
}