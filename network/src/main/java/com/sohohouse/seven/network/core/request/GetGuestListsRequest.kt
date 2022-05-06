package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.common.extensions.formatWithCommas
import com.sohohouse.seven.network.core.models.GuestList
import retrofit2.Call

class GetGuestListsRequest(
    private val dateFrom: String? = null,
    private val dateTo: String? = null,
    private val include: Array<String>? = arrayOf(INCLUDE_INVITES, INCLUDE_VENUE),
) : CoreAPIRequest<List<GuestList>> {

    override fun createCall(api: CoreApi): Call<out List<GuestList>> {
        return api.getGuestLists(dateFrom = dateFrom,
            dateTo = dateTo,
            include = include?.formatWithCommas())
    }

    companion object {
        private const val INCLUDE_VENUE = "venue"
        private const val INCLUDE_INVITES = "invites"
    }
}