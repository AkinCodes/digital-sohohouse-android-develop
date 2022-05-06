package com.sohohouse.seven.housepay.payment

import com.sohohouse.seven.FeatureFlags
import com.sohohouse.seven.common.error.ErrorReporter
import com.sohohouse.seven.network.core.models.Card
import com.sohohouse.seven.network.core.split
import com.sohohouse.seven.payment.PaymentCardStatus
import com.sohohouse.seven.payment.repo.CardRepo
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

interface CheckPaymentMethodManager {
    var selectedPaymentMethod: PaymentMethod?
    suspend fun fetchPaymentMethod(forceRefresh: Boolean)
    suspend fun fetchPaymentMethodIfNeeded()
}

class CheckPaymentMethodManagerImpl @Inject constructor(
    private val cardRepo: CardRepo,
    private val featureFlags: FeatureFlags
) : CheckPaymentMethodManager {

    init {
        Timber.d("init")
    }

    private var _selectedPaymentMethod: PaymentMethod? = null

    override var selectedPaymentMethod: PaymentMethod?
        get() = _selectedPaymentMethod
        set(value) {
            _selectedPaymentMethod = value
        }

    override suspend fun fetchPaymentMethod(forceRefresh: Boolean) {
        if (featureFlags.googlePay) {
            selectedPaymentMethod = PaymentMethod.GooglePay
        } else {
            findDefaultCard(forceRefresh)?.let {
                selectedPaymentMethod = PaymentMethod.PaymentCard(it)
            }
        }
    }

    override suspend fun fetchPaymentMethodIfNeeded() {
        if (this._selectedPaymentMethod != null) {
            return
        }
        fetchPaymentMethod(forceRefresh = true)
    }

    private suspend fun findDefaultCard(forceRefresh: Boolean): Card? {
        return cardRepo.getPaymentMethods(forceRefresh).split(
            ifSuccess = {
                it.firstOrNull { card ->
                    card.isPrimary && card.status == PaymentCardStatus.ACTIVE.name
                }?.let { primaryCard ->
                    return@split primaryCard
                }
                it.firstOrNull { card ->
                    card.status == PaymentCardStatus.ACTIVE.name
                }?.let { firstActiveCard ->
                    return@split firstActiveCard
                }
                it.firstOrNull()?.let { firstCard ->
                    return@split firstCard
                }
                null
            },
            ifError = {
                ErrorReporter.logException(Throwable(it.message))
                null
            }
        )
    }
}