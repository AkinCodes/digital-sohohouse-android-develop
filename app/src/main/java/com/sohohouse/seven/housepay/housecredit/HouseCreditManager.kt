package com.sohohouse.seven.housepay.housecredit

import com.sohohouse.seven.common.error.ErrorReporter
import com.sohohouse.seven.housepay.amountPayableByCredit
import com.sohohouse.seven.housepay.tips.CheckTipsManager
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.doOnComplete
import com.sohohouse.seven.network.core.models.housepay.Check
import com.sohohouse.seven.network.core.models.housepay.HouseCredit
import com.sohohouse.seven.network.core.split
import javax.inject.Inject

interface HouseCreditManager {
    val availableHouseCredit: HouseCredit?
    var usingHouseCreditCents: Int
    fun useCheck(check: Check)
    suspend fun fetchHouseCreditIfNeeded()
    suspend fun fetchHouseCredit(): ApiResponse<HouseCredit>
}

class HouseCreditManagerImpl @Inject constructor(
    private val houseCreditRepo: HouseCreditRepo,
    private val tipsManager: CheckTipsManager
) : HouseCreditManager {

    private var check: Check? = null
    private var _availableHouseCredit: HouseCredit? = null
    private var _usingHouseCreditCents = 0

    override val availableHouseCredit: HouseCredit?
        get() = _availableHouseCredit

    override var usingHouseCreditCents: Int
        get() {
            if (_usingHouseCreditCents > 0) {
                _usingHouseCreditCents = minOf(
                    _usingHouseCreditCents,
                    check?.amountPayableByCredit(tipsManager.tipValueCents) ?: 0
                )
            }
            return _usingHouseCreditCents
        }
        set(value) {
            _usingHouseCreditCents = value
        }

    override suspend fun fetchHouseCreditIfNeeded() {
        if (this._availableHouseCredit != null) {
            return
        }
        fetchHouseCredit()
    }

    override suspend fun fetchHouseCredit(): ApiResponse<HouseCredit> {
        return houseCreditRepo.getHouseCredits(check?.currency ?: "").doOnComplete(
            ifSuccess = {
                this._availableHouseCredit = it
            }
        ) {
            ErrorReporter.logException(Throwable(it.message))
        }
    }

    override fun useCheck(check: Check) {
        this.check = check
    }

}
