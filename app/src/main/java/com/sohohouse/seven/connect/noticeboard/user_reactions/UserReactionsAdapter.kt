package com.sohohouse.seven.connect.noticeboard.user_reactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sohohouse.seven.base.DefaultDiffItemCallback
import com.sohohouse.seven.common.extensions.context
import com.sohohouse.seven.databinding.UserReactionProfileItemBinding

class UserReactionsAdapter :
    ListAdapter<UserReactionItem, UserReactionsViewHolder>(DefaultDiffItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserReactionsViewHolder {
        return UserReactionsViewHolder(
            UserReactionProfileItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: UserReactionsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

data class UserReactionsViewHolder(
    private val binding: UserReactionProfileItemBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: UserReactionItem) = with(binding) {
        authorName.text = item.profileItem.fullName

        item.profileItem.imageUrl?.takeIf { it.isNotEmpty() }?.let { url ->
            Glide.with(context)
                .load(url)
                .circleCrop()
                .into(profileImage)
        }

        Glide.with(context)
            .load(item.reaction.iconUrl)
            .into(reactionIcon)

        profession.text = item.profileItem.occupation

        binding.root.setOnClickListener {
            item.onProfileClick.invoke()
        }
    }
}