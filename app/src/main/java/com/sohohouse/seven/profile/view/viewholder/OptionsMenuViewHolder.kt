package com.sohohouse.seven.profile.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.databinding.ViewHolderOptionsMenuBinding
import com.sohohouse.seven.profile.view.model.Button

class OptionsMenuViewHolder(private val binding: ViewHolderOptionsMenuBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(button: Button, onClick: (Button) -> Unit) {
        binding.root.setOnClickListener { onClick(button) }
    }
}