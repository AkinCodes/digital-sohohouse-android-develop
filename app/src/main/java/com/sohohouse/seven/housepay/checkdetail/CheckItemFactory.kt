package com.sohohouse.seven.housepay.checkdetail

import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.utils.CurrencyUtils
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.housepay.*
import com.sohohouse.seven.housepay.checkdetail.closed.CheckReceiptCallbacks
import com.sohohouse.seven.housepay.checkdetail.open.OpenCheckCallbacks
import com.sohohouse.seven.housepay.checkdetail.receipt.EmailReceiptState
import com.sohohouse.seven.housepay.housecredit.HouseCreditManager
import com.sohohouse.seven.housepay.tips.CheckTipsManager
import com.sohohouse.seven.housepay.tips.Tip
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.common.extensions.nullIfEmpty
import com.sohohouse.seven.network.core.models.housepay.Check
import com.sohohouse.seven.network.core.models.housepay.Location
import com.sohohouse.seven.network.core.models.housepay.Payment
import com.sohohouse.seven.payment.PaymentCardType
import java.util.*

object CheckItemFactory {

    fun getTipSelectItem(
        check: Check,
        stringProvider: StringProvider,
        checkTipsManager: CheckTipsManager,
        callbacks: OpenCheckCallbacks
    ): CheckItem? {
        val location = check.location ?: return null

        val values = location.variableTipsValues.nullIfEmpty()
            ?: listOf("15", "18", "20")

        val options = mutableListOf<TipOptionUiModel>().apply {

            add(
                TipOptionUiModel.CustomTipOptionUiModel(
                    label = stringProvider.getString(R.string.housepay_tip_label_custom),
                    isSelected = checkTipsManager.tip is Tip.CustomAmount,
                    onClick = callbacks.onTipOptionClick
                )
            )

            values.forEach { valueString ->
                val percentValue: Float = (valueString.toFloatOrNull() ?: 0f) / 100f
                add(
                    TipOptionUiModel.PercentageTipOptionUiModel(
                        label = stringProvider.getString(
                            R.string.housepay_tip_label_percentage,
                            valueString
                        ),
                        percentage = percentValue,
                        isSelected = (checkTipsManager.tip as? Tip.Percentage)?.percentage == percentValue,
                        onClick = callbacks.onTipOptionClick
                    )
                )
            }
        }

        return CheckItem.TipSelectItem(
            Tips(
                options
            )
        )
    }

    fun getVATitems(
        stringProvider: StringProvider,
        check: Check
    ): List<CheckItem> = mutableListOf<CheckItem>().apply {
        check.vatItems?.forEach { vatItem ->
            add(
                CheckItem.VATlineItem(
                    CheckDetailValueModel(
                        label = stringProvider.getString(
                            R.string.housepay_vat_item_label,
                            vatItem.percentage?.toString() ?: ""
                        ),
                        amount = CurrencyUtils.getFormattedPrice(
                            vatItem.cents ?: 0,
                            check.currency,
                            showCurrency = false
                        )
                    )
                )
            )
        }
    }

    fun getOrderTotalOpenCheckItem(
        check: Check,
        stringProvider: StringProvider,
        tipValueCents: Int,
        houseCreditValueCents: Int
    ): CheckItem? {
        var total = check.totalAmountDue()

        if (total <= 0) return null

        val label = stringProvider.getString(R.string.housepay_order_total)

        total += tipValueCents
        total -= houseCreditValueCents

        return CheckItem.OrderTotal(
            CheckDetailValueModel(
                label,
                CurrencyUtils.getFormattedPrice(
                    total,
                    check.currency,
                    showCurrency = true
                )
            )
        )
    }

    fun getOrderTotalReceiptItem(
        check: Check,
        stringProvider: StringProvider,
        tipValueCents: Int
    ): CheckItem? {
        val total = check.totalAmountDue(
            tip = tipValueCents
        )

        if (total <= 0) return null

        val label = stringProvider.getString(R.string.housepay_order_total)

        return CheckItem.OrderTotal(
            CheckDetailValueModel(
                label,
                CurrencyUtils.getFormattedPrice(
                    total,
                    check.currency,
                    showCurrency = true
                )
            )
        )
    }

