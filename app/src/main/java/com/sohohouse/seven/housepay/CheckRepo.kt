package com.sohohouse.seven.housepay

import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.common.extensions.formatWithCommas
import com.sohohouse.seven.network.core.models.Wallet
import com.sohohouse.seven.network.core.models.housepay.Check
import com.sohohouse.seven.network.core.models.housepay.Payment

interface CheckRepo {

    companion object {
        private const val INCLUDE_PAYMENTS = "payments"
        private const val INCLUDE_LOCATION = "location"
        private const val INCLUDE_VENUE = "venue"
        private const val DEFAULT_ITEMS_PER_PAGE = 10

        val DEFAULT_GET_CHECK_INCLUDES = arrayOf(
            INCLUDE_LOCATION,
            INCLUDE_PAYMENTS,
            INCLUDE_VENUE,
        ).formatWithCommas()

        val DEFAULT_GET_CHECKS_INCLUDES = arrayOf(
            INCLUDE_LOCATION,
            INCLUDE_PAYMENTS,
            INCLUDE_VENUE,
        ).formatWithCommas()

        val OPEN_CHECKS_STATUS_FILTER = arrayOf(
            Check.STATUS_OPEN,
        ).formatWithCommas()

        val DEFAULT_CHECK_STATUS_FILTER = arrayOf(
            Check.STATUS_OPEN,
            Check.STATUS_CLOSED,
            Check.STATUS_PAID
        ).formatWithCommas()

        val INCLUDE_CHECK_CLOSED_FILTER = arrayOf(
            Check.STATUS_CLOSED,
            Check.STATUS_PAID
        ).formatWithCommas()
    }

    suspend fun getChecks(
        page: Int,
        perPage: Int = DEFAULT_ITEMS_PER_PAGE,
        status: String? = DEFAULT_CHECK_STATUS_FILTER,
        include: String? = DEFAULT_GET_CHECKS_INCLUDES
    ): ApiResponse<List<Check>>

    suspend fun getCheck(
        id: String,
        include: String? = DEFAULT_GET_CHECK_INCLUDES
    ): ApiResponse<Check>

    suspend fun getWallets(): ApiResponse<List<Wallet>>

    suspend fun postCheckDiscount(
        id: String,
        include: String? = DEFAULT_GET_CHECK_INCLUDES
    ): ApiResponse<Check>

    suspend fun payCheckByCard(
        checkId: String,
        cardId: String,
        cardAmountCents: Int,
        tipCents: Int,
        creditCents: Int,
    ): ApiResponse<Payment>

    suspend fun emailReceipt(
        checkId: String
    ): ApiResponse<Unit>

}