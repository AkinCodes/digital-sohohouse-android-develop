package com.sohohouse.seven.guests

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.isCwh
import com.sohohouse.seven.common.extensions.isUnavailable
import com.sohohouse.seven.common.prefs.LocalVenueProvider
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.models.GuestList
import com.sohohouse.seven.network.core.models.Invite
import com.sohohouse.seven.network.core.models.Venue
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

class NewGuestListViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    private val guestListRepository: GuestListRepository,
    private val userManager: UserManager,
    private val localVenueProvider: LocalVenueProvider,
    private val venueRepo: VenueRepo,
    private val guestListHelper: GuestListHelper,
    private val dispatcher: CoroutineDispatcher
) : BaseViewModel(analyticsManager, dispatcher), ErrorDialogViewModel by ErrorDialogViewModelImpl(),
    Loadable.ViewModel by Loadable.ViewModelImpl() {

    companion object {
        const val MONTHS_IN_FUTURE = 2
    }

    private var selectedVenue: Venue? = null
    private var selectedDate: Date = Date()
    var note: String? = null

    private val _house = MutableLiveData<GuestListFormHouseItem?>()
    val houseItem: LiveData<GuestListFormHouseItem?> get() = _house

    private val _date = MutableLiveData<GuestListFormDateItem?>()
    val dateItem: LiveData<GuestListFormDateItem?> get() = _date

    private val _navigateToGustListDetailsEvent = LiveEvent<String>()
    val navigateToGustListDetailsEvent: LiveEvent<String> get() = _navigateToGustListDetailsEvent

    private val _houseClosedErrorEvent = LiveEvent<Any>()
    val houseClosedErrorEvent: LiveEvent<Any> get() = _houseClosedErrorEvent

    private val _guestList = MutableLiveData<List<NewGuestItem>>()
    val guestList: LiveData<List<NewGuestItem>> get() = _guestList

    private val _shareInvitationEvent = LiveEvent<Any>()
    val shareInvitationEvent: LiveEvent<Any> get() = _shareInvitationEvent

    val submitEnabled: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(_house) {
            postValue(it != null && _date.value != null)
        }
        addSource(_date) {
            postValue(it != null && _house.value != null)
        }
    }

    private val _guestItemCreatedEvent = LiveEvent<Any>()
    val guestItemCreatedEvent: LiveEvent<Any> get() = _guestItemCreatedEvent

    private var _newGuestItem: GuestList? = null

    private val unInvitedGuestList: List<NewGuestItem>?
        get() {
            return guestList.value?.filter { !it.guestName.isNullOrEmpty() && it.invitationId.isNullOrEmpty() }
        }

    init {
        emitInitialHouseItem()
        fillSelectedDate()
    }

    private fun emitInitialHouseItem() {
        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            if (userManager.subscriptionType == SubscriptionType.LOCAL) {
                val localVenue = localVenueProvider.localVenue.value
                selectedVenue = localVenue
                if (localVenue != null) {
                    emitSelectedVenueItem(localVenue, lockSelection = true)
                }
            } else {
                val venueItem = venueRepo.venues().firstOrNull { it.id == "GRS" }
                if (venueItem != null) {
                    onHouseSelected(venueItem)
                } else {
                    _house.postValue(null)
                }
            }
        }
    }

    fun onHouseSelected(venue: Venue) {
        selectedVenue = venue
        generateGuestsTemplate()
        emitSelectedVenueItem(venue, lockSelection = false)
    }

    private fun emitSelectedVenueItem(venue: Venue, lockSelection: Boolean) {
        _house.postValue(guestListHelper.buildHouseUIItem(venue, enabled = lockSelection.not()))
    }

    private fun generateGuestsTemplate() {
        val templates = mutableListOf<NewGuestItem>()
        for (i in 0 until (selectedVenue?.maxGuests ?: 1)) {
            templates.add(NewGuestItem(i, null, null))
        }
        _guestList.postValue(templates)
    }

    fun getDatePickerData(): DatePickerData {
        return DatePickerData(
            selectedDate, Calendar.getInstance().time, getMaxDate().time
        )
    }

    private fun getMaxDate(): Calendar {
        val maxDate = Calendar.getInstance()
        maxDate.add(Calendar.MONTH, /*extraMonths*/MONTHS_IN_FUTURE)
        maxDate.set(Calendar.DAY_OF_MONTH, maxDate.getActualMaximum(Calendar.DAY_OF_MONTH))
        return maxDate
    }

    fun onDateSelected(year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
        selectedDate = calendar.time
        fillSelectedDate()
    }

    private fun fillSelectedDate() {
        _date.value = guestListHelper.buildDateUIItem(selectedDate, enabled = true)
    }

    fun onShareClicked(itemToShare: NewGuestItem) {
        _newGuestItem?.let { item ->
            val guestNames = guestList.value?.filter { it.guestIndex == itemToShare.guestIndex }
            inviteGuests(item, guestNames!!, itemToShare)
        } ?: run {
            createNewGuestItem(itemToShare)
        }
    }

    fun onConfirmClick() {
        _newGuestItem?.let {
            processExistingGuestItem(it)
        } ?: run {
            createNewGuestItem()
        }
    }

    private fun processExistingGuestItem(
        newGuestItem: GuestList,
        clickedItem: NewGuestItem? = null
    ) {
        unInvitedGuestList?.let { guestList ->
            setLoadingState(LoadingState.Loading)
            inviteGuests(newGuestItem, guestList, clickedItem)
        } ?: run {
            _navigateToGustListDetailsEvent.postValue(newGuestItem.id)
        }
    }

    private fun createNewGuestItem(clickedItem: NewGuestItem? = null) {
        if (selectedVenue!!.isCwh.not() && selectedVenue!!.operatingHours.isUnavailable) {
            _houseClosedErrorEvent.postEvent()
            return
        }

        val name = "${selectedVenue!!.name} ${_date.value!!.dateString}"
        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            setLoadingState(LoadingState.Loading)
            guestListRepository.createGuestList(name, selectedVenue!!.id, selectedDate!!, note)
                .fold(
                    ifError = {
                        handleError(it)
                    }, ifValue = {
                        newGuestItemCreated(it, clickedItem)
                    }, ifEmpty = {}
                )
            setLoadingState(LoadingState.Idle)
        }
    }

    private fun newGuestItemCreated(guestListIem: GuestList, clickedItem: NewGuestItem?) {
        logCreateInvitation(guestListIem)

        unInvitedGuestList?.let { guestList ->
            inviteGuests(guestListIem, guestList, clickedItem)
            _guestItemCreatedEvent.postValue(true)
            _newGuestItem = guestListIem
        } ?: kotlin.run {
            _navigateToGustListDetailsEvent.postValue(guestListIem.id)
        }
    }


    private fun inviteGuests(
        guestItem: GuestList,
        guestNames: List<NewGuestItem>,
        clickedItem: NewGuestItem?
    ) {

        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            val results = guestNames.filter { g -> !g.guestName.isNullOrEmpty() }.map {
                async {
                    guestListRepository.addGuestToGuestList(
                        guestListId = guestItem.id,
                        it.guestName!!
                    )
                }
            }.awaitAll()

            updateGuestsInviteIds(results, guestItem, guestNames)

            if (clickedItem != null) {
                callShareInvitation(clickedItem)
            } else {
                _navigateToGustListDetailsEvent.postValue(guestItem.id)
            }
        }
    }

    private fun callShareInvitation(clickedItem: NewGuestItem) {
        shareInvitationEvent.postValue(
            _guestList.value?.find { item -> item.guestIndex == clickedItem.guestIndex }?.invitationId
                ?: "-1"
        )
    }

    private fun updateGuestsInviteIds(
        results: List<Either<ServerError, Invite>>,
        guestItem: GuestList,
        guestNames: List<NewGuestItem>
    ) {
        results.forEachIndexed { inx, result ->
            result.ifValue {
                logInvitationGuest(guestItem)
                _guestList.value?.find { item -> item.guestIndex == guestNames[inx].guestIndex }?.invitationId =
                    it.id
            }
        }
    }

    fun getShareMessage(inviteId: String): String {
        return guestListHelper.buildShareMessage(
            inviteId,
            selectedVenue?.name ?: ""
        )
    }

    private fun logCreateInvitation(guestList: GuestList) {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.GuestsDetailNewListCreate,
            AnalyticsManager.HouseGuest.buildParams(
                membershipType = userManager.membershipType,
                houseId = selectedVenue?.id,
                inviteId = guestList.id
            )
        )
    }

    private fun logInvitationGuest(guestList: GuestList) {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.GuestsDetailGuestAdd,
            AnalyticsManager.HouseGuest.buildParams(
                userManager.membershipType,
                guestList.venue.id,
                inviteId = guestList.id
            )
        )
    }

}

data class DatePickerData(val selectedDate: Date, val minDate: Date, val maxDate: Date)