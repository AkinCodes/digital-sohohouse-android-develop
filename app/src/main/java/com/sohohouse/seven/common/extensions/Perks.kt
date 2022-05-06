package com.sohohouse.seven.common.extensions

import com.sohohouse.seven.common.house.HouseType
import com.sohohouse.seven.network.core.models.Perk
import com.sohohouse.seven.network.core.models.Venue

fun Perk.isValid(): Boolean {
    return (this.title?.isNotEmpty() == true && this.region?.isNotEmpty() == true)
}

fun Perk.isValidAndCwh(
    venues: List<Venue>,
    onSuccess: (relatedPerk: Perk, venueName: String) -> Unit,
    onNotCwh: (relatedPerk: Perk) -> Unit,
    onInvalid: () -> Unit = {}
) {
    if (this.isValid()) {
        val perkVenue: List<Venue>
        if (this.region == HouseType.CWH.name) {
            perkVenue = venues.filter {
                it.id == this.venues?.get()?.id
            }

            if (perkVenue.isNotEmpty()) {
                onSuccess(this, perkVenue[0].name)
            } else {
                onInvalid()
            }

        } else {
            onNotCwh(this)
        }
    } else {
        onInvalid()
    }
}