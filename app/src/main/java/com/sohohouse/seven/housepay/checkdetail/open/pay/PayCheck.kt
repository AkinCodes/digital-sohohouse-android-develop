package com.sohohouse.seven.housepay.checkdetail.open.pay

import com.sohohouse.seven.housepay.CheckRepo
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.ifError
import com.sohohouse.seven.network.core.models.housepay.Check
import com.sohohouse.seven.network.core.models.housepay.Payment
import com.sohohouse.seven.network.core.split
import com.sohohouse.seven.payment.repo.CardRepo
import javax.inject.Inject

interface PayCheck {
    suspend operator fun invoke(
        params: PayCheckParams
    ): PayCheckResult
}

sealed class PayCheckPaymentInfo {
    data class Card(val cardId: String) : PayCheckPaymentInfo()
    data class GooglePay(val key: String) : PayCheckPaymentInfo()
}

data class PayCheckParams(
    val check: Check?,
    val payCheckPaymentInfo: PayCheckPaymentInfo?,
    val amountCents: Int,
    val creditCents: Int,
    val tipCents: Int
)

class PayCheckImpl @Inject constructor(
    private val validateCheck: ValidateCheck,
    private val cardRepo: CardRepo,
    private val checkRepo: CheckRepo
) : PayCheck {

    override suspend fun invoke(
        params: PayCheckParams
    ): PayCheckResult {

        params.check ?: return PayCheckResult.PayCheckFailure(
            PayCheckError.NoCheck
        )

        params.payCheckPaymentInfo ?: return PayCheckResult.PayCheckFailure(
            PayCheckError.NoPaymentMethod
        )

        cardRepo.getPaymentMethods(forceRefresh = true).ifError {
            return PayCheckResult.PayCheckFailure(PayCheckError.CardSyncFailed(it))
        }

        validateCheck(params.check)?.let {
            return PayCheckResult.PayCheckFailure(it)
        }

        return when (params.payCheckPaymentInfo) {
            is PayCheckPaymentInfo.Card -> {
                checkRepo.payCheckByCard(
                    checkId = params.check.id,
                    cardId = params.payCheckPaymentInfo.cardId,
                    cardAmountCents = params.amountCents,
                    tipCents = params.tipCents,
                    creditCents = params.creditCents
                ).split(
                    ifSuccess = {
                        onPayCheckSuccess(it)
                    }, ifError = {
                        onPayCheckError(it, params.check)
                    }
                )
            }
            is PayCheckPaymentInfo.GooglePay -> TODO()
        }

    }


    private suspend fun onPayCheckError(apiError: ApiResponse.Error, check: Check): PayCheckResult {
        val payCheckError = apiError.payError()
        return onFailureDownloadCheck(payCheckError, check.id)
    }

    private suspend fun onFailureDownloadCheck(
        payCheckError: PayCheckError,
        id: String
    ): PayCheckResult {
        return checkRepo.getCheck(id).split(
            ifSuccess = {
                PayCheckResult.PayCheckFailure(payCheckError, it)
            },
            ifError = {
                PayCheckResult.PayCheckFailure(payCheckError)
            }
        )
    }

    private suspend fun onPayCheckSuccess(payment: Payment): PayCheckResult {
        payment.check?.let {
            return PayCheckResult.PayCheckSuccess(it)
        }
        return checkRepo.getCheck(payment.checkId ?: "").split(
            ifSuccess = {
                PayCheckResult.PayCheckSuccess(it)
            },
            ifError = {
                PayCheckResult.PayCheckFailure(PayCheckError.PostDownloadFailed(it))
            }
        )
    }
}

private fun ApiResponse.Error.payError(): PayCheckError {
    return when (firstErrorCode()) {
        //TODO
        else -> PayCheckError.Unknown(this)
    }
}
