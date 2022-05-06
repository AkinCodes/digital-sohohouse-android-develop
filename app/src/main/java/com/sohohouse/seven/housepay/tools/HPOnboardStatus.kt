package com.sohohouse.seven.housepay.tools

import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.housepay.CheckRepo
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.models.Card
import com.sohohouse.seven.network.core.models.Wallet
import com.sohohouse.seven.network.core.split
import com.sohohouse.seven.payment.repo.CardRepo
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

class HPOnboardStatus @Inject constructor(
    val userManager: UserManager,
    val cardRepo: CardRepo,
    val checkRepo: CheckRepo
) {

    sealed class OnboardStatus {
        object Unknown : OnboardStatus()
        object Yes : OnboardStatus()
        object No : OnboardStatus()
    }

    private val handler = CoroutineExceptionHandler { _, exception ->
        Timber.d("CoroutineExceptionHandler got $exception")
    }

    suspend fun isComplete() = coroutineScope {
        val cardRequest: Deferred<Boolean> = async(handler) {
            val listCards: ApiResponse<List<Card>> = cardRepo.getPaymentMethods(true)
            listCards.split(
                ifSuccess = { return@async it.isNotEmpty() },
                ifError = { return@async false })
        }

        val walletRequest = async(handler) {
            val listCheck: ApiResponse<List<Wallet>> = checkRepo.getWallets()
            listCheck.split(
                ifSuccess = { return@async it.isNotEmpty() },
                ifError = { return@async false })
        }

        val one = cardRequest.await()
        val two = walletRequest.await()

        return@coroutineScope if (cardRequest.isCancelled && walletRequest.isCancelled) {
            OnboardStatus.Unknown
        } else if (one && two && userManager.didConsentHousePayTermsConditions) {
            OnboardStatus.Yes
        } else {
            OnboardStatus.No
        }
    }
}