package com.sohohouse.seven.profile.view.viewholder

import android.view.LayoutInflater
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.context
import com.sohohouse.seven.common.extensions.inflateAttachAndReturnSelf
import com.sohohouse.seven.databinding.ViewHolderSocialMediaBinding
import com.sohohouse.seven.profile.SocialMediaItem
import com.sohohouse.seven.profile.view.model.SocialAccounts

class SocialAccountsViewHolder(private val binding: ViewHolderSocialMediaBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(socialAccounts: SocialAccounts, onSocialAccountClick: (SocialMediaItem) -> Unit) {
        binding.socialAccounts.removeAllViews()

        val items = socialAccounts.items
        items.forEach { item ->
            LayoutInflater.from(context).inflateAttachAndReturnSelf<ImageView>(
                R.layout.social_icon_imageview,
                binding.socialAccounts
            ).apply {
                contentDescription = item.name
                setImageResource(item.type.iconResId)
                setOnClickListener { onSocialAccountClick(item) }
            }
        }
    }
}