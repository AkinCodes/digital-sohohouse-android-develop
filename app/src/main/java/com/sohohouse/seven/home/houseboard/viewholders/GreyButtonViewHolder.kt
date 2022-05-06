package com.sohohouse.seven.home.houseboard.viewholders

import android.widget.Button
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.ItemButtonGreyBinding

const val GREY_BUTTON_LAYOUT = R.layout.item_button_grey

class GreyButtonViewHolder(private val binding: ItemButtonGreyBinding) :
    ButtonViewHolder(binding.root) {
    override val button: Button
        get() = binding.button
}