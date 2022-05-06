@file:Suppress("MoveLambdaOutsideParentheses")

package com.sohohouse.seven.housepay.checkdetail.render.renderer

import com.sohohouse.seven.common.design.adapter.NoOpBind
import com.sohohouse.seven.common.design.adapter.renderer
import com.sohohouse.seven.housepay.checkdetail.CheckItem
import com.sohohouse.seven.housepay.checkdetail.render.viewholder.*

val CheckLinkItemRenderer
    get() = renderer<CheckItem.CheckLinkItem, CheckLinkViewHolder>(
        viewHolderProvider = ::CheckLinkViewHolder,
        bind = { holder, item -> holder.bind(item.info) }
    )

val VenueDetailsItemRenderer
    get() = renderer<CheckItem.VenueDetailsItem, CheckVenueDetailsViewHolder>(
        viewHolderProvider = ::CheckVenueDetailsViewHolder,
        bind = { holder, item -> holder.bind(item.info) }
    )

val EmailReceiptItemRenderer
    get() = renderer<CheckItem.EmailReceiptItem, EmailReceiptItemViewHolder>(
        viewHolderProvider = ::EmailReceiptItemViewHolder,
        bind = { holder, item -> holder.bind(item.info) }
    )

val DiscountNotAppliedWalkedOutItemRenderer
    get() = renderer<CheckItem.DiscountNotAppliedWalkedOutItem, CheckReceiptBannerViewHolder>(
        viewHolderProvider = ::CheckReceiptBannerViewHolder,
        bind = { holder, item -> holder.bind(item.info) }
    )

val PaymentDetailsItemRenderer
    get() = renderer<CheckItem.ReceiptPaymentDetailsItem, ReceiptPaymentDetailsViewHolder>(
        viewHolderProvider = ::ReceiptPaymentDetailsViewHolder,
        bind = { holder, item -> holder.bind(item.info) }
    )

val ReceiptPaymentDetailsHeaderRenderer
    get() = renderer<CheckItem.ReceiptPaymentDetailsHeaderItem, ReceiptPaymentDetailsHeaderViewHolder>(
        viewHolderProvider = ::ReceiptPaymentDetailsHeaderViewHolder,
        bind = NoOpBind
    )