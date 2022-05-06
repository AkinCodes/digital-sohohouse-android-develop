package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.Inquiry
import com.sohohouse.seven.network.core.models.NewInquiry
import moe.banana.jsonapi2.ObjectDocument
import retrofit2.Call
import java.net.HttpURLConnection

class PostInquiryRequest(
    private val inquiryType: String,
    private val reason: String,
    private val venueType: String?,
    private val venueName: String?,
    private val inquiryMessage: String?,
) : CoreAPIRequest<Inquiry> {
    override fun createCall(api: CoreApi): Call<Inquiry> {
        val document = ObjectDocument<NewInquiry>()
        val inquiry = NewInquiry(
            inquiryType = inquiryType,
            reason = reason,
            venueType = venueType,
            venueName = venueName,
            body = inquiryMessage
        )
        document.set(inquiry)
        return api.postInquiry(document)
    }

    override fun mapError(statusCode: Int, rawBody: String): ServerError {
        if (statusCode == HttpURLConnection.HTTP_BAD_REQUEST) {
            return ServerError.BAD_REQUEST
        }
        return super.mapError(statusCode, rawBody)
    }
}