package com.sohohouse.seven.housepay.housecredit

import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.models.housepay.HouseCredit
import javax.inject.Inject

interface HouseCreditRepo {

    suspend fun getHouseCredits(currencyCode: String): ApiResponse<HouseCredit>

}

class HouseCreditRepoImpl @Inject constructor(
    private val apiService: SohoApiService,
) : HouseCreditRepo {
    override suspend fun getHouseCredits(currencyCode: String): ApiResponse<HouseCredit> {
        return apiService.getHouseCredit(currencyCode)
    }
}