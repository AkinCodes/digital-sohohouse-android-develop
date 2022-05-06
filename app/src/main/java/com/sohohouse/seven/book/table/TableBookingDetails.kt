package com.sohohouse.seven.book.table

import com.sohohouse.seven.network.core.models.Menu
import com.sohohouse.seven.network.core.models.SlotLock
import com.sohohouse.seven.network.core.models.TableAvailabilities
import com.sohohouse.seven.network.core.models.TableReservation
import java.io.Serializable
import java.util.*

data class TableBookingDetails(
    val id: String,
    val name: String,
    val description: String,
    val specialNotes: String,
    val address: String,
    val country: String,
    val houseDetails: String,
    val imageUrl: String,
    val menus: List<Menu>,
    val availabilities: TableAvailabilities,
    var date: Date,
    val persons: Int,
    val venueId: String,
    val startTimeMills: Long,
    val venueDetails: TableBookingHouseDetailsItem,
    val formVenueInput: String,
    val formTimeInput: Date?,
    var slotLock: SlotLock? = null,
    var booking: TableReservation? = null
) : Serializable