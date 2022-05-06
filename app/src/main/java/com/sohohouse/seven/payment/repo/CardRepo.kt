package com.sohohouse.seven.payment.repo

import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.models.Card

interface CardRepo {

    suspend fun getPaymentMethods(forceRefresh: Boolean): ApiResponse<List<Card>>

    class Impl constructor(
        private val apiService: SohoApiService
    ) : CardRepo {

        private var cached: List<Card>? = null

        override suspend fun getPaymentMethods(forceRefresh: Boolean): ApiResponse<List<Card>> {
            if (!forceRefresh) {
                cached?.let {
                    return ApiResponse.Success(it)
                }
            }
            return apiService.getPaymentMethods().also {
                if (it is ApiResponse.Success) {
                    this.cached = it.response
                }
            }
        }
    }

}

