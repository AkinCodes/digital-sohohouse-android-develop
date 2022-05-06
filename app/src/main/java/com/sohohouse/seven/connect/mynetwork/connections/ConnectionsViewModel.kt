package com.sohohouse.seven.connect.mynetwork.connections

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.LiveEvent
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.connect.mynetwork.ConnectionRepository
import com.sohohouse.seven.network.chat.ChannelId
import com.sohohouse.seven.network.chat.ChatChannelsRepo
import com.sohohouse.seven.network.chat.ChatConnectionRepo
import com.sohohouse.seven.network.chat.model.CreateOneToOneChannel
import com.sohohouse.seven.network.chat.model.OneToOneChatMembers
import com.sohohouse.seven.profile.Connected
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

class ConnectionsViewModel @Inject constructor(
    private val userManager: UserManager,
    repo: ConnectionRepository,
    analyticsManager: AnalyticsManager,
    private val chatConnectionRepo: ChatConnectionRepo,
    private val chatChannelsRepo: ChatChannelsRepo,
    ioDispatcher: CoroutineDispatcher,
) : BaseViewModel(analyticsManager),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    Errorable.ViewModel by Errorable.ViewModelImpl() {

    init {
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            repo.getConnectionRequests(page = 1, perPage = 1)
        }
    }

    private val dataSourceFactory = ConnectionsDataSource.Factory(
        repo = repo,
        userManager = userManager,
        scope = viewModelScope,
        coroutineContext = viewModelContext,
        loadable = this,
        errorable = this
    )

    val profileImageURL = userManager.profileImageURL
    val profileID = userManager.profileID
    private val _navigateToChatScreen = LiveEvent<Triple<ChannelId, Boolean, String>>()
    val navigateToChatScreen: LiveData<Triple<ChannelId, Boolean, String>> get() = _navigateToChatScreen

    val connections: LiveData<PagedList<DiffItem>> = LivePagedListBuilder(
        object : DataSource.Factory<Int, DiffItem>() {
            override fun create(): DataSource<Int, DiffItem> = dataSourceFactory.create()
        },
        PagedList.Config.Builder().setEnablePlaceholders(false)
            .setPageSize(ConnectionRepository.ITEMS_PER_PAGE).build()
    ).build()

    fun refresh() = dataSourceFactory.invalidate()

    fun message(profile: ProfileItem) {
        viewModelScope.launch(viewModelContext) {
            chatConnectionRepo.connect(userManager.getMiniProfileForSB())
            val (channelId, _) = chatChannelsRepo.create(
                CreateOneToOneChannel(
                    OneToOneChatMembers(userManager.profileID, profile.id),
                    imageUrl = profile.imageUrl ?: "",
                    name = profile.fullName
                )
            )

            _navigateToChatScreen.postValue(
                Triple(channelId, profile.status == Connected, profile.id)
            )
        }
    }
}