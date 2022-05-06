package com.sohohouse.seven.connect.message.chat.content.binder

import android.content.Context
import android.view.LayoutInflater
import com.sohohouse.seven.databinding.ChatContentItemBinding
import com.sohohouse.seven.databinding.ChatImageMessageItemBinding
import com.sohohouse.seven.network.chat.model.message.Message

class ImageMsgBinder(
    val context: Context,
    val boundView: ChatContentItemBinding
) : BaseMsgBinder() {

    fun bindContent(
        content: Message.Image,
        isSentByMe: Boolean,
        onClick: (url: String) -> Unit,
        onContentReady: () -> Unit
    ) {
        val layoutInflater = LayoutInflater.from(context)
        ChatImageMessageItemBinding.inflate(
            layoutInflater, boundView.messagesContainer, true
        ).apply {

            val imageUrl = content.thumbnail ?: content.messageImageUrl

            image.setImageUrl(imageUrl) {
                onContentReady.invoke()
                return@setImageUrl it
            }
            image.setOnClickListener {
                onClick.invoke(content.messageImageUrl)
            }
            date.text = content.sentDate.toString()

            adjustConstraints(this, isSentByMe)
        }
    }

}