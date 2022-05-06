package com.sohohouse.seven.network.places

import com.google.gson.annotations.SerializedName

data class PlacesResponse(
    @SerializedName("predictions") val predictions: List<City>,
    @SerializedName("error_message") val errorMessage: String,
    @SerializedName("status") private val _status: String,
) {
    enum class Status {
        OK,
        ZERO_RESULTS,
        OVER_QUERY_LIMIT,
        REQUEST_DENIED,
        INVALID_REQUEST,
        UNKNOWN_ERROR
    }

    val status: Status
        get() = try {
            Status.valueOf(_status)
        } catch (exception: IllegalArgumentException) {
            Status.UNKNOWN_ERROR
        }
}