package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.common.extensions.formatWithCommas
import com.sohohouse.seven.network.core.models.Checkin
import retrofit2.Call

@Deprecated("Use SohoApiService instead")
class GetNoticeboardPostRequest(
    private val postId: String, private val includeReplies: Boolean = true,
    private val includeProfile: Boolean = true,
    private val includeVenues: Boolean = true,
) : CoreAPIRequest<Checkin> {

    companion object {
        private const val REPLIES_INCLUDE_TYPE = "replies"
        private const val VENUES_INCLUDE_TYPE = "venues"
        private const val PROFILE_INCLUDE_TYPE = "profile"
        private const val REPLIES_PROFILE_INCLUDE_TYPE = "replies.profile"
    }

    override fun createCall(api: CoreApi): Call<out Checkin> {
        var includes = arrayOf<String>()
        if (includeReplies) {
            includes = includes.plus(REPLIES_INCLUDE_TYPE)
        }
        if (includeProfile) {
            includes = includes.plus(PROFILE_INCLUDE_TYPE)
            includes = includes.plus(REPLIES_PROFILE_INCLUDE_TYPE)
        }
        if (includeVenues) {
            includes = includes.plus(VENUES_INCLUDE_TYPE)
        }
        includes = includes.plus("user_reaction")
        return api.getPost(postId, includes.formatWithCommas())
    }

}