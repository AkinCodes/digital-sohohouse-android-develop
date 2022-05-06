package com.sohohouse.seven.network.base.error

sealed class ServerError {
    object BAD_REQUEST : ServerError()
    object INVALID_CERT : ServerError()
    object NO_INTERNET : ServerError()
    object TIMEOUT : ServerError()
    object INVALID_RESPONSE : ServerError()
    object COMPLETE_MELTDOWN : ServerError()
    object NOT_FOUND : ServerError()
    object REQUEST_ALREADY_EXISTS : ServerError()

    open class ApiError(vararg val errorCodes: String = arrayOf("")) : ServerError()
}