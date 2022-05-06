package com.sohohouse.seven.common.views.locationlist

import android.widget.ImageView
import android.widget.TextView
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.ListFilterParentItemBinding

open class ParentRecyclerViewHolder(private val binding: ListFilterParentItemBinding) :
    ParentViewHolder(binding.root) {

    override val header: TextView
        get() = binding.title

    override val icon: ImageView
        get() = binding.icon
}