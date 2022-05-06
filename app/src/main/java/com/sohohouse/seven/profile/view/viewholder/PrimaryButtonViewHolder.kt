package com.sohohouse.seven.profile.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.databinding.ViewHolderPrimaryButtonBinding
import com.sohohouse.seven.profile.view.model.Button

class PrimaryButtonViewHolder(private val binding: ViewHolderPrimaryButtonBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(button: Button, onClick: (Button) -> Unit) {
        with(binding.button) {
            setOnClickListener { onClick(button) }
            setText(button.buttonTitle)
            isEnabled = button.buttonEnabled
            setVisible(button.buttonVisible)
        }
    }

}