package com.sohohouse.seven.common.prefs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.network.core.models.Venue

interface LocalVenueProvider {

    val localVenue: LiveData<Venue>

    class Impl(
        venueRepo: VenueRepo,
        private val venueAttendanceProvider: VenueAttendanceProvider,
        private val userManager: UserManager
    ) : LocalVenueProvider {

        private val _localVenue = MutableLiveData<Venue>()
        override val localVenue: LiveData<Venue> get() = _localVenue

        init {
            venueRepo.liveVenues().observeForever(Observer {
                val localHouse = venueAttendanceProvider.attendingVenue
                    ?: it.findById(userManager.localHouseId)
                    ?: return@Observer
                if (localHouse.id != _localVenue.value?.id) {
                    _localVenue.value = localHouse
                }
            })
        }

    }
}