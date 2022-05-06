package com.sohohouse.seven.connect.message.chat.content.binder

import android.content.Context
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.setLinkableText
import com.sohohouse.seven.databinding.ChatContentItemBinding
import com.sohohouse.seven.databinding.ChatMessageItemBinding
import com.sohohouse.seven.network.chat.model.message.Message

class TextMsgBinder(
    val context: Context,
    val boundView: ChatContentItemBinding
) : BaseMsgBinder() {

    fun bindContent(
        content: Message.Text,
        isSentByMe: Boolean
    ) {
        val layoutInflater = LayoutInflater.from(context)
        ChatMessageItemBinding.inflate(
            layoutInflater, boundView.messagesContainer, true
        ).apply {
            message.setLinkableText(content.message)
            card.setCardBackgroundColor(
                if (isSentByMe)
                    context.getColor(R.color.black)
                else
                    context.getColor(R.color.black_russian)
            )
            message.setTextColor(context.getColor(R.color.charcoal))
            date.text = content.sentDate.toString()

            message.setOnClickListener {
                date.isVisible = date.isVisible.not()
            }
            adjustConstraints(this, isSentByMe)
        }

    }
}