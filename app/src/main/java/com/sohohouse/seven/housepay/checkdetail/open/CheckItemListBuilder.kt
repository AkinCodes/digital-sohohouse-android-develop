package com.sohohouse.seven.housepay.checkdetail.open

import com.sohohouse.seven.common.extensions.addAllNonNull
import com.sohohouse.seven.common.extensions.addNonNull
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.housepay.checkdetail.CheckItem
import com.sohohouse.seven.housepay.checkdetail.CheckItemFactory
import com.sohohouse.seven.housepay.discounts.DiscountManager
import com.sohohouse.seven.housepay.housecredit.HouseCreditManager
import com.sohohouse.seven.housepay.tips.CheckTipsManager
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.models.housepay.Check

interface CheckItemListBuilder {

    fun buildPayingCheckItems(
        check: Check?,
        openCheckCallbacks: OpenCheckCallbacks,
        applyDiscountError: ApiResponse.Error?,
        fetchHouseCreditError: ApiResponse.Error?,
        payCheckError: ApiResponse.Error?,
    ): List<CheckItem>

    fun buildWorkingCheckItems(
        check: Check?,
        openCheckCallbacks: OpenCheckCallbacks
    ): List<CheckItem>

}

class CheckItemListBuilderImpl(
    private val stringProvider: StringProvider,
    private val houseCreditManager: HouseCreditManager,
    private val tipManager: CheckTipsManager,
    private val discountManager: DiscountManager
) : CheckItemListBuilder {

    override fun buildPayingCheckItems(
        check: Check?,
        openCheckCallbacks: OpenCheckCallbacks,
        applyDiscountError: ApiResponse.Error?,
        fetchHouseCreditError: ApiResponse.Error?,
        payCheckError: ApiResponse.Error?
    ): List<CheckItem> {
        check ?: return emptyList()

        return mutableListOf<CheckItem>().apply {

            addNonNull(
                CheckItemFactory.getDiscountNotAppliedNotificationBannerItem(
                    applyDiscountError,
                    stringProvider,
                    openCheckCallbacks = openCheckCallbacks
                )
            )

            addNonNull(
                CheckItemFactory.getFetchHouseCreditErrorNotificationBannerItem(
                    fetchHouseCreditError,
                    stringProvider,
                    callbacks = openCheckCallbacks
                )
            )

            addNonNull(
                CheckItemFactory.getPayCheckErrorNotificationBannerItem(
                    payCheckError,
                    stringProvider,
                    callbacks = openCheckCallbacks
                )
            )

            add(CheckItemFactory.getVenueHeaderItem(check))

            addAll(CheckItemFactory.getLineItems(check))

            add(CheckItem.LineBreak)

            addNonNull(
                CheckItemFactory.getTipSelectItem(
                    check,
                    stringProvider,
                    tipManager,
                    openCheckCallbacks
                )
            )

            add(CheckItem.LineBreak)

            addAll(CheckItemFactory.getSubtotalItems(stringProvider, check))

            addNonNull(CheckItemFactory.getServiceChargeItem(check, stringProvider))

            addAllNonNull(CheckItemFactory.getDiscountItems(check, stringProvider))

            addNonNull(
                CheckItemFactory.getTipLineItem(
                    check,
                    stringProvider,
                    tipManager.tipValueCents
                )
            )

            addNonNull(
                CheckItemFactory.getHouseCreditLineItem(
                    check,
                    stringProvider,
                    houseCreditManager.usingHouseCreditCents
                )
            )

            addNonNull(CheckItemFactory.getServiceChargeAndTipsNoteItem(check, stringProvider))

            add(CheckItem.LineBreak)

            addNonNull(
                CheckItemFactory.getOrderTotalOpenCheckItem(
                    check,
                    stringProvider,
                    tipManager.tipValueCents,
                    houseCreditManager.usingHouseCreditCents
                )
            )

            addAll(CheckItemFactory.getVATitems(stringProvider, check))

            add(CheckItemFactory.getTabIdItem(stringProvider, check))

            addNonNull(
                CheckItemFactory.getHouseCreditItem(
                    stringProvider,
                    houseCreditManager,
                    openCheckCallbacks
                )
            )
        }
    }

    override fun buildWorkingCheckItems(
        check: Check?,
        openCheckCallbacks: OpenCheckCallbacks
    ): List<CheckItem> {
        check ?: return emptyList()

        return mutableListOf<CheckItem>().apply {

            add(CheckItemFactory.getVenueHeaderItem(check))

            addAll(CheckItemFactory.getLineItems(check))

            add(CheckItem.LineBreak)

            addNonNull(
                CheckItemFactory.getOrderTotalOpenCheckItem(
                    check,
                    stringProvider,
                    tipManager.tipValueCents,
                    houseCreditManager.usingHouseCreditCents
                )
            )

            addAll(CheckItemFactory.getVATitems(stringProvider, check))

            add(CheckItemFactory.getTabIdItem(stringProvider, check))

            if (discountManager.shouldShowU27DiscountBanner) {
                add(
                    CheckItemFactory.getU27discountReminder(
                        stringProvider,
                        callbacks = openCheckCallbacks
                    )
                )
            }
        }
    }

}