    fun getVenueHeaderItem(check: Check) =
        CheckItem.VenueHeader(check.locationName ?: "")

    fun getTabIdItem(
        stringProvider: StringProvider,
        check: Check
    ) = CheckItem.TabId(
        stringProvider.getString(R.string.check_tab_id, check.checkNumber ?: "")
    )

    fun getSubtotalItems(
        stringProvider: StringProvider,
        check: Check
    ): List<CheckItem> {
        val location: Location = check.location
            ?: return emptyList()

        return if (location.subtotalSplitFoodBeverage == true) {
            listOf(
                getFoodSubtotalItem(stringProvider, check),
                getBeverageSubtotalItem(stringProvider, check)
            )
        } else {
            listOf(getSubtotalItem(stringProvider, check))
        }
    }

    private fun getFoodSubtotalItem(
        stringProvider: StringProvider,
        check: Check
    ): CheckItem {
        return CheckItem.TotalsSectionLineItem(
            CheckDetailValueModel(
                label = stringProvider.getString(R.string.check_food_subtotal),
                amount = CurrencyUtils.getFormattedPrice(
                    check.foodSubtotal,
                    check.currency,
                    showCurrency = false
                )
            )
        )
    }

    private fun getBeverageSubtotalItem(
        stringProvider: StringProvider,
        check: Check
    ): CheckItem {
        return CheckItem.TotalsSectionLineItem(
            CheckDetailValueModel(
                label = stringProvider.getString(R.string.check_beverage_subtotal),
                amount = CurrencyUtils.getFormattedPrice(
                    check.beverageSubtotal,
                    check.currency,
                    showCurrency = false
                )
            )
        )
    }

    private fun getSubtotalItem(
        stringProvider: StringProvider,
        check: Check
    ) = CheckItem.TotalsSectionLineItem(
        CheckDetailValueModel(
            label = stringProvider.getString(R.string.check_subtotal),
            amount = CurrencyUtils.getFormattedPrice(
                check.subtotal,
                check.currency,
                showCurrency = false
            )
        )
    )

    fun getLineItems(
        check: Check,
    ): List<CheckItem> {
        val lineItems = mutableListOf<CheckItem>()
        val groupedItems = check.groupedItems

        groupedItems.forEachIndexed { _, revenueCenter ->
            if (groupedItems.size > 1) {
                lineItems.add(
                    CheckItem.RevenueCenter(revenueCenter.name.uppercase(Locale.US))
                )
            }
            revenueCenter.items.forEach {
                lineItems.add(
                    CheckItem.LineItem(it)
                )
            }
        }
        return lineItems
    }

    fun getU27discountReminder(
        stringProvider: StringProvider,
        callbacks: OpenCheckCallbacks
    ): CheckItem {
        return CheckItem.U27DiscountReminder(
            CheckBannerInfo(
                title = stringProvider.getString(R.string.check_u27_discount_reminder_title),
                subtitle = stringProvider.getString(R.string.check_u27_discount_reminder_subtitle),
                onClick = callbacks.onU27AlertBannerClick
            )
        )
    }

    fun getServiceChargeItem(
        check: Check,
        stringProvider: StringProvider
    ): CheckItem? {
        val gratuitiesCents = check
            .gratuitiesCents
            .takeIf { it > 0 }
            ?: return null

        val serviceCharge = check
            .location
            ?.serviceChargePercentage

        return CheckItem.ServiceCharge(
            CheckDetailValueModel(
                label = stringProvider.getString(
                    R.string.check_service_charge,
                    serviceCharge.toString()
                ),
                amount = CurrencyUtils.getFormattedPrice(gratuitiesCents)
            )
        )
    }

    fun getDiscountItems(
        check: Check,
        stringProvider: StringProvider
    ): Collection<CheckItem> {
        val discounts = check.discounts ?: return emptyList()

        return mutableListOf<CheckItem>().apply {
            discounts.forEach { discount ->
                add(
                    CheckItem.Discount(
                        CheckDetailValueModel(
                            label = discount.label(stringProvider),
                            amount = "-${CurrencyUtils.getFormattedPrice(discount.cents)}"
                        )
                    )
                )
            }
        }
    }

