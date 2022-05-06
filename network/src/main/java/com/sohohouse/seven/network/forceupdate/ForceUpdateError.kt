package com.sohohouse.seven.network.forceupdate

import com.sohohouse.seven.network.base.error.ServerError

sealed class ForceUpdateError {
    object UPDATE_REQUIRED : ServerError.ApiError()
    object SERVER_MAINTENANCE : ServerError.ApiError()
}