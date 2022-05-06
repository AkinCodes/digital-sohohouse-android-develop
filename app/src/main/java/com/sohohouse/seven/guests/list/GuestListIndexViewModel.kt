package com.sohohouse.seven.guests.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.asEnumOrDefault
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.guests.GuestListRepository
import com.sohohouse.seven.guests.InviteStatus
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.models.GuestList
import com.sohohouse.seven.network.core.models.Venue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

class GuestListIndexViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    private val guestListRepository: GuestListRepository,
    private val venueRepo: VenueRepo,
    private val userManager: UserManager,
    dispatcher: CoroutineDispatcher
) : BaseViewModel(analyticsManager, dispatcher), Loadable.ViewModel by Loadable.ViewModelImpl() {

    private val _items = MutableLiveData<List<GuestListItem>>()
    val items: LiveData<List<GuestListItem>> get() = _items

    @Suppress("UNCHECKED_CAST")
    fun getGuestLists() {
        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)
            val result = awaitAll(
                async { value(venueRepo.venues().filterWithTopLevel()) },
                async { guestListRepository.getGuestLists() }
            ).also {
                it.firstOrNull { either -> either !is Either.Value }
                    ?.let {
                        setLoadingState(LoadingState.Idle)
                        return@launch
                    }
            }

            val venues = result[0] as Either.Value<List<Venue>>
            val guestList = result[1] as Either.Value<List<GuestList>>
            onGuestListsRetrieved(guestList.value, venues.value)
            setLoadingState(LoadingState.Idle)
        }
    }

    private fun onGuestListsRetrieved(guestLists: List<GuestList>, venues: List<Venue>) {
        val items = mutableListOf<GuestListItem>(GuestListItem.DescriptionItem)

        if (guestLists.isNotEmpty()) {
            val list = guestLists.sortedWith(Comparator { o1, o2 ->
                val time1 = o1.date?.time
                val time2 = o2.date?.time
                if (time1 != null && time2 != null && time1 != time2) {
                    time1.compareTo(time2)
                } else {
                    guestLists.indexOf(o1).compareTo(guestLists.indexOf(o2))
                }
            }).let {
                it.map { createGuestListItem(it, venues) }
            }
            items.add(GuestListItem.ListHeaderItem)
            items.addAll(list)
        }
        _items.postValue(items)
    }

    private fun createGuestListItem(
        guestList: GuestList,
        venues: List<Venue>
    ): GuestListItem.GuestInvitationItem {
        val status = processStatus(guestList).asEnumOrDefault<InviteStatus>()
        val venue = guestList.venue
        val imageUrl = venues.firstOrNull { it.id == venue.id }?.let {
            it.house.get(it.document)?.houseImageSet?.mediumPng
        }
        val address = buildAddress(venue.venueAddress.lines)
        return GuestListItem.GuestInvitationItem(
            id = guestList.id,
            title = guestList.name,
            location = venue.name,
            address = address,
            date = guestList.date,
            imageUrl = imageUrl,
            status = status
        )
    }

    private fun processStatus(guestList: GuestList): String {
        val result = guestList.invites.firstOrNull()
        for (invite in guestList.invites) {
            if (invite.status != result?.status) return ""
        }
        return result?.status ?: ""
    }

    private fun buildAddress(lines: List<String>?): String? {
        if (lines.isNullOrEmpty()) return null
        return lines.joinToString(", ")
    }

    fun logClickNewInvitation() {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.GuestsLandingNewListStart,
            AnalyticsManager.HouseGuest.buildParams(membershipType = userManager.membershipType)
        )
    }
}

