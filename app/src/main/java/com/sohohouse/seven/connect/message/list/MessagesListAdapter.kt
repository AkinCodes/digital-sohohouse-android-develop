package com.sohohouse.seven.connect.message.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseRecyclerDiffAdapter
import com.sohohouse.seven.common.extensions.context
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.connect.message.model.MessagesListItem
import com.sohohouse.seven.databinding.ViewMessagesItemBinding

class MessagesListAdapter :
    BaseRecyclerDiffAdapter<MessagesListAdapter.MessagesViewHolder, MessagesListItem>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MessagesViewHolder(ViewMessagesItemBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class MessagesViewHolder(val binding: ViewMessagesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(messagesListItem: MessagesListItem) {
            binding.messagesItemTitle.text = messagesListItem.title
            binding.messagesItemText.text = messagesListItem.text
            binding.messagesItemText.setTextColor(
                ContextCompat.getColor(
                    binding.root.context, if (messagesListItem.hasUnreadMsg)
                        R.color.white else R.color.charcoal
                )
            )
            binding.messagesLastTime.text = messagesListItem.strTime
            binding.messagesUnreadIndicator.isVisible =
                messagesListItem.hasUnreadMsg && !messagesListItem.isMuted
            binding.messagesMuteIndicator.isInvisible = !messagesListItem.isMuted
            binding.messagesOnlineIndicator.isVisible = messagesListItem.isOnline
            binding.muteMessages.text = getMuteButtonText(messagesListItem)
            binding.messagesProfileImage.apply {
                setImageFromUrl(
                    messagesListItem.imageUrl,
                    isRound = true,
                    placeholder = R.drawable.ic_profile
                )
            }
            binding.staffIndication.isVisible = messagesListItem.isStaff
            binding.deleteMessages.setOnClickListener {
                messagesListItem.onDelete()
            }
            binding.muteMessages.setOnClickListener {
                messagesListItem.onMute()
            }
            binding.constrait.setOnClickListener {
                messagesListItem.onClick()
            }
            binding.messagingSwipeLayout.close(false)
        }

        private fun getMuteButtonText(item: MessagesListItem): CharSequence {
            return if (item.isMuted) {
                context.getText(R.string.un_mute)
            } else {
                context.getText(R.string.mute)
            }
        }
    }

}