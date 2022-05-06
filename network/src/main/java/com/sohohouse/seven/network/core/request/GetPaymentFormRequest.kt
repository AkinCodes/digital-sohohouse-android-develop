package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.common.PAYMENT_VENUE_ID_SOHO_HOUSE
import com.sohohouse.seven.network.core.models.Form
import com.sohohouse.seven.network.core.models.NewForm
import com.sohohouse.seven.network.core.models.Venue
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.ObjectDocument
import retrofit2.Call

class GetPaymentFormRequest() : CoreAPIRequest<Form> {

    override fun createCall(api: CoreApi): Call<out Form> {
        val document = ObjectDocument<NewForm>()
        val venue = Venue()
        venue.id = PAYMENT_VENUE_ID_SOHO_HOUSE
        val paymentForm = NewForm(formType = "ENCRYPTED", venue = HasOne(venue))
        document.set(paymentForm)
        document.addInclude(venue)
        return api.getPaymentForm(document)
    }
}