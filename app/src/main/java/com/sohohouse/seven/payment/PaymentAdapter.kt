package com.sohohouse.seven.payment

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.sohohouse.seven.base.BaseRecyclerDiffAdapter
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.databinding.ListPaymentAddItemBinding
import com.sohohouse.seven.databinding.ListPaymentDescriptionItemBinding
import com.sohohouse.seven.databinding.ListPaymentItemBinding

enum class PaymentItemType {
    DESCRIPTION,
    CARD,
    ADD_CARD
}

interface PaymentMethodListener {
    fun onPaymentMethodSelected(model: CardPaymentItem)
    fun onAddPaymentSelected()
}

class PaymentAdapter constructor(
    private val paymentMethodListener: PaymentMethodListener,
    private val isBooking: Boolean = false
) : BaseRecyclerDiffAdapter<RecyclerView.ViewHolder, BasePaymentItem>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (PaymentItemType.values()[viewType]) {
            PaymentItemType.CARD -> createPaymentCardRecyclerViewHolder(parent)
            PaymentItemType.ADD_CARD -> createAddRecyclerViewHolder(parent)
            PaymentItemType.DESCRIPTION -> createTextRecyclerViewHolder(parent)
        }
    }

    private fun createPaymentCardRecyclerViewHolder(parent: ViewGroup) =
        PaymentCardRecyclerViewHolder(
            ListPaymentItemBinding.inflate(getLayoutInflater(parent), parent, false)
        )

    private fun createAddRecyclerViewHolder(parent: ViewGroup) =
        AddRecyclerViewHolder(
            ListPaymentAddItemBinding.inflate(getLayoutInflater(parent), parent, false)
        )

    private fun createTextRecyclerViewHolder(parent: ViewGroup) =
        TextRecyclerViewHolder(
            ListPaymentDescriptionItemBinding.inflate(getLayoutInflater(parent), parent, false)
        )

    private fun getLayoutInflater(parent: ViewGroup) = LayoutInflater.from(parent.context)

    override fun getItemViewType(position: Int): Int {
        return currentItems[position].type.ordinal
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            PaymentItemType.CARD.ordinal -> {
                val itemHolder = holder as PaymentCardRecyclerViewHolder
                val item = currentItems[position] as CardPaymentItem
                itemHolder.bind(item, paymentMethodListener, isBooking)
            }
            PaymentItemType.DESCRIPTION.ordinal -> {
                val itemHolder = holder as TextRecyclerViewHolder
                val item = currentItems[position] as TextPaymentItem
                itemHolder.bind(item.stringRes)
            }
            PaymentItemType.ADD_CARD.ordinal -> {
                holder.itemView.clicks { paymentMethodListener.onAddPaymentSelected() }
            }
        }
    }

    fun updateDefaultPayment(id: String) {
        for (item in currentItems) {
            if (item is CardPaymentItem && item.id == id) {
                item.isDefault = true
                notifyItemChanged(currentItems.indexOf(item))
            } else if (item is CardPaymentItem && item.id != id && item.isDefault) {
                item.isDefault = false
                notifyItemChanged(currentItems.indexOf(item))
            }
        }
    }

    fun deletePayment(id: String) {
        modifyList { list ->
            list.removeAll { it is CardPaymentItem && it.id == id }
            if (list.none { it.type === PaymentItemType.CARD }) {
                list.clear()
            }
        }
    }

    fun updateSelection(id: String) {
        for (item in currentItems) {
            if (item is CardPaymentItem && item.id == id) {
                item.isSelected = true
                notifyItemChanged(currentItems.indexOf(item))
            } else if (item is CardPaymentItem && item.id != id && item.isSelected) {
                item.isSelected = false
                notifyItemChanged(currentItems.indexOf(item))
            }
        }
    }

}