package com.sohohouse.seven.common.views.locationlist

import android.widget.ImageView
import android.widget.TextView
import com.sohohouse.seven.databinding.ItemLocationPickerRegionBinding

open class LargeParentRecyclerViewHolder(private val binding: ItemLocationPickerRegionBinding) :
    ParentViewHolder(binding.root) {

    override val header: TextView
        get() = binding.buttonText

    override val icon: ImageView
        get() = binding.icon
}