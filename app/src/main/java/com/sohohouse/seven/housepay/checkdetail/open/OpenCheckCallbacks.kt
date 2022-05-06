package com.sohohouse.seven.housepay.checkdetail.open

import com.sohohouse.seven.housepay.checkdetail.TipOptionUiModel

interface OpenCheckCallbacks {
    val onRetryPayCheck: () -> Unit
    val onHouseCreditTsAndCsClick: () -> Unit
    val onUseHouseCreditClick: () -> Unit
    val onRetryFetchHouseCredit: () -> Unit
    val onU27AlertBannerClick: () -> Unit
    val onRetryApplyDiscount: () -> Unit
    val onTipOptionClick: (tipOption: TipOptionUiModel) -> Unit
}