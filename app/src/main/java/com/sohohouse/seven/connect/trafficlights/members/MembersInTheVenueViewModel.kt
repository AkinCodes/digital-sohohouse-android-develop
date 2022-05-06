package com.sohohouse.seven.connect.trafficlights.members

import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.LiveEvent
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.prefs.LocalVenueProvider
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.connect.VenueMember
import com.sohohouse.seven.connect.mynetwork.ConnectionRepository
import com.sohohouse.seven.connect.trafficlights.AvailableStatus
import com.sohohouse.seven.connect.trafficlights.repo.TrafficLightsRepo
import com.sohohouse.seven.network.core.models.MutualConnectionRequests
import com.sohohouse.seven.profile.*
import com.sohohouse.seven.profile.view.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class MembersInTheVenueViewModel @Inject constructor(
    private val userManager: UserManager,
    private val connectionRepo: ConnectionRepository,
    private val trafficLightsRepo: TrafficLightsRepo,
    analyticsManager: AnalyticsManager,
    localVenueProvider: LocalVenueProvider
) : BaseViewModel(analyticsManager),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    Errorable.ViewModel by Errorable.ViewModelImpl() {

    private val estimatedTotalFlow = MutableStateFlow<Int?>(null)

    private val _showConnectionRequestBottomSheet = LiveEvent<ShowConnectionRequest>()
    val showConnectionRequestBottomSheet: LiveData<ShowConnectionRequest> get() = _showConnectionRequestBottomSheet

    private val _showProfileViewer = LiveEvent<VenueMember>()
    val showProfileViewer: LiveData<VenueMember> get() = _showProfileViewer

    private var dataSource: DataSource<Int, MemberInTheVenueListItem>? = null

    private val membersInVenueDataSourceFactory = MembersInVenueDataSourceFactory(
        trafficLightsRepo = trafficLightsRepo,
        coroutineScope = viewModelScope,
        coroutineContext = viewModelContext,
        loadable = this,
        errorable = this,
        estimatedTotalFlow = estimatedTotalFlow
    )

    val profileImageUrl = flowOf(userManager.profileImageURL)
    private val availabilityStatus: Flow<AvailableStatus> =
        userManager.availableStatusFlow.map { it.availableStatus }
    private val localVenueName =
        flow { emit(localVenueProvider.localVenue.value?.name ?: "") }.flowOn(viewModelContext)

    val statusColorAttr = availabilityStatus.map { it.colorAttrRes }
    val title = availabilityStatus.map {
        when (it) {
            AvailableStatus.UNAVAILABLE -> R.string.I_am_unavailable
            AvailableStatus.AVAILABLE -> R.string.I_am_available
            AvailableStatus.CONNECTIONS_ONLY -> R.string.connections_only
        }
    }

    val venueMembers = buildPagedList().asFlow().flowOn(viewModelContext)

    val connectionMembersCountAndHouseName: Flow<Pair<Int, String>>
        get() = estimatedTotalFlow
            .filterNotNull()
            .combine(localVenueName) { totalCount, name -> totalCount to name }

    val areOtherMembersInVenue: Flow<Boolean> = estimatedTotalFlow.filterNotNull().map { it > 1 }

    init {
        trafficLightsRepo.clearCache()
    }

    fun refresh() {
        dataSource?.invalidate()
    }

    fun clearCache() {
        trafficLightsRepo.clearCache()
    }


    private fun buildPagedList() = LivePagedListBuilder(
        object : DataSource.Factory<Int, MemberInTheVenueListItem>() {
            override fun create(): DataSource<Int, MemberInTheVenueListItem> =
                membersInVenueDataSourceFactory.create().map {
                    venueMemberToListItem(it, userManager.availableStatusFlow.value.availableStatus)
                }.also { dataSource = it }
        },
        PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(10).build()
    ).build()

    private fun venueMemberToListItem(
        venueMember: VenueMember,
        status: AvailableStatus
    ): MemberInTheVenueListItem {
        val isBlurred = status == AvailableStatus.UNAVAILABLE ||
                status == AvailableStatus.CONNECTIONS_ONLY && !venueMember.isConnection
        return MemberInTheVenueListItem(
            venueMember = venueMember,
            isBlurred = isBlurred,
            onActionRequest = {
                when (venueMember.mutualConnectionStatus) {
                    is NotConnected -> showConnectionRequestBottomSheet(venueMember)
                    is RequestReceived -> onClickAcceptRequest(venueMember)
                    is Connected -> _showProfileViewer.value = venueMember
                }
            },
            button = if (!isBlurred) getButtonText(venueMember.mutualConnectionStatus) else EmptyButton
        )
    }

    fun getButtonText(status: MutualConnectionStatus): Button {
        return when (status) {
            NotConnected -> ConnectButton
            RequestSent -> RequestSentButton
            RequestReceived -> AcceptButton
            Connected -> ViewProfileButton
            else -> EmptyButton
        }
    }


    private fun onClickAcceptRequest(venueMember: VenueMember) {
        viewModelScope.launch(viewModelContext) {
            val connection = venueMember.mutualConnectionRequest.firstOrNull {
                it.receiver.get().id == userManager.profileID
            }?.apply { state = MutualConnectionRequests.STATE_ACCEPTED } ?: return@launch
            connectionRepo.patchConnectionRequest(connection).ifValue {
                updateConnectedConnection(venueMember)
            }
        }
    }

    private fun showConnectionRequestBottomSheet(venueMember: VenueMember) {
        _showConnectionRequestBottomSheet.postValue(
            ShowConnectionRequest(
                profileItem = ProfileItem(
                    id = venueMember.id,
                    firstName = venueMember.fullName,
                    lastName = venueMember.lastName,
                    occupation = venueMember.occupation,
                    location = venueMember.location,
                    imageUrl = venueMember.imageUrl
                )
            ) {
                updateSentConnection(venueMember)
            }
        )
    }

    private fun updateSentConnection(venueMember: VenueMember) {
        trafficLightsRepo.updateSentConnection(venueMember)
        dataSource?.invalidate()
    }

    private fun updateConnectedConnection(venueMember: VenueMember) {
        trafficLightsRepo.updateConnectedConnection(venueMember)
        dataSource?.invalidate()
    }

    data class ShowConnectionRequest(
        val profileItem: ProfileItem,
        val onConnect: () -> Unit
    )

}
