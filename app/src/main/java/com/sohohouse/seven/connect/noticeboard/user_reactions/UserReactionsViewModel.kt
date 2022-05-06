package com.sohohouse.seven.connect.noticeboard.user_reactions

import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.connect.noticeboard.reactions.NoticeboardReactionIconsProvider
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.isSuccessful
import com.sohohouse.seven.network.core.models.CheckInReactionByUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserReactionsViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    dispatcher: CoroutineDispatcher,
    private val apiService: SohoApiService,
    private val ioDispatcher: CoroutineDispatcher,
    private val iconsProvider: NoticeboardReactionIconsProvider,
) : BaseViewModel(analyticsManager, dispatcher),
    Errorable.ViewModel by Errorable.ViewModelImpl(),
    Loadable.ViewModel by Loadable.ViewModelImpl() {

    val listOfReactions = MutableStateFlow<List<UserReactionItem>>(emptyList())
    val showProfile = MutableSharedFlow<ProfileItem>(extraBufferCapacity = 1)

    fun loadReactionsForPost(id: String) =
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            setLoading()
            val requestForReactions = apiService.getProfilesOfUsersWhoHaveReactedToPost(id)
            if (requestForReactions.isSuccessful()) {
                listOfReactions.emit(sortAndMapReactionList(requestForReactions.response))
            } else {
                showError()
            }
            setIdle()
        }

    private fun sortAndMapReactionList(response: List<CheckInReactionByUser>): List<UserReactionItem> {
        return response.map {
            val profileItem = ProfileItem(profile = it.profile!!)
            UserReactionItem(
                profileItem = profileItem,
                reaction = iconsProvider.getNoticeBoardReaction(it.icon),
                onProfileClick = { showProfile.tryEmit(profileItem) }
            )
        }.sortedBy { it.reaction.reaction }
    }
}