    fun getServiceChargeAndTipsNoteItem(
        check: Check,
        stringProvider: StringProvider
    ): CheckItem? {
        return when {
            check.gratuitiesCents > 0 && check.variableTipsEnabled -> {
                CheckItem.ServiceChargeAndTipNote(
                    stringProvider.getString(R.string.housepay_service_charge_and_tips_note)
                )
            }
            check.variableTipsEnabled -> {
                CheckItem.ServiceChargeAndTipNote(
                    stringProvider.getString(R.string.housepay_tips_note)
                )
            }
            else -> null
        }
    }

    fun getDiscountNotAppliedNotificationBannerItem(
        error: ApiResponse.Error?,
        stringProvider: StringProvider,
        openCheckCallbacks: OpenCheckCallbacks
    ): CheckItem.NotificationBanner? {
        return when (error?.firstErrorCode()) {
            HousePayErrorCodes.CHECK_OPEN_ON_WORKSTATION -> {
                CheckItem.NotificationBanner(
                    NotificationBannerItem(
                        stringProvider.getString(R.string.housepay_error_generic_title),
                        stringProvider.getString(
                            R.string.housepay_error_discount_not_applied_check_is_editing
                        ),
                        stringProvider.getString(R.string.try_again),
                        openCheckCallbacks.onRetryApplyDiscount
                    )
                )
            }
            else -> null
        }
    }

    fun getTipLineItem(
        check: Check,
        stringProvider: StringProvider,
        tipValueCents: Int
    ): CheckItem? {
        val tip = tipValueCents.takeIf { it > 0 } ?: return null

        return CheckItem.TotalsSectionLineItem(
            CheckDetailValueModel(
                stringProvider.getString(R.string.housepay_tip_item_label),
                CurrencyUtils.getFormattedPrice(
                    tip,
                    check.currency,
                    showCurrency = false
                )
            )
        )
    }

    fun getHouseCreditItem(
        stringProvider: StringProvider,
        houseCreditManager: HouseCreditManager,
        openCheckCallbacks: OpenCheckCallbacks
    ): CheckItem? {
        houseCreditManager.availableHouseCredit ?: return null

        val message = stringProvider.getString(
            R.string.housepay_house_credit_available
        ).createClickableSpannableForSubstring(
            value = stringProvider.getString(R.string.housepay_house_credit_available_ts_and_cs),
            onClick = openCheckCallbacks.onHouseCreditTsAndCsClick,
            bold = false,
            underline = true
        )

        val cta = if (houseCreditManager.usingHouseCreditCents > 0) {
            stringProvider.getString(R.string.housepay_edit_credit_cta)
        } else {
            stringProvider.getString(R.string.housepay_use_credit_cta)
        }
        return CheckItem.HouseCreditAlert(
            HouseCreditAlertInfo(
                message = message,
                cta = cta,
                onClick = openCheckCallbacks.onUseHouseCreditClick
            )
        )
    }

    fun getHouseCreditLineItem(
        check: Check,
        stringProvider: StringProvider,
        houseCreditCents: Int
    ): CheckItem? {
        val credit = houseCreditCents.takeIf { it > 0 } ?: return null

        val amount = CurrencyUtils.getFormattedPrice(
            credit,
            check.currency,
            showCurrency = false
        )
        return CheckItem.TotalsSectionLineItem(
            CheckDetailValueModel(
                stringProvider.getString(R.string.housepay_house_credit_item_label),
                "-$amount"
            )
        )
    }

    fun getFetchHouseCreditErrorNotificationBannerItem(
        fetchHouseCreditError: ApiResponse.Error?,
        stringProvider: StringProvider,
        callbacks: OpenCheckCallbacks
    ): CheckItem? {
        if (fetchHouseCreditError == null) return null

        return CheckItem.NotificationBanner(
            NotificationBannerItem(
                title = stringProvider.getString(R.string.housepay_error_generic_title),
                message = stringProvider.getString(R.string.housepay_error_retrieving_house_credit),
                cta = stringProvider.getString(R.string.try_again),
                action = callbacks.onRetryFetchHouseCredit
            )
        )
    }

