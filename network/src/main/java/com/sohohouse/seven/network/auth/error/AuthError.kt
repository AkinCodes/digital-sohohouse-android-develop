package com.sohohouse.seven.network.auth.error

import com.sohohouse.seven.network.base.error.ServerError

sealed class AuthError {
    object INVALID_CREDENTIALS : ServerError.ApiError()
    object INVALID_OAUTH_TOKEN : ServerError.ApiError()
}