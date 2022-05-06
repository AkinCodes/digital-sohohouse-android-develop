package com.sohohouse.seven.housepay

import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.map
import com.sohohouse.seven.network.core.models.Wallet
import com.sohohouse.seven.network.core.models.housepay.Check
import com.sohohouse.seven.network.core.models.housepay.PayCheckByCardInfo
import com.sohohouse.seven.network.core.models.housepay.Payment

class CheckRepoImpl constructor(
    private val apiService: SohoApiService
) : CheckRepo {

    override suspend fun getChecks(
        page: Int,
        perPage: Int,
        status: String?,
        include: String?
    ): ApiResponse<List<Check>> {
        return apiService.getChecks(page, perPage, status, include)
    }

    override suspend fun getCheck(
        id: String,
        include: String?
    ): ApiResponse<Check> {
        return apiService.getCheck(id, include)
    }

    override suspend fun postCheckDiscount(
        id: String,
        include: String?
    ): ApiResponse<Check> {
        return apiService.postCheckDiscount(
            id,
            include
        )
    }

    override suspend fun payCheckByCard(
        checkId: String,
        cardId: String,
        cardAmountCents: Int,
        tipCents: Int,
        creditCents: Int
    ): ApiResponse<Payment> {
        return apiService.payCheck(
            PayCheckByCardInfo(
                PayCheckByCardInfo.Data(
                    checkId = checkId,
                    amountCents = cardAmountCents,
                    cardId = cardId,
                    tipAmountCents = tipCents,
                    creditCents = creditCents
                )
            )
        )
    }

    override suspend fun getWallets(): ApiResponse<List<Wallet>> {
        return apiService.getWallets()
    }

    override suspend fun emailReceipt(checkId: String): ApiResponse<Unit> {
        return apiService.emailReceipt(checkId).map(
            ifSuccess = {
                ApiResponse.Success(Unit)
            },
            ifError = {
                it
            })
    }
}