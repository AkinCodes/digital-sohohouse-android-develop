package com.sohohouse.seven.home.houseboard.viewholders

import android.widget.Button
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.ItemButtonSecondaryBinding

const val SECONDARY_BUTTON_LAYOUT = R.layout.item_button_secondary

class SecondaryButtonViewHolder(private val binding: ItemButtonSecondaryBinding) :
    ButtonViewHolder(binding.root) {

    override val button: Button
        get() = binding.button
}