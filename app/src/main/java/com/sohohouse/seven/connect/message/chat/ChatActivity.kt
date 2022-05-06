package com.sohohouse.seven.connect.message.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.show
import com.sohohouse.seven.connect.message.chat.content.ChatContentFragment
import com.sohohouse.seven.connect.message.chat.fullscreen.MediaFullScreenFragment
import com.sohohouse.seven.network.chat.ChannelId

class ChatActivity : BaseMVVMActivity<ChatViewModel>() {

    override val viewModelClass: Class<ChatViewModel>
        get() = ChatViewModel::class.java

    override fun getContentLayout(): Int {
        return R.layout.chat_content_holder
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val channelUrl = intent.getStringExtra(ChatContentFragment.CHANNEL_URL)
            ?: intent.data?.getQueryParameter("id")
            ?: ""
        val channelId = intent.getStringExtra(ChatContentFragment.CHANNEL_ID) ?: ""
        val memberId = intent.getStringExtra(ChatContentFragment.MEMBER_ID) ?: ""

        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction().replace(
                R.id.root,
                ChatContentFragment.newInstance(
                    channelId = channelId,
                    channelUrl = channelUrl,
                    memberId = memberId
                )
            ).commit()

        supportFragmentManager.setFragmentResultListener(
            ChatContentFragment.GO_BACK,
            this
        ) { key, _ ->
            if (key == ChatContentFragment.GO_BACK) finish()
        }

        viewModel.fullScreenMutableLiveData.observe(this) {
            MediaFullScreenFragment.getInstance(it.first, it.second)
                .show(supportFragmentManager, R.id.root, MediaFullScreenFragment.TAG, false)
        }

        viewModel.setScreenName(name= AnalyticsManager.Screens.Chat.name)
    }

    companion object {
        fun newIntentViaURL(
            context: Context,
            channelUrl: String,
            memberId: String
        ): Intent {
            return Intent(context, ChatActivity::class.java).apply {
                putExtra(ChatContentFragment.CHANNEL_URL, channelUrl)
                putExtra(ChatContentFragment.MEMBER_ID, memberId)
            }
        }

        fun newIntentViaID(
            context: Context,
            channelId: ChannelId,
            memberId: String = ""
        ): Intent {
            return Intent(context, ChatActivity::class.java).apply {
                putExtra(ChatContentFragment.CHANNEL_ID, channelId)
                putExtra(ChatContentFragment.MEMBER_ID, memberId)
            }
        }
    }

}
