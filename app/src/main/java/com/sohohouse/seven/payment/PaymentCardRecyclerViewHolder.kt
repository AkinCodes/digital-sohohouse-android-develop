package com.sohohouse.seven.payment

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.databinding.ListPaymentItemBinding

class PaymentCardRecyclerViewHolder(private val binding: ListPaymentItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        model: CardPaymentItem,
        paymentMethodListener: PaymentMethodListener,
        isBooking: Boolean
    ) {
        with(binding) {
            listPaymentImage.setImageResource(model.paymentCardType.resDrawable)
            listPaymentImage.contentDescription =
                getString(model.paymentCardType.resAltString)

            listPaymentNumber.text =
                getString(R.string.payment_card_number_label).replaceBraces(model.lastFour)

            with(listPaymentType) {
                if (model.status == PaymentCardStatus.ACTIVE && !model.isDefault) {
                    setGone()
                } else if (model.status == PaymentCardStatus.EXPIRED) {
                    text = getString(R.string.payment_methods_expired_label)
                    setTextColor(getColor(R.color.mahohany))
                    setVisible()
                } else if (model.isDefault) {
                    text = getString(R.string.payment_methods_default_label)
                    setVisible()
                }
            }

            with(listPaymentButton) {
                setImageResource(R.drawable.menu_dots_horizontal)
                if (isBooking) {
                    setImageResource(R.drawable.ic_checkmark)
                    setVisible(model.isSelected)
                }
            }

            root.clicks {
                paymentMethodListener.onPaymentMethodSelected(model)
            }
        }
    }
}