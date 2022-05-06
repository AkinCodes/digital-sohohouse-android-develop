package com.sohohouse.seven.connect.mynetwork.connections

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.setTextOrHide
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.databinding.ViewHolderProfileItemBinding

class ProfileItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = ViewHolderProfileItemBinding.bind(itemView)

    fun bind(
        item: ProfileItem,
        onClick: (ProfileItem) -> Unit,
        onMessageClick: (ProfileItem) -> Unit
    ) = with(binding) {
        item.imageUrl?.let {
            profileImage.setImageUrl(it)
        }
        title.text = item.fullName
        staffIndication.isVisible = item.isStaff
        subtitle.setTextOrHide(arrayOf(item.occupation, item.location)
            .filterNot { it.isNullOrEmpty() }
            .joinToString(","))
        root.setOnClickListener { onClick(item) }
        buttonMessage.setOnClickListener { onMessageClick(item) }
    }
}