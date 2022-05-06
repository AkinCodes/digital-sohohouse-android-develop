package com.sohohouse.seven.common.prefs

import com.sohohouse.seven.common.extensions.isOpenNow
import com.sohohouse.seven.common.interactors.AccountInteractor
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.network.core.models.Venue
import java.util.*

interface VenueAttendanceProvider {
    val attendingVenue: Venue?
    val isAttending: Boolean
    val venueAttendanceId: String
    val isFirstVenueVisit: Boolean

    class Impl(
        private val venueRepo: VenueRepo,
        private val accountInteractor: AccountInteractor
    ) : VenueAttendanceProvider {

        override val attendingVenue: Venue?
            get() {
                return if (isAttending) latestAttendedVenue else null
            }
        override val isAttending: Boolean
            get() {
                return (accountInteractor.userAccount?.attendance != null && latestAttendedVenue.isOpenNow())
            }

        override val venueAttendanceId: String
            get() {
                return accountInteractor.userAccount?.attendance?.id ?: ""
            }

        override val isFirstVenueVisit: Boolean
            get() {
                return accountInteractor.userAccount?.attendance?.firstVisit ?: false
            }

        private val lastAttendedVenueId get() = accountInteractor.userAccount?.attendance?.venueResource?.get()?.id

        private val latestAttendedVenue: Venue?
            get() {
                val venueID = lastAttendedVenueId ?: return null
                return venueRepo.venues().findById(venueID.toUpperCase(Locale.US))
            }
    }
}