package com.sohohouse.seven.housepay.checkdetail.open.pay

import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.models.housepay.Check

sealed class PayCheckResult {
    data class PayCheckSuccess(val check: Check) : PayCheckResult()
    data class PayCheckFailure(
        val error: PayCheckError,
        val check: Check? = null
    ) : PayCheckResult()
}

sealed class PayCheckError(open val error: ApiResponse.Error?) {
    data class CardSyncFailed(override val error: ApiResponse.Error) : PayCheckError(error)
    object NoCheck : PayCheckError(null)
    object NoAmount : PayCheckError(null)
    object CheckClosed : PayCheckError(null)
    object NoPaymentMethod : PayCheckError(null)
    data class CheckOutOfDate(override val error: ApiResponse.Error?) : PayCheckError(error)
    data class Unknown(override val error: ApiResponse.Error?) : PayCheckError(error)
    data class CardError(override val error: ApiResponse.Error) : PayCheckError(error)
    data class PostDownloadFailed(override val error: ApiResponse.Error) : PayCheckError(error)
    data class InsufficientCredit(override val error: ApiResponse.Error) : PayCheckError(error)
    data class CreditFail(override val error: ApiResponse.Error) : PayCheckError(error)
    data class CreditPaidThenPayFailed(override val error: ApiResponse.Error) : PayCheckError(error)
    data class CheckOpenOnWorkStation(override val error: ApiResponse.Error) : PayCheckError(error)
}
