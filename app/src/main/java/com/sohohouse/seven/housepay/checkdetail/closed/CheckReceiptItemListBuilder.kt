package com.sohohouse.seven.housepay.checkdetail.closed

import com.sohohouse.seven.common.extensions.addAllNonNull
import com.sohohouse.seven.common.extensions.addNonNull
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.housepay.checkdetail.CheckItem
import com.sohohouse.seven.housepay.checkdetail.CheckItemFactory
import com.sohohouse.seven.housepay.checkdetail.receipt.EmailReceiptState
import com.sohohouse.seven.network.core.models.housepay.Check

interface CheckReceiptItemListBuilder {

    fun buildReceiptItems(
        check: Check?,
        emailReceiptState: EmailReceiptState,
        callbacks: CheckReceiptCallbacks
    ): List<CheckItem>

}

class CheckReceiptItemListBuilderImpl(
    private val stringProvider: StringProvider
) : CheckReceiptItemListBuilder {

    override fun buildReceiptItems(
        check: Check?,
        emailReceiptState: EmailReceiptState,
        callbacks: CheckReceiptCallbacks
    ): List<CheckItem> {
        return mutableListOf<CheckItem>().apply {
            check ?: return emptyList()

            add(CheckItemFactory.getVenueHeaderItem(check))

            addAll(CheckItemFactory.getLineItems(check))

            add(CheckItem.LineBreak)

            addAll(CheckItemFactory.getSubtotalItems(stringProvider, check))

            addNonNull(CheckItemFactory.getServiceChargeItem(check, stringProvider))

            addAllNonNull(CheckItemFactory.getDiscountItems(check, stringProvider))

            addNonNull(
                CheckItemFactory.getTipLineItem(
                    check,
                    stringProvider,
                    check.paymentsTipsTotal
                )
            )

            add(CheckItem.LineBreak)

            addNonNull(
                CheckItemFactory.getOrderTotalReceiptItem(
                    check,
                    stringProvider,
                    check.paymentsTipsTotal
                )
            )

            addAll(CheckItemFactory.getVATitems(stringProvider, check))

            add(CheckItemFactory.getTabIdItem(stringProvider, check))

            add(
                CheckItemFactory.getEmailReceiptItem(
                    emailReceiptState,
                    callbacks.onEmailReceiptClick
                )
            )

            addAllNonNull(
                CheckItemFactory.getReceiptPaymentItems(
                    check,
                    stringProvider
                )
            )

            addNonNull(
                CheckItemFactory.getDiscountNotAppliedWalkoutItem(
                    check,
                    stringProvider
                )
            )

            add(
                CheckItemFactory.getHousePayFaqsLinkItem(
                    stringProvider,
                    callbacks.onFaqsClick
                )
            )

            addNonNull(
                CheckItemFactory.getVenueAddressItem(
                    check,
                    stringProvider,
                    callbacks
                )
            )
        }
    }
}