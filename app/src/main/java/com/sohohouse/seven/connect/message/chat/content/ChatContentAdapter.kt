package com.sohohouse.seven.connect.message.chat.content

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.ChatContentItemBinding
import com.sohohouse.seven.databinding.ChatDateIndicatorItemBinding
import androidx.recyclerview.widget.DiffUtil as rvDiffUtil

class ChatContentAdapter : ListAdapter<ChatContentListItem, ChatContentViewHolder>(DiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatContentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            MESSAGE_VIEW_TYPE -> {
                ChatContentViewHolder.MessageHolder(
                    ChatContentItemBinding.bind(
                        inflater.inflate(R.layout.chat_content_item, parent, false)
                    )
                )
            }
            DATE_VIEW_TYPE -> {
                ChatContentViewHolder.DateIndicatorHolder(
                    ChatDateIndicatorItemBinding.bind(
                        inflater.inflate(R.layout.chat_date_indicator_item, parent, false)
                    )
                )
            }
            else -> error("Unhandled view type: $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ChatContentListItem.DateIndicator -> DATE_VIEW_TYPE
            is ChatContentListItem.MessageGroup -> MESSAGE_VIEW_TYPE
        }
    }

    override fun onBindViewHolder(holder: ChatContentViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ChatContentListItem.DateIndicator -> (holder as? ChatContentViewHolder.DateIndicatorHolder)?.bind(
                item
            )
            is ChatContentListItem.MessageGroup -> (holder as? ChatContentViewHolder.MessageHolder)?.bind(
                item
            )
        }
    }

    class DiffUtil : rvDiffUtil.ItemCallback<ChatContentListItem>() {
        override fun areItemsTheSame(
            oldItem: ChatContentListItem,
            newItem: ChatContentListItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ChatContentListItem,
            newItem: ChatContentListItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val MESSAGE_VIEW_TYPE = 1
        private const val DATE_VIEW_TYPE = 2
    }

}