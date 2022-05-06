package com.sohohouse.seven.housepay.checkdetail.open.pay

import com.sohohouse.seven.network.core.models.housepay.Check
import javax.inject.Inject

interface ValidateCheck {
    operator fun invoke(check: Check): PayCheckError?
}

class ValidateCheckImpl @Inject constructor() : ValidateCheck {
    override fun invoke(check: Check): PayCheckError? {
        return when {
            check.status != Check.STATUS_OPEN && check.remainingCents > 0 -> {
                PayCheckError.Unknown(null)
            }
            check.status != Check.STATUS_OPEN -> {
                PayCheckError.CheckClosed
            }
            check.remainingCents <= 0 -> {
                PayCheckError.NoAmount
            }
            else -> null
        }
    }
}