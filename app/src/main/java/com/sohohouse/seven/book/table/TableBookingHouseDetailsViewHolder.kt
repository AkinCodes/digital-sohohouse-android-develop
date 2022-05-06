package com.sohohouse.seven.book.table

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.navigation.IntentUtils
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.databinding.ItemTableBookingHouseDetailsBinding
import com.sohohouse.seven.network.core.models.Venue
import java.io.Serializable


class TableBookingHouseDetailsViewHolder(
    private val binding: ItemTableBookingHouseDetailsBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: TableBookingHouseDetailsItem) {
        with(binding) {
            venueHours.setTextOrHide(item.hours)
            venueAddress.setTextOrHide(item.address)
            venuePhoneNo.setTextOrHide(item.phoneNumber)
            venueMapLink.setVisible(item.name.isNotEmpty() || item.address.isNotEmpty())

            venueMapLink.clicks {
                context.startActivitySafely(
                    IntentUtils.viewOnMapIntent(
                        item.name.nullIfBlank() ?: item.address
                    )
                )
            }
            venuePhoneNo.clicks {
                context.startActivitySafely(IntentUtils.dialIntent(item.phoneNumber))
            }
        }
    }

}

data class TableBookingHouseDetailsItem(
    val name: String,
    val hours: String,
    val phoneNumber: String,
    val address: String
) : Serializable {
    companion object {
        fun from(
            venue: Venue?,
            parentVenue: Venue?,
            stringProvider: StringProvider
        ): TableBookingHouseDetailsItem {
            val restaurant = venue?.restaurant

            val dayAndTimesPlaceholder =
                stringProvider.getString(R.string.opening_hours_day_with_times_placeholder)
            val venueOperatingHours = venue.getVenueOperatingHours(dayAndTimesPlaceholder)
                .let {
                    if (it.isEmpty()) {
                        return@let parentVenue.getVenueOperatingHours(dayAndTimesPlaceholder)
                    }
                    it
                }

            val phone = venue?.phoneNumber?.let {
                if (it.isEmpty()) {
                    return@let parentVenue?.phoneNumber
                }
                it
            }

            val address = venue?.buildAddress(singleLine = true)
                .let {
                    if (it.isNullOrBlank()) {
                        return@let parentVenue?.buildAddress(singleLine = true)
                    }
                    it
                }

            return TableBookingHouseDetailsItem(
                restaurant?.bookingPartnerName ?: "",
                venueOperatingHours.concatenateWithNewLine(),
                phone ?: "",
                address ?: ""
            )
        }
    }

}