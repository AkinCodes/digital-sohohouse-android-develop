package com.sohohouse.seven.profile.view.renderer

import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.databinding.ViewHolderSocialMediaBinding
import com.sohohouse.seven.profile.SocialMediaItem
import com.sohohouse.seven.profile.view.model.SocialAccounts
import com.sohohouse.seven.profile.view.viewholder.SocialAccountsViewHolder

class SocialAccountsRenderer(
    private val onSocialAccountClick: (SocialMediaItem) -> Unit = {}
) : Renderer<SocialAccounts, SocialAccountsViewHolder> {

    override val type: Class<SocialAccounts>
        get() = SocialAccounts::class.java

    override fun createViewHolder(parent: ViewGroup): SocialAccountsViewHolder {
        return SocialAccountsViewHolder(
            ViewHolderSocialMediaBinding.bind(
                createItemView(parent, R.layout.view_holder_social_media)
            )
        )
    }

    override fun bindViewHolder(holder: SocialAccountsViewHolder, item: SocialAccounts) {
        holder.bind(item, onSocialAccountClick)
    }
}