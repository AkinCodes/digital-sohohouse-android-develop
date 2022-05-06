package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.Checkin
import com.sohohouse.seven.network.core.models.CursorMeta
import com.squareup.moshi.Moshi
import moe.banana.jsonapi2.JsonBuffer
import retrofit2.Call

class GetNoticeboardPostsRequest(
    private val venueIds: String? = null,
    private val cities: String? = null,
    private val topics: String? = null,
    private val profileId: String? = null,
    private val includeProfile: Boolean = false,
    private val includeVenue: Boolean = true,
    override var nextCursor: String? = null,
    override var perPage: Int? = null,
) : CoreAPIRequestCursorPagable<List<Checkin>> {

    companion object {
        private const val PROFILE_INCLUDE_TYPE = "profile"
        private const val VENUES_INCLUDE_TYPE = "venues"
        private const val REACTION_INCLUDE_TYPE = "user_reaction"

        fun getMeta(response: List<Checkin>): CursorMeta? {
            val adapter = Moshi.Builder().build().adapter(CursorMeta::class.java)
            if (response.isNotEmpty() && response[0].document.meta != null) {
                @Suppress("UNCHECKED_CAST")
                return (response[0].document.meta as JsonBuffer<CursorMeta>).get(adapter)
            }
            return null
        }
    }

    override fun createCall(api: CoreApi): Call<out List<Checkin>> {
        val includes = mutableListOf<String>(REACTION_INCLUDE_TYPE)

        if (includeProfile) {
            includes.add(PROFILE_INCLUDE_TYPE)
        }
        if (includeVenue) {
            includes.add(VENUES_INCLUDE_TYPE)
        }
        return api.getRollCall(
            venueIds,
            cities,
            topics,
            profileId,
            includes.joinToString(separator = ","),
            perPage,
            nextCursor
        )
    }

    override fun getMeta(response: List<Checkin>): CursorMeta? {
        return GetNoticeboardPostsRequest.getMeta(response)
    }
}