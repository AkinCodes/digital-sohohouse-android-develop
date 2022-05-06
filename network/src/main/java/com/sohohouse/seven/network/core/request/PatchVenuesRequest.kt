package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.Venue
import moe.banana.jsonapi2.ResourceIdentifier
import retrofit2.Call

class PatchVenuesRequest(private val venuesIds: List<String>) : CoreAPIRequest<List<Venue>> {
    override fun createCall(api: CoreApi): Call<out List<Venue>> {
        val venues = venuesIds.map { ResourceIdentifier("venues", it) }
        return api.patchVenues(venues.toTypedArray())
    }
}