package com.sohohouse.seven.network.core.request

import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.models.ReportMember
import retrofit2.Call

data class PostReportMember(val message: String, val userID: String) : CoreAPIRequest<Void> {
    override fun createCall(api: CoreApi): Call<out Void> {
        val body =
            "===INFO===\nReported member global ID: $userID\n\n===MEMBER MESSAGE===\n$message"
        return api.postReportUser(ReportMember(body = body))
    }
}