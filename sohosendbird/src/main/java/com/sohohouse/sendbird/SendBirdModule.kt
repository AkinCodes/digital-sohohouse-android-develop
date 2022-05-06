package com.sohohouse.sendbird

import com.sohohouse.sendbird.mapper.SendBirdChannelToOnoToOneChannel
import com.sohohouse.sendbird.repo.SendBirdChatChannelRepoImpl
import com.sohohouse.sendbird.repo.SendBirdChatConnectionRepoImpl
import com.sohohouse.sendbird.repo.SendBirdChatUsersRepoImpl
import com.sohohouse.seven.network.chat.ChatChannelsRepo
import com.sohohouse.seven.network.chat.ChatConnectionRepo
import com.sohohouse.seven.network.chat.ChatUsersRepo
import com.sohohouse.seven.network.core.BaseApiService
import com.sohohouse.seven.network.core.SohoApiService
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@Module
class SendBirdModule {

    companion object {
        const val SEND_BIRD_BASE_URL = "send_bird_app_name"
    }

    @Provides
    @Singleton
    fun providesSendBirdChatRepo(): ChatUsersRepo {
        return SendBirdChatUsersRepoImpl()
    }

    @ExperimentalCoroutinesApi
    @Provides
    @Singleton
    fun providesSendBirdChatChannelsRepo(
        networkErrorReporter: BaseApiService.NetworkErrorReporter,
        sohoApiService: SohoApiService
    ): ChatChannelsRepo {
        return SendBirdChatChannelRepoImpl(
            networkErrorReporter,
            SendBirdChannelToOnoToOneChannel.Impl(),
            sohoApiService,
        )
    }

    @Provides
    @Singleton
    fun providesSendBirdChatConnectionRepo(
        sohoApiService: SohoApiService,
        sendBirdHelper: SendBirdHelper
    ): ChatConnectionRepo {
        return SendBirdChatConnectionRepoImpl(sohoApiService, sendBirdHelper)
    }

    @Provides
    @Singleton
    fun provideSendBirdHelper(): SendBirdHelper {
        return SendBirdHelper()
    }
}