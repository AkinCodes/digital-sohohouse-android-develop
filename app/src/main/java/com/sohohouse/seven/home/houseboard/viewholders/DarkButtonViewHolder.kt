package com.sohohouse.seven.home.houseboard.viewholders

import android.widget.Button
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.ItemButtonDarkBinding

const val DARK_BUTTON_LAYOUT = R.layout.item_button_dark

class DarkButtonViewHolder(private val binding: ItemButtonDarkBinding) :
    ButtonViewHolder(binding.root) {
    override val button: Button
        get() = binding.button
}