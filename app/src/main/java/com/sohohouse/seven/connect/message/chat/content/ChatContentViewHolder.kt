package com.sohohouse.seven.connect.message.chat.content

import android.view.Gravity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.connect.message.chat.content.binder.ImageMsgBinder
import com.sohohouse.seven.connect.message.chat.content.binder.LinkMsgBinder
import com.sohohouse.seven.connect.message.chat.content.binder.TextMsgBinder
import com.sohohouse.seven.connect.message.chat.content.binder.VideoMsgBinder
import com.sohohouse.seven.databinding.*
import com.sohohouse.seven.network.chat.model.message.Message


sealed class ChatContentViewHolder(
    boundView: ViewBinding
) : RecyclerView.ViewHolder(boundView.root) {


    class MessageHolder(
        val boundView: ChatContentItemBinding
    ) : ChatContentViewHolder(boundView) {

        var dataItem: ChatContentListItem.MessageGroup? = null
        var videoMsgBinding: ChatVideoMessageItemBinding? = null

        fun bind(item: ChatContentListItem.MessageGroup) {
            dataItem = item

            boundView.profileImage.setImageUrl(
                item.imageUrl,
                R.drawable.ic_chat_profile_placeholder
            )
            boundView.profileImage.isVisible = item.isSentByMe.not()
            boundView.profileImage.setOnClickListener {
                item.onProfileIconClick.invoke()
            }

            boundView.messagesContainer.removeAllViews()
            boundView.messagesContainer.gravity =
                if (item.isSentByMe) Gravity.END else Gravity.START
            item.messageContents.iterator().forEach { message ->
                when (message) {
                    is Message.Image -> ImageMsgBinder(context, boundView).apply {
                        bindContent(
                            content = message,
                            isSentByMe = item.isSentByMe,
                            onClick = { url ->
                                item.onMediaClick.invoke(Pair(url, null))
                            },
                            onContentReady = { updateConstraints(item) }
                        )
                    }
                    is Message.Video -> {
                        VideoMsgBinder(context, boundView).apply {
                            bindContent(
                                content = message,
                                isSentByMe = item.isSentByMe,
                                onClick = { url ->
                                    item.onMediaClick.invoke(Pair(null, url))
                                },
                                onContentReady = { updateConstraints(item) }
                            )
                            this@MessageHolder.videoMsgBinding = videoMsgBinding
                        }
                        boundView.root.tag = this
                    }
                    is Message.Text -> TextMsgBinder(context, boundView).apply {
                        bindContent(message, item.isSentByMe)
                    }
                    is Message.Link -> LinkMsgBinder(context, boundView).apply {
                        bindContent(message, item.isSentByMe)
                    }
                }
                updateConstraints(item)
            }
        }


        private fun updateConstraints(item: ChatContentListItem.MessageGroup) {
            val constraintSet = ConstraintSet()
            constraintSet.clone(boundView.root)
            constraintSet.clear(boundView.profileImage.id, ConstraintSet.END)
            constraintSet.clear(boundView.profileImage.id, ConstraintSet.START)
            constraintSet.clear(boundView.messagesContainer.id, ConstraintSet.START)
            constraintSet.clear(boundView.messagesContainer.id, ConstraintSet.END)
            if (item.isSentByMe) {
                constraintSet.connect(
                    boundView.profileImage.id,
                    ConstraintSet.END,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.END,
                    0
                )
                constraintSet.connect(
                    boundView.messagesContainer.id,
                    ConstraintSet.END,
                    boundView.profileImage.id,
                    ConstraintSet.START,
                    0
                )
            } else {
                constraintSet.connect(
                    boundView.profileImage.id,
                    ConstraintSet.START,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.START,
                    0
                )
                constraintSet.connect(
                    boundView.messagesContainer.id,
                    ConstraintSet.START,
                    boundView.profileImage.id,
                    ConstraintSet.END,
                    context.resources.getDimensionPixelOffset(R.dimen.dp_8)
                )
            }
            constraintSet.applyTo(boundView.root)
        }

    }

    class DateIndicatorHolder(
        private val boundView: ChatDateIndicatorItemBinding
    ) : ChatContentViewHolder(boundView) {

        fun bind(item: ChatContentListItem.DateIndicator) {
            boundView.root.text = item.sentDate
        }
    }
}