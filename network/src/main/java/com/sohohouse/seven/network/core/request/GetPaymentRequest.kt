package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.common.PAYMENT_VENUE_ID_SOHO_HOUSE
import com.sohohouse.seven.network.core.models.Card
import retrofit2.Call

class GetPaymentRequest(private val isTransactional: Boolean = false) : CoreAPIRequest<List<Card>> {
    companion object {
        private const val FILTER_TRANSACTIONAL = "TRANSACTIONAL"
    }

    override fun createCall(api: CoreApi): Call<out List<Card>> {
        return if (isTransactional) {
            api.getPaymentCards(FILTER_TRANSACTIONAL, PAYMENT_VENUE_ID_SOHO_HOUSE)
        } else {
            api.getPaymentCards(venue = PAYMENT_VENUE_ID_SOHO_HOUSE)
        }
    }
}