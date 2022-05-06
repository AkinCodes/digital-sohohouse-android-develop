package com.sohohouse.seven.profile.view

import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.connect.mynetwork.ConnectionRepository
import com.sohohouse.seven.connect.trafficlights.repo.TrafficLightsRepo
import com.sohohouse.seven.network.chat.ChatChannelsRepo
import com.sohohouse.seven.network.chat.ChatConnectionRepo
import com.sohohouse.seven.network.chat.model.CreateChatResult
import com.sohohouse.seven.network.chat.model.CreateOneToOneChannel
import com.sohohouse.seven.network.chat.model.OneToOneChatMembers
import com.sohohouse.seven.network.core.models.Connection
import com.sohohouse.seven.network.core.models.MutualConnectionRequests
import com.sohohouse.seven.network.core.models.MutualConnectionRequests.Companion.STATE_ACCEPTED
import com.sohohouse.seven.network.core.models.MutualConnectionRequests.Companion.STATE_HIDDEN
import com.sohohouse.seven.network.core.models.Profile
import com.sohohouse.seven.profile.MutualConnectionStatus
import com.sohohouse.seven.profile.ProfileRepository
import com.sohohouse.seven.profile.SocialMediaItem
import com.sohohouse.seven.profile.view.model.Buttons
import com.sohohouse.seven.profile.view.model.MessageButton
import com.sohohouse.seven.profile.view.model.MessageItem
import com.sohohouse.seven.profile.view.model.SocialAccounts
import com.sohohouse.seven.profile.view.renderer.ProfileHeaderRenderer
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ProfileViewerViewModel @AssistedInject constructor(
    @Assisted private var _profile: ProfileItem,
    @Assisted private val message: String?,
    private val userManager: UserManager,
    private val profileRepo: ProfileRepository,
    private val connectionRepo: ConnectionRepository,
    private val trafficLightsRepo: TrafficLightsRepo,
    private val chatConnectionRepo: ChatConnectionRepo,
    private val chatChannelsRepo: ChatChannelsRepo,
    analyticsManager: AnalyticsManager,
    dispatcher: CoroutineDispatcher
) : BaseViewModel(analyticsManager, dispatcher),
    Errorable.ViewModel by Errorable.ViewModelImpl(),
    Loadable.ViewModel by Loadable.ViewModelImpl() {

    private val _items: MutableLiveData<List<DiffItem>> = MutableLiveData()
    val items: LiveData<List<DiffItem>>
        get() = _items

    val profileId: String = profile.id

    private val _navigateToChatScreen = LiveEvent<CreateChatResult>()
    val navigateToChatScreen: LiveData<CreateChatResult> get() = _navigateToChatScreen

    private val _connectionChanged: LiveEvent<Any> = LiveEvent()
    val connectionChanged: LiveData<Any>
        get() = _connectionChanged

    val fullName = profile.fullName

    val profile: ProfileItem
        get() = _profile

    val isFriendsSubscriptionType = userManager.subscriptionType == SubscriptionType.FRIENDS

    private val myProfileId: String = userManager.profileID

    val isMyProfile: Boolean = _profile.id == userManager.profileID

    private val userAvailableStatusColorAttrRes = userManager.isCheckedIn
        .combine(userManager.availableStatusFlow.map { it.availableStatus }) { isCheckedIn, status ->
            if (isCheckedIn)
                trafficLightsRepo.getUserStatus().fold(
                    ifEmptyOrError = { false to 0 },
                    ifValue = { isCheckedIn to status.colorAttrRes }
                )
            else
                false to 0
        }.filter { isMyProfile }.flowOn(viewModelContext)

    val isCheckedIn get() = userManager.isCheckedIn.value

    init {
        _items.value = buildItems()

        // get profile cos there is no connection info when coming from notice board
        viewModelScope.launch(viewModelContext) {
            connectionRepo.getBlockedMembers()
            getProfile()
        }
    }

    fun getProfile() {
        viewModelScope.launch(viewModelContext) {
            profileRepo.getProfile(_profile.id)
                .ifValue {
                    onSuccess(it, connectionRepo.blockedMembers.value)
                }
                .ifError { showError() }
        }
    }

    private fun onSuccess(profile: Profile, blockedMembers: List<String>) {
        _profile = ProfileItem(
            profile = profile,
            status = MutualConnectionStatus.from(profile, myProfileId, blockedMembers),
            connectionId = getConnectionId(profile),
            showMessageButton = _profile.showMessageButton
        )
        _items.postValue(buildItems())
    }

    private fun getConnectionId(profile: Profile): String? {
        if (isMyProfile) return null

        profile.mutualConnectionRequest.firstOrNull {
            Connection.getValidConnectionId(myProfileId, it) != null
        }?.let { return it.id }

        return profile.mutualConnections.firstOrNull {
            Connection.getValidConnectionId(myProfileId, it) != null
        }?.id
    }

    private fun buildItems(): MutableList<DiffItem> {
        return mutableListOf<DiffItem>(
            ProfileHeaderRenderer.ProfileHeaderItem(
                _profile,
                userAvailableStatusColorAttrRes
            )
        ).apply {
            if (_profile.socialOptIns.isNotEmpty()) add(SocialAccounts(_profile.socialOptIns))
            if (!message.isNullOrEmpty()) add(MessageItem(message))
            Buttons.from(_profile.status)?.let { buttons ->
                buttons.list.filter {
                    if (it is MessageButton) {
                        _profile.showMessageButton
                    } else {
                        true
                    }
                }.also { add(Buttons(it)) }
            }
        }
    }

    fun acceptRequest() {
        patchConnectionRequest(STATE_ACCEPTED)
    }

    fun ignoreRequest() {
        patchConnectionRequest(STATE_HIDDEN)
    }

    fun unblockMember() {
        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)
            connectionRepo.patchUnblockMember(profile.id)
                .ifValue { onConnectionStatusChanged() }
                .ifError { showError() }
            setLoadingState(LoadingState.Idle)
        }
    }

    private fun onConnectionStatusChanged() {
        println("onConnectionStatusChanged")
        _connectionChanged.postValue(Any())
        getProfile()
    }

    private fun patchConnectionRequest(status: String) {
        if (_profile.connectionId == null) return

        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)
            connectionRepo.patchConnectionRequest(MutualConnectionRequests(state = status).also {
                it.id = _profile.connectionId
            })
                .ifValue { onConnectionStatusChanged() }
                .ifError { showError() }
            setLoadingState(LoadingState.Idle)
        }
    }

    fun logViewed() {
        if (isMyProfile) {
            analyticsManager.logEventAction(AnalyticsManager.Action.ViewPersonalProfile)
        } else {
            analyticsManager.logEventAction(AnalyticsManager.Action.ViewPublicProfile)
        }
    }

    fun logSocialMediaClick(account: SocialMediaItem) {
        analyticsManager.logEventAction(AnalyticsManager.SocialMedia.mapToAction(account))
    }

    fun createChannel() {
        viewModelScope.launch(viewModelContext) {
            chatConnectionRepo.connect(userManager.getMiniProfileForSB())

            val oenToOneChannel = CreateOneToOneChannel(
                OneToOneChatMembers(userManager.profileID, profileId),
                imageUrl = profile.imageUrl ?: "",
                name = profile.fullName
            )

            var createChatResult = chatChannelsRepo.create(oenToOneChannel)

            if (createChatResult.isNew) createChatResult =
                chatChannelsRepo.createNewDMChannel(oenToOneChannel)

            analyticsManager.logEventAction(
                action = AnalyticsManager.Action.ProfileMessageTap,
                params = bundleOf(
                    AnalyticsManager.Parameters.GlobalId.value to userManager.profileID,
                    "is_new_chat" to createChatResult.isNew,
                    AnalyticsManager.Parameters.RecipientGlobalId.value to profile.id
                )
            )
            _navigateToChatScreen.postValue(createChatResult)
        }
    }

    fun logAnalyticsAction(action: AnalyticsManager.Action) {
        analyticsManager.logEventAction(action)
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted profile: ProfileItem,
            @Assisted message: String?
        ): ProfileViewerViewModel
    }
}