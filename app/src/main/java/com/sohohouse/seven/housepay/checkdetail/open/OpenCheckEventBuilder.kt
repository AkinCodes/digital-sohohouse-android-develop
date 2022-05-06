package com.sohohouse.seven.housepay.checkdetail.open

import com.sohohouse.seven.R
import com.sohohouse.seven.base.error.ErrorHelper
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.housepay.amountPayableByCredit
import com.sohohouse.seven.housepay.maxCustomTip
import com.sohohouse.seven.housepay.housecredit.HouseCreditManager
import com.sohohouse.seven.housepay.tips.CheckTipsManager
import com.sohohouse.seven.housepay.tips.Tip
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.models.housepay.Check

object OpenCheckEventBuilder {

    fun buildDiscountWarningEvent(
        stringProvider: StringProvider,
        checkCallbacks: OpenCheckCallbacks,
        showU27warning: Boolean
    ): OpenCheckEvent {
        val message = if (showU27warning) {
            stringProvider.getString(R.string.housepay_u27_discount_warning_message)
        } else {
            stringProvider.getString(R.string.housepay_discount_warning_message)
        }
        return OpenCheckEvent.ShowRichDialog(
            title = stringProvider.getString(R.string.housepay_u27_discount_warning),
            message = message,
            confirmCta = stringProvider.getString(R.string.housepay_u27_discount_warning_cta),
            onConfirm = checkCallbacks.onRetryApplyDiscount,
            cancelCta = stringProvider.getString(R.string.cta_cancel)
        )
    }

    fun buildOpenCustomTipInputEvent(
        check: Check?,
        tipManager: CheckTipsManager
    ): OpenCheckEvent {
        return OpenCheckEvent.OpenCustomTipInput(
            initialAmountCents = (tipManager.tip as? Tip.CustomAmount?)
                ?.amountInCents
                ?: 0,
            currencyCode = check?.currency ?: "",
            leftToPayCents = check?.maxCustomTip ?: 0
        )
    }

    fun buildOpenHouseCreditInputEvent(
        houseCreditManager: HouseCreditManager,
        tipManager: CheckTipsManager,
        check: Check?
    ): OpenCheckEvent {
        val initialAmountCents = houseCreditManager.usingHouseCreditCents
        val maxAmountCents = houseCreditManager.availableHouseCredit?.balance ?: 0
        val currencyCode = houseCreditManager.availableHouseCredit?.currencyCode ?: ""

        return OpenCheckEvent.OpenHouseCreditInput(
            initialAmountCents,
            maxAmountCents,
            check?.amountPayableByCredit(tipManager.tipValueCents) ?: 0,
            currencyCode
        )
    }

    fun buildShowErrorDialogEvent(
        error: ApiResponse.Error?,
        stringProvider: StringProvider,
        onDismiss: () -> Unit
    ): OpenCheckEvent {
        val displayableError = ErrorHelper.getErrorMessage(
            error?.allErrorCodes() ?: emptyArray(),
            stringProvider
        )
        return OpenCheckEvent.ShowRichDialog(
            displayableError.title,
            displayableError.message,
            confirmCta = stringProvider.getString(R.string.dismiss_button_label),
            onConfirm = onDismiss
        )
    }
}