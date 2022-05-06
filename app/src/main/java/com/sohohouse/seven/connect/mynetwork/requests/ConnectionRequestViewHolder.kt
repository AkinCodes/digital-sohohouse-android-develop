package com.sohohouse.seven.connect.mynetwork.requests

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.resources
import com.sohohouse.seven.common.extensions.setTextOrHide
import com.sohohouse.seven.common.utils.DateUtils
import com.sohohouse.seven.common.utils.StringProviderImpl
import com.sohohouse.seven.databinding.ViewHolderConnectionRequestBinding

class ConnectionRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = ViewHolderConnectionRequestBinding.bind(itemView)

    fun bind(
        item: ConnectionRequestItem,
        onClickProfile: (ConnectionRequestItem) -> Unit = {},
        onClickAccept: (ConnectionRequestItem) -> Unit = {},
        onClickIgnore: (ConnectionRequestItem) -> Unit = {}
    ) = with(binding) {
        profileImage.setImageUrl(item.profile.imageUrl)
        title.text = item.profile.fullName
        staffIndication.isVisible = item.profile.isStaff

        subtitle.setTextOrHide(item.profile.occupation)
        message.setTextOrHide(item.message)

        timestamp.text =
            DateUtils.getTimeElapsedLabel(StringProviderImpl(resources), item.createdAt)

        root.setOnClickListener { onClickProfile(item) }
        accept.setOnClickListener { onClickAccept(item) }
        ignore.setOnClickListener { onClickIgnore(item) }
    }

}