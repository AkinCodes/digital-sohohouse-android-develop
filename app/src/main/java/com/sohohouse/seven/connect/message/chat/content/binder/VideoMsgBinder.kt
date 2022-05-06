package com.sohohouse.seven.connect.message.chat.content.binder

import android.content.Context
import android.view.LayoutInflater
import com.sohohouse.seven.databinding.ChatContentItemBinding
import com.sohohouse.seven.databinding.ChatVideoMessageItemBinding
import com.sohohouse.seven.network.chat.model.message.Message

class VideoMsgBinder(
    val context: Context,
    val boundView: ChatContentItemBinding
) : BaseMsgBinder() {

    var videoMsgBinding: ChatVideoMessageItemBinding? = null

    fun bindContent(
        content: Message.Video,
        isSentByMe: Boolean,
        onClick: (url: String) -> Unit,
        onContentReady: () -> Unit
    ) {
        val layoutInflater = LayoutInflater.from(context)
        videoMsgBinding = ChatVideoMessageItemBinding.inflate(
            layoutInflater, boundView.messagesContainer, true
        ).apply {

            thumbnailImage.setImageUrl(content.thumbnail) {
                onContentReady.invoke()
                return@setImageUrl it
            }
            thumbnailImage.setOnClickListener {
                onClick.invoke(content.messageVideoUrl)
            }
            date.text = content.sentDate.toString()

            adjustConstraints(this, isSentByMe)
        }
    }

}