package com.sohohouse.seven

import com.sohohouse.seven.network.core.models.OperatingHours
import com.sohohouse.seven.network.core.models.Venue
import com.sohohouse.seven.network.core.models.VenueAddress
import com.sohohouse.seven.network.core.models.VenueIcons

object VenueTestHelper {
    fun mockVenue() = Venue(
        _name = "Soho House Amsterdam",
        venueAddress = VenueAddress(locality = "Amsterdam", country = "Netherlands"),
        venueIcons = VenueIcons("", "", "darkIcon.png", ""),
        operatingHours = OperatingHours()
    ).apply { id = "AMS" }
}