package com.sohohouse.seven.common.views.locationlist.table

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sohohouse.seven.common.house.HouseType
import com.sohohouse.seven.network.core.models.Venue
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TableBookingSearchByCityTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var venues: List<Venue>

    @Before
    fun setUpVenues() {

        venues = listOf(
            Venue(_name = "1", _city = "London", _region = "UK", _venueType = "HOUSE"),
            Venue(_name = "2", _city = "London", _region = "UK", _venueType = "GYM"),
            Venue(_name = "3", _city = "London", _region = "UK", _venueType = "RESTAURANT"),
            Venue(
                _name = "5",
                _city = "Vancouver",
                _region = "NORTH_AMERICA",
                _venueType = "RESTAURANT"
            ),
            Venue(_name = "8", _city = "Vancouver", _region = "NORTH_AMERICA", _venueType = "CWH"),
            Venue(_name = "4", _city = "Istanbul", _region = "EUROPE", _venueType = "HOUSE"),
            Venue(_name = "6", _city = "Vienna", _region = "EUROPE", _venueType = "HOUSE"),
            Venue(_name = "7", _city = "Vienna", _region = "EUROPE", _venueType = "HOUSE"),
        )
    }

    @Test
    fun `get grouped venues by city`() {
        val filtered = venues.filter { it.venueType != HouseType.GYM.name }.groupBy { it.city }
            .filter { it.value.size > 1 }
        Assert.assertEquals(3, filtered.size)
    }

}