package com.sohohouse.seven.common.viewholders

import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.databinding.GuestListViewHolderStickyHeaderBinding

class DateHeaderViewHolder(private val binding: GuestListViewHolderStickyHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(@StringRes resId: Int) {
        binding.title.setText(resId)
    }

    fun bind(text: String?) {
        binding.title.text = text
    }

    companion object {
        const val HEADER_TYPE_TODAY = 1
        const val HEADER_TYPE_THIS_WEEK = 2
        const val HEADER_TYPE_NEXT_WEEK = 3
        const val HEADER_TYPE_IN_FUTURE = 4
    }

}