    fun getPayCheckErrorNotificationBannerItem(
        payCheckError: ApiResponse.Error?,
        stringProvider: StringProvider,
        callbacks: OpenCheckCallbacks
    ): CheckItem? {
        if (payCheckError == null) return null

        return CheckItem.NotificationBanner(
            NotificationBannerItem(
                title = stringProvider.getString(R.string.housepay_error_generic_title),
                message = stringProvider.getString(R.string.housepay_error_retrieving_house_credit),
                cta = stringProvider.getString(R.string.try_again),
                action = callbacks.onRetryPayCheck
            )
        )
    }

    fun getEmailReceiptItem(
        emailReceiptState: EmailReceiptState,
        onSendEmailClick: (EmailReceiptState) -> Unit
    ): CheckItem {
        return CheckItem.EmailReceiptItem(
            EmailReceiptItemInfo(
                state = emailReceiptState,
                onClick = onSendEmailClick
            )
        )
    }

    fun getReceiptPaymentItems(
        check: Check?,
        stringProvider: StringProvider
    ): List<CheckItem> {
        val check = check ?: return emptyList()
        val list = mutableListOf<CheckItem>()

        check.payments
            .filter { it.status == Payment.STATUS_PAID }
            .forEach {
                list.addNonNull(getPaymentItem(check, it, stringProvider))
            }

        if (list.isNotEmpty()) {
            list.add(0, CheckItem.ReceiptPaymentDetailsHeaderItem)
        }

        return list
    }

    private fun getPaymentItem(
        check: Check,
        payment: Payment,
        stringProvider: StringProvider
    ): CheckItem? {
        var icon: Int? = null
        var label: String = ""
        when (payment.paymentType) {
            Payment.TYPE_CARD -> {
                payment.cardType?.asEnumOrDefault<PaymentCardType>()?.let {
                    icon = it.resDrawable
                    label = stringProvider.getString(it.resAltString)
                }
            }
            Payment.TYPE_HOUSE_CREDIT -> {
//                icon = TODO
                label = stringProvider.getString(R.string.housepay_house_credit_title)
            }
            //TODO apple pay
            //TODO Google pay
        }
        val amountCharged = CurrencyUtils.getFormattedPrice(
            priceInCents = payment.cents,
            currencyCode = check.currency,
            showCurrency = true
        )
        return CheckItem.ReceiptPaymentDetailsItem(
            ReceiptPaymentDetailsInfo(
                label, icon, amountCharged
            )
        )
    }

    fun getHousePayFaqsLinkItem(
        stringProvider: StringProvider,
        onClick: () -> Unit
    ): CheckItem {
        return CheckItem.CheckLinkItem(
            CheckLinkInfo(
                text = stringProvider.getString(R.string.housepay_faqs_link),
                onClick = onClick
            )
        )
    }

    fun getVenueAddressItem(
        check: Check?,
        stringProvider: StringProvider,
        callbacks: CheckReceiptCallbacks
    ): CheckItem? {
        val venue = check?.venue ?: return null
        return CheckItem.VenueDetailsItem(
            VenueDetailsInfo(
                name = venue.name,
                address = venue.buildAddress(singleLine = false),
                phoneNumber = venue.phoneNumber,
                hours = venue.getVenueOperatingHours(
                    dayAndTimesPlaceholder = stringProvider.getString(
                        R.string.opening_hours_day_with_times_placeholder
                    )
                ),
                onPhoneNumberClick = callbacks.onPhoneNumberClick
            )
        )
    }

    fun getDiscountNotAppliedWalkoutItem(
        check: Check,
        stringProvider: StringProvider
    ): CheckItem? {
        if (check.walkedOut.not()) return null
        return CheckItem.DiscountNotAppliedWalkedOutItem(
            CheckBannerInfo(
                title = stringProvider.getString(R.string.housepay_walkout_discount_not_applied_primary),
                subtitle = stringProvider.getString(R.string.housepay_walkout_discount_not_applied_secondary),
                onClick = {}
            )
        )
    }

}