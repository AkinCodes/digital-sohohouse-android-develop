package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.GuestList
import com.sohohouse.seven.network.core.models.JSON_API_TYPE_VENUES
import com.sohohouse.seven.network.core.models.PostGuestList
import moe.banana.jsonapi2.HasOne
import retrofit2.Call

class PostGuestListRequest(
    private val name: String, private val venueId: String,
    private val date: String, private val notes: String?,
) : CoreAPIRequest<GuestList> {
    override fun createCall(api: CoreApi): Call<out GuestList> {
        val guestList = PostGuestList(date = date,
            venue = HasOne(JSON_API_TYPE_VENUES, venueId),
            notes = notes ?: "",
            name = name)
        return api.postGuestList(guestList)
    }


}