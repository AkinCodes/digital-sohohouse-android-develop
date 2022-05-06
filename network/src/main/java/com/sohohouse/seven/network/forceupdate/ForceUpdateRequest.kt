package com.sohohouse.seven.network.forceupdate

import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.request.APIRequest
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import java.net.HttpURLConnection.HTTP_UNAVAILABLE

class ForceUpdateRequest(
    private val version: String,
) : APIRequest<ForceUpdateApi, Void> {

    companion object {
        private const val UPDATE_REQUIRED_ERROR_CODE = 426
        private const val FORCE_UPDATE_PLATFORM = "ANDROID"
        private const val SERVER_MAINTENANCE = "MAINTENANCE"
        private const val ERRORS_RESPONSE = "errors"
        private const val ERROR_CODE = "code"
    }

    override fun createCall(api: ForceUpdateApi): Call<out Void> {
        return api.checkForceUpdate(version, FORCE_UPDATE_PLATFORM)
    }

    override fun mapError(statusCode: Int, rawBody: String): ServerError {
        return when (statusCode) {
            UPDATE_REQUIRED_ERROR_CODE -> ForceUpdateError.UPDATE_REQUIRED
            HTTP_UNAVAILABLE -> {
                val jsonArray = JSONObject(rawBody).opt(ERRORS_RESPONSE) as JSONArray
                val jsonObject = jsonArray.getJSONObject(0)
                val code = jsonObject.get(ERROR_CODE) as String?
                return if (SERVER_MAINTENANCE == code) {
                    ForceUpdateError.SERVER_MAINTENANCE
                } else {
                    ServerError.INVALID_RESPONSE
                }
            }
            else -> throw IllegalStateException("Unexpected login response")
        }
    }
}