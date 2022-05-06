package com.sohohouse.seven.housepay.checkdetail

import androidx.annotation.DrawableRes
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.housepay.checkdetail.receipt.EmailReceiptState

data class CheckDetailValueModel(
    val label: String,
    val amount: String
)

data class CheckFooterInfo(
    val title: String,
    val subtitle: String,
)

data class CheckBannerInfo(
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit
)

data class NotificationBannerItem(
    val title: String,
    val message: CharSequence,
    val cta: String,
    val action: () -> Unit
)

data class HouseCreditAlertInfo(
    val message: CharSequence,
    val cta: String,
    val onClick: () -> Unit
)

data class EmailReceiptItemInfo(
    val state: EmailReceiptState,
    val onClick: (EmailReceiptState) -> Unit
)

data class ReceiptPaymentDetailsInfo(
    val label: String,
    @DrawableRes val icon: Int?,
    val amountCharged: String
)

data class CheckLinkInfo(
    val text: String,
    val onClick: () -> Unit
)

sealed class TipOptionUiModel(
    open val label: String,
    open val isSelected: Boolean,
    open val onClick: (option: TipOptionUiModel) -> Unit
) : DiffItem {

    data class CustomTipOptionUiModel(
        override val label: String,
        override var isSelected: Boolean,
        override val onClick: (option: TipOptionUiModel) -> Unit
    ) : TipOptionUiModel(label, isSelected, onClick)

    data class PercentageTipOptionUiModel(
        override val label: String,
        val percentage: Float,
        override var isSelected: Boolean,
        override val onClick: (option: TipOptionUiModel) -> Unit
    ) : TipOptionUiModel(label, isSelected, onClick)
}

data class Tips(
    val tipOptions: List<TipOptionUiModel>
)

class VenueDetailsInfo(
    val name: String,
    val address: String,
    val phoneNumber: String,
    val hours: List<String>,
    val onPhoneNumberClick: (String) -> Unit
)

sealed class CheckItem : DiffItem {

    data class Discount(
        val item: CheckDetailValueModel
    ) : CheckItem() {
        override val key: Any
            get() = javaClass
    }

    data class LineItem(
        val lineItemDTO: com.sohohouse.seven.network.core.models.housepay.LineItemDTO
    ) : CheckItem() {
        override val key: Any
            get() = javaClass
    }

    data class VATlineItem(
        val item: CheckDetailValueModel
    ) : CheckItem() {
        override val key: Any
            get() = javaClass
    }

    data class WaiterId(
        val value: String
    ) : CheckItem() {
        override val key: Any
            get() = javaClass
    }

    data class TabId(
        val value: String
    ) : CheckItem() {
        override val key: Any
            get() = javaClass
    }

    data class TotalsSectionLineItem(
        val item: CheckDetailValueModel
    ) : CheckItem() {
        override val key: Any
            get() = javaClass
    }

    data class ServiceCharge(
        val item: CheckDetailValueModel
    ) : CheckItem() {
        override val key: Any
            get() = javaClass
    }

    data class VenueHeader(
        val venue: String
    ) : CheckItem() {
        override val key: Any
            get() = javaClass
    }

    data class OrderTotal(
        val item: CheckDetailValueModel
    ) : CheckItem() {
        override val key: Any
            get() = javaClass
    }

    object LineBreak : CheckItem() {
        override val key: Any
            get() = javaClass
    }

    data class RevenueCenter(
        val name: String
    ) : CheckItem() {
        override val key: Any
            get() = javaClass
    }

    data class U27DiscountReminder(
        val item: CheckBannerInfo
    ) : CheckItem() {
        override val key: Any
            get() = javaClass
    }

    data class ServiceChargeAndTipNote(
        val text: String
    ) : CheckItem() {
        override val key: Any
            get() = javaClass
    }

    data class NotificationBanner(
        val alert: NotificationBannerItem
    ) : CheckItem() {
        override val key: Any
            get() = javaClass
    }

    data class TipSelectItem(
        val tips: Tips
    ) : CheckItem() {
        override val key: Any
            get() = javaClass
    }

    data class HouseCreditAlert(
        val info: HouseCreditAlertInfo
    ) : CheckItem() {
        override val key: Any
            get() = javaClass
    }

    data class EmailReceiptItem(
        val info: EmailReceiptItemInfo
    ) : CheckItem() {
        override val key: Any
            get() = javaClass
    }

    object ReceiptPaymentDetailsHeaderItem : CheckItem() {
        override val key: Any
            get() = javaClass
    }

    data class ReceiptPaymentDetailsItem(
        val info: ReceiptPaymentDetailsInfo
    ) : CheckItem() {
        override val key: Any
            get() = javaClass
    }

    data class VenueDetailsItem(
        val info: VenueDetailsInfo
    ) : CheckItem() {
        override val key: Any
            get() = javaClass
    }

    data class CheckLinkItem(
        val info: CheckLinkInfo
    ) : CheckItem() {
        override val key: Any
            get() = javaClass
    }

    data class DiscountNotAppliedWalkedOutItem(
        val info: CheckBannerInfo
    ) : CheckItem() {
        override val key: Any
            get() = javaClass
    }

}
