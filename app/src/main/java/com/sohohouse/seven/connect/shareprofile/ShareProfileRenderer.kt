package com.sohohouse.seven.connect.shareprofile

import android.view.LayoutInflater
import android.view.ViewGroup
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.databinding.SharePofileItemBinding

class ShareProfileRenderer(
    private val onClick: () -> Unit,
    private val profileImageURL: String
) : Renderer<ShareProfileAdapterItem, ShareProfileViewHolder> {

    override val type: Class<ShareProfileAdapterItem>
        get() = ShareProfileAdapterItem::class.java

    override fun createViewHolder(parent: ViewGroup): ShareProfileViewHolder {
        return ShareProfileViewHolder(
            SharePofileItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), onClick
        )
    }

    override fun bindViewHolder(holder: ShareProfileViewHolder, item: ShareProfileAdapterItem) {
        holder.bind(profileImageURL)
    }

}