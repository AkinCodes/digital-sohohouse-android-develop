package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.Card
import retrofit2.Call

class PatchDefaultPaymentRequest(private val id: String) : CoreAPIRequest<Card> {
    override fun createCall(api: CoreApi): Call<out Card> {
        val card = Card(isPrimary = true)
        card.id = id
        return api.patchPayment(id, card)
    }
}