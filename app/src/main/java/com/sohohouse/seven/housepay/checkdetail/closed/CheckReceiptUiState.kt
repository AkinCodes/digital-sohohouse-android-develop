package com.sohohouse.seven.housepay.checkdetail.closed

import com.sohohouse.seven.base.error.DisplayableError
import com.sohohouse.seven.housepay.checkdetail.CheckItem

sealed class CheckReceiptUiState {
    data class Receipt(
        val items: List<CheckItem>,
        val updatedAt: Long
    ) : CheckReceiptUiState()

    object Loading : CheckReceiptUiState()
    object Initial : CheckReceiptUiState()
    data class ErrorState(val error: DisplayableError) : CheckReceiptUiState()
}
