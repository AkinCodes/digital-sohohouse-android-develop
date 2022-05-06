package com.sohohouse.seven.housepay.checkdetail.closed

import com.sohohouse.seven.housepay.checkdetail.receipt.EmailReceiptState

interface CheckReceiptCallbacks {
    val onPhoneNumberClick: (String) -> Unit
    val onEmailReceiptClick: (EmailReceiptState) -> Unit
    val onFaqsClick: () -> Unit
}