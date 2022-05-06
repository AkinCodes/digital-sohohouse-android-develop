package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.TableReservation
import retrofit2.Call

class GetTableBookingsRequest(
    private val fromDate: String? = null,
    private val toDate: String? = null,
    private val status: String? = null,
) : CoreAPIRequest<List<TableReservation>> {

    companion object {
        const val STATUS_UPCOMING = "upcoming"
    }

    override fun createCall(api: CoreApi): Call<out List<TableReservation>> {
        return api.getTableBookings(status, fromDate, toDate)
    }

}