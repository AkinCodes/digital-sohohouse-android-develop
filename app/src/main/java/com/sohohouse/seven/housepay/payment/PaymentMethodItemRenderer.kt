package com.sohohouse.seven.housepay.payment

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.databinding.ChoosePaymentMethodItemBinding

class PaymentMethodItemRenderer
    : Renderer<ChoosePaymentMethodListItem.PaymentMethodListItem,
        PaymentMethodViewHolder> {

    override val type: Class<ChoosePaymentMethodListItem.PaymentMethodListItem>
        get() = ChoosePaymentMethodListItem.PaymentMethodListItem::class.java

    override fun createViewHolder(parent: ViewGroup): PaymentMethodViewHolder {
        return PaymentMethodViewHolder.create(parent)
    }

    override fun bindViewHolder(
        holder: PaymentMethodViewHolder,
        item: ChoosePaymentMethodListItem.PaymentMethodListItem
    ) {
        holder.bind(item)
    }
}

class PaymentMethodViewHolder(
    private val binding: ChoosePaymentMethodItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup): PaymentMethodViewHolder {
            return PaymentMethodViewHolder(
                ChoosePaymentMethodItemBinding.inflate(
                    parent.layoutInflater(),
                    parent,
                    false
                )
            )
        }
    }

    init {
        itemView.setOnClickListener {
            item?.let { item ->
                item.onClick(item)
            }
        }
    }

    private var item: ChoosePaymentMethodListItem.PaymentMethodListItem? = null
    fun bind(item: ChoosePaymentMethodListItem.PaymentMethodListItem) {
        this.item = item

        with(binding) {
            choosePaymentOptionIcon.setImageResourceNotNull(
                item.paymentMethod.icon
            )
            choosePaymentOptionDefaultLabel.setVisible(item.isDefault)
            choosePaymentOptionLabel.text = item.paymentMethod.getLabel(context.stringProvider)
            choosePaymentOptionRadioBtn.isChecked = item.isSelected
        }

    }
}