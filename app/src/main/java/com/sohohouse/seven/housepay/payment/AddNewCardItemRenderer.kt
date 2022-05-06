package com.sohohouse.seven.housepay.payment

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.common.extensions.layoutInflater
import com.sohohouse.seven.databinding.ItemChoosePaymentAddNewCardBinding

class AddNewCardItemRenderer
    : Renderer<ChoosePaymentMethodListItem.AddNewCard, AddNewCardItemViewHolder> {

    override val type: Class<ChoosePaymentMethodListItem.AddNewCard>
        get() = ChoosePaymentMethodListItem.AddNewCard::class.java

    override fun createViewHolder(parent: ViewGroup) = AddNewCardItemViewHolder.create(parent)

    override fun bindViewHolder(
        holder: AddNewCardItemViewHolder,
        item: ChoosePaymentMethodListItem.AddNewCard
    ) {
        holder.bind(item)
    }

}

class AddNewCardItemViewHolder(private val binding: ItemChoosePaymentAddNewCardBinding) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(parent: ViewGroup): AddNewCardItemViewHolder {
            return AddNewCardItemViewHolder(
                ItemChoosePaymentAddNewCardBinding.inflate(
                    parent.layoutInflater(),
                    parent,
                    false
                )
            )
        }
    }

    init {
        itemView.setOnClickListener {
            item?.let { it.onClick() }
        }
    }

    private var item: ChoosePaymentMethodListItem.AddNewCard? = null
    fun bind(item: ChoosePaymentMethodListItem.AddNewCard) {
        this.item = item
    }
}
