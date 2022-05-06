package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.common.PAYMENT_VENUE_ID_SOHO_HOUSE
import com.sohohouse.seven.network.core.models.Card
import com.sohohouse.seven.network.core.models.NewCard
import com.sohohouse.seven.network.core.models.NewForm
import com.sohohouse.seven.network.core.models.Venue
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.ObjectDocument
import retrofit2.Call

class PostPaymentCardRequest(private val encryptedPayload: String, private val formId: String) :
    CoreAPIRequest<Card> {
    override fun createCall(api: CoreApi): Call<out Card> {
        val document = ObjectDocument<NewCard>()

        val venue = Venue()
        venue.id = PAYMENT_VENUE_ID_SOHO_HOUSE

        val paymentForm = NewForm(venue = HasOne(venue))
        paymentForm.id = formId

        val newCard = NewCard(cardPayload = encryptedPayload, form = HasOne(paymentForm))
        document.set(newCard)
        return api.postPaymentCard(document)
    }
}