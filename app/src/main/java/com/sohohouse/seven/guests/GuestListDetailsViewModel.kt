package com.sohohouse.seven.guests

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.BuildConfigManager
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.asEnumOrDefault
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.network.core.models.GuestList
import com.sohohouse.seven.network.core.models.Invite
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject


class GuestListDetailsViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    private val guestListRepository: GuestListRepository,
    private val buildConfigManager: BuildConfigManager,
    private val stringProvider: StringProvider,
    private val userManager: UserManager,
    private val guestListHelper: GuestListHelper,
    dispatcher: CoroutineDispatcher
) : BaseViewModel(analyticsManager, dispatcher), Loadable.ViewModel by Loadable.ViewModelImpl(),
    ErrorDialogViewModel by ErrorDialogViewModelImpl() {

    private val _items = MutableLiveData<List<GuestListDetailsAdapterItem>>()
    private val _addGuestAvailbale: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _addAnotherGuestAvailbale: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _doneAvailbale: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _deleteAvailable: MutableLiveData<Boolean> = MutableLiveData(false)

    val items: LiveData<List<GuestListDetailsAdapterItem>> get() = _items
    val addGuestAvailbale: LiveData<Boolean> get() = _addGuestAvailbale
    val addAnotherGuestAvailbale: LiveData<Boolean> get() = _addAnotherGuestAvailbale
    val doneAvailbale: LiveData<Boolean> get() = _doneAvailbale
    val deleteAvailable: LiveData<Boolean> get() = _deleteAvailable


    private val _navigationExitEvent = LiveEvent<Any>()
    val navigationExitEvent: LiveEvent<Any> get() = _navigationExitEvent

    private var guestList: GuestList? = null
    private var invites: MutableList<Invite>? = null

    val venueName get() = guestList?.venue?.name

    private lateinit var guestListID: String
    private var mode: GuestListDetailsMode = GuestListDetailsMode.MODE_NEW_GUEST_LIST

    fun init(id: String, mode: GuestListDetailsMode) {
        guestListID = id
        this.mode = mode
        fetchGuestList(id)
    }

    private fun fetchGuestList(id: String) {
        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)
            guestListRepository.getGuestList(id).fold(
                ifError = { handleError(it) },
                ifValue = { guestList ->
                    onGuestListRetrieved(guestList)
                }, ifEmpty = {}
            )
            setLoadingState(LoadingState.Idle)
        }
    }

    private fun onGuestListRetrieved(guestList: GuestList) {
        this.guestList = guestList
        this.invites = guestList.invites as MutableList<Invite>
        createAndEmitAdapterItems(guestList, invites!!)
        refreshAddGuestState()
        refreshModeButtons()
    }

    private fun createAndEmitAdapterItems(guestList: GuestList, invites: MutableList<Invite>) {
        _items.postValue(createAdapterItems(guestList, invites))
    }

    private fun createAdapterItems(
        guestList: GuestList,
        invites: List<Invite>
    ): List<GuestListDetailsAdapterItem> {
        return ArrayList<GuestListDetailsAdapterItem>().apply {
            val venue = guestList.venue
            val formItem = GuestListDetailsAdapterItem.GuestListFormItem(
                guestListHelper.buildHouseUIItem(venue, enabled = false),
                guestListHelper.buildDateUIItem(guestList.date!!, enabled = false),
                guestList.notes,
                isNoteEnabled = false
            )

            add(
                GuestListDetailsAdapterItem.FormHeaderItem(
                    R.string.label_invitation_details,
                    expanded = false
                )
            )
            add(formItem)

            add(GuestListDetailsAdapterItem.GuestsHeaderItem(R.string.header_your_guests))
            add(GuestListDetailsAdapterItem.GuestsSubheaderItem(R.string.title_guest_new_invite_message))

            addAll(buildGuestItems(invites))
        }
    }

    private fun refreshModeButtons() {
        _deleteAvailable.postValue(mode == GuestListDetailsMode.MODE_EXISTING_GUEST_LIST)
        _doneAvailbale.postValue(mode == GuestListDetailsMode.MODE_NEW_GUEST_LIST && invites!!.size > 0)
    }

    private fun refreshAddGuestState() {
        val numGuests = invites?.size ?: 0
        when {
            numGuests == 0 -> {
                _addGuestAvailbale.postValue(true)
                _addAnotherGuestAvailbale.postValue(false)
            }
            numGuests < guestList?.maxGuests ?: 0 -> {
                _addGuestAvailbale.postValue(false)
                _addAnotherGuestAvailbale.postValue(true)
            }
            else -> {
                _addGuestAvailbale.postValue(false)
                _addAnotherGuestAvailbale.postValue(false)
            }
        }
    }

    private fun buildGuestItems(invites: List<Invite>): Collection<GuestListDetailsAdapterItem.GuestItem> {
        return mutableListOf<GuestListDetailsAdapterItem.GuestItem>().apply {
            invites.iterator().forEach { invite ->
                val showStatus =
                    invite.status.asEnumOrDefault<InviteStatus>() != InviteStatus.PENDING
                add(
                    GuestListDetailsAdapterItem.GuestItem(
                        invite.id, invite.guestName,
                        invite.status.asEnumOrDefault<InviteStatus>(), showStatus = showStatus
                    )
                )
            }
        }
    }

    fun onNewGuestNameConfirmed(name: String) {
        logAddGuest()

        if (name.isEmpty()) {
            showGenericErrorDialogEvent.postValue(
                arrayOf(stringProvider.getString(R.string.error_general))
            )
            return
        }

        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)
            guestListRepository.addGuestToGuestList(guestListID, name).fold(
                ifError = {
                    handleError(it)
                },
                ifValue = { invite ->
                    addGuestToList(invite)
                }, ifEmpty = {}
            )
            setLoadingState(LoadingState.Idle)
        }
    }

    private fun addGuestToList(invite: Invite) {
        createAndEmitAdapterItems(guestList!!, invites!!.apply { add(invite) })
        refreshAddGuestState()
        refreshModeButtons()
    }

    fun onExistingGuestNameChanged(id: String, name: String) {
        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)
            guestListRepository.editGuestName(id, name).fold(
                ifError = { handleError(it) },
                ifValue = { newInvite ->
                    invites?.let { invites ->
                        invites[invites.indexOfFirst { it.id == newInvite.id }] = newInvite
                        createAndEmitAdapterItems(guestList!!, invites)
                    }
                }, ifEmpty = {}
            )
            setLoadingState(LoadingState.Idle)
        }
    }

    fun buildShareMessage(inviteId: String): String {
        return guestListHelper.buildShareMessage(inviteId, venueName ?: "")
    }

    fun deleteGuestList() {
        logDeleteInvitation()

        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)
            guestListRepository.deleteGuestList(guestListID).fold(
                ifError = {
                    handleError(it)
                },
                ifValue = {},
                ifEmpty = {
                    _navigationExitEvent.postEvent()
                }
            )
            setLoadingState(LoadingState.Idle)
        }
    }

    fun logClickShareLink() {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.GuestsDetailShare,
            AnalyticsManager.HouseGuest.buildParams(
                userManager.membershipType,
                guestList?.venue?.id,
                inviteId = guestListID
            )
        )
    }

    private fun logAddGuest() {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.GuestsDetailGuestAdd,
            AnalyticsManager.HouseGuest.buildParams(
                userManager.membershipType,
                guestList?.venue?.id,
                inviteId = guestListID
            )
        )
    }

    private fun logDeleteInvitation() {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.GuestsDetailListDelete,
            AnalyticsManager.HouseGuest.buildParams(
                userManager.membershipType,
                guestList?.venue?.id,
                guestList?.invites?.size,
                inviteId = guestListID
            )
        )
    }
}