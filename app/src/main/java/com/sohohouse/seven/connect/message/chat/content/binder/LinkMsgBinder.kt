package com.sohohouse.seven.connect.message.chat.content.binder

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.sohohouse.seven.R
import com.sohohouse.seven.common.views.webview.WebViewBottomSheetFragment
import com.sohohouse.seven.databinding.ChatContentItemBinding
import com.sohohouse.seven.databinding.ChatMessageLinkItemBinding
import com.sohohouse.seven.network.chat.model.message.Message

class LinkMsgBinder(
    val context: Context,
    val boundView: ChatContentItemBinding
) : BaseMsgBinder() {

    fun bindContent(content: Message.Link, isSentByMe: Boolean) {

        val layoutInflater = LayoutInflater.from(context)
        ChatMessageLinkItemBinding.inflate(
            layoutInflater, boundView.messagesContainer, true
        ).apply {
            title.text = content.title
            description.text = content.description
            link.text = content.getVisibleLink()
            card.setCardBackgroundColor(
                if (isSentByMe)
                    context.getColor(R.color.black)
                else
                    context.getColor(R.color.black_russian)
            )
            date.text = content.sentDate.toString()

            card.setOnClickListener {
                if (content.isSohoHouseLink) {
                    WebViewBottomSheetFragment.withUrl(content.url, true)
                        .show(
                            (boundView.root.context as AppCompatActivity).supportFragmentManager,
                            WebViewBottomSheetFragment.TAG
                        )
                } else {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(content.url))
                    ContextCompat.startActivity(boundView.root.context, browserIntent, null)
                }
            }

            adjustConstraints(this, isSentByMe)
        }

    }
}