package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.common.extensions.formatWithCommas
import com.sohohouse.seven.network.core.models.Checkin
import com.sohohouse.seven.network.core.models.PostTags
import com.sohohouse.seven.network.core.models.Venue
import moe.banana.jsonapi2.HasMany
import moe.banana.jsonapi2.ObjectDocument
import retrofit2.Call

class PostRollCallRequest(
    private val status: String,
    private val venueId: String? = null,
    private val parentId: String? = null,
    private val city: String? = null,
    private val theme: String? = null,
    private val includeReplies: Boolean = true,
    private val includeProfile: Boolean = true,
) : CoreAPIRequest<Checkin> {

    companion object {
        private const val REPLIES_INCLUDE_TYPE = "replies"
        private const val PROFILE_INCLUDE_TYPE = "profile"
        private const val REPLIES_PROFILE_INCLUDE_TYPE = "replies.profile"
    }

    override fun createCall(api: CoreApi): Call<out Checkin> {
        val venue = if (venueId != null) Venue().apply { id = venueId } else null
        val tags =
            if (!city.isNullOrEmpty() || !theme.isNullOrEmpty()) PostTags(city, theme) else null

        val checkin = Checkin(status = status, parentId = this.parentId, tags = tags).apply {
            venue?.let { this.venueResource = HasMany(venue) }
        }
        var includes = arrayOf<String>()

        if (includeReplies) {
            includes = includes.plus(REPLIES_INCLUDE_TYPE)
        }
        if (includeProfile) {
            includes = includes.plus(PROFILE_INCLUDE_TYPE)
            includes = includes.plus(REPLIES_PROFILE_INCLUDE_TYPE)
        }
        return api.postRollCall(ObjectDocument(checkin).apply {
            venue?.let { addInclude(venue) }
        }, includes.formatWithCommas())
    }
}