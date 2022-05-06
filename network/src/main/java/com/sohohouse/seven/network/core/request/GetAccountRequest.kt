package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.common.extensions.formatWithCommas
import com.sohohouse.seven.network.core.models.Account
import retrofit2.Call

class GetAccountRequest(
    private val includeMembership: Boolean = false,
    private val includeProfile: Boolean = false,
    private val includeLocalHouse: Boolean = false,
    private val includeFavouriteHouse: Boolean = false,
    private val includeFavouriteContentCategories: Boolean = false,
    private val includeLastAttendance: Boolean = false,
    private val includeInterests: Boolean = false,
) : CoreAPIRequest<Account> {
    companion object {
        private const val MEMBERSHIP_INCLUDE_TYPE = "membership"
        private const val PROFILE_INCLUDE_TYPE = "profile"
        private const val LOCAL_HOUSE_INCLUDE_TYPE = "local_house"
        private const val FAVOURITE_HOUSE_INCLUDE_TYPE = "favorite_venues"
        private const val FAVOURITE_CONTENT_CATEGORIES_INCLUDE_TYPE = "favorite_content_categories"
        private const val LATEST_ATTENDANCE = "latest_attendance"
        private const val INTERESTS_INCLUDE_TYPE = "profile.interests"
    }

    override fun createCall(api: CoreApi): Call<out Account> {
        var includes = arrayOf<String>()
        if (includeMembership) {
            includes = includes.plus(MEMBERSHIP_INCLUDE_TYPE)
        }
        if (includeProfile) {
            includes = includes.plus(PROFILE_INCLUDE_TYPE)
        }
        if (includeLocalHouse) {
            includes = includes.plus(LOCAL_HOUSE_INCLUDE_TYPE)
        }
        if (includeFavouriteHouse) {
            includes = includes.plus(FAVOURITE_HOUSE_INCLUDE_TYPE)
        }
        if (includeFavouriteContentCategories) {
            includes = includes.plus(FAVOURITE_CONTENT_CATEGORIES_INCLUDE_TYPE)
        }
        if (includeLastAttendance) {
            includes = includes.plus(LATEST_ATTENDANCE)
        }
        if (includeInterests) {
            includes = includes.plus(INTERESTS_INCLUDE_TYPE)
        }
        return api.getAccount(includes.formatWithCommas())
    }
}