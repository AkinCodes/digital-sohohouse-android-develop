package com.sohohouse.seven.housepay.checkdetail.open

import com.sohohouse.seven.base.error.DisplayableError
import com.sohohouse.seven.housepay.checkdetail.CheckItem
import com.sohohouse.seven.housepay.payment.PaymentMethod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

sealed class OpenCheckUiState {

    object Initial : OpenCheckUiState()

    object Loading : OpenCheckUiState()

    object CheckPaid : OpenCheckUiState()

    data class Empty(
        val updatedAt: Long
    ) : OpenCheckUiState()

    data class ErrorState(
        val details: DisplayableError
    ) : OpenCheckUiState()

    data class Working(
        val updatedAt: Long,
        val items: List<CheckItem>
    ) : OpenCheckUiState()

    data class Paying(
        val updatedAt: Long,
        val items: List<CheckItem>,
        val paymentMethod: PaymentMethod?,
        val paymentInProgress: Boolean
    ) : OpenCheckUiState()
}