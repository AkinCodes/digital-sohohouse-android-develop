package com.sohohouse.seven.profile.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.getAttributeColor
import com.sohohouse.seven.common.extensions.setText
import com.sohohouse.seven.databinding.ViewHolderListItemBinding
import com.sohohouse.seven.profile.view.model.ProfileAction

class ConnectActionViewHolder(private val binding: ViewHolderListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(action: ProfileAction, onClick: (ProfileAction) -> Unit) {
        binding.title.setText(action.title, getAttributeColor(action.color))
        binding.root.setOnClickListener { onClick(action) }
    }
}