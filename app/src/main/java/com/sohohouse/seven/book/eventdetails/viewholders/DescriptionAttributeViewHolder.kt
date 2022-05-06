package com.sohohouse.seven.book.eventdetails.viewholders

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.getString
import com.sohohouse.seven.common.extensions.replaceBraces
import com.sohohouse.seven.common.utils.CurrencyUtils
import com.sohohouse.seven.common.views.eventdetaillist.EventCancellationAdapterItem
import com.sohohouse.seven.common.views.eventdetaillist.EventDepositPolicyAdapterItem
import com.sohohouse.seven.common.views.eventdetaillist.EventDescriptionAdapterItem
import com.sohohouse.seven.databinding.EventDetailsDescriptionLayoutBinding

class DescriptionAttributeViewHolder(
    private val binding: EventDetailsDescriptionLayoutBinding
) : RecyclerView.ViewHolder(binding.root), BaseEventAttributesViewHolder {

    override val label: TextView
        get() = binding.label

    override val description: TextView
        get() = binding.description

    fun bind(item: EventDescriptionAdapterItem) {
        setLabel(item.labelStringRes)

        when {
            item is EventCancellationAdapterItem -> onEventCancellationItem(item)
            item is EventDepositPolicyAdapterItem && item.descriptionStringRes != null ->
                setDescription(
                    getString(item.descriptionStringRes).replaceBraces(
                        CurrencyUtils.getFormattedPrice(item.price, item.currencyCode)
                    )
                )
            item.description?.isNotEmpty() == true -> setDescription(item.description)
        }
    }

    private fun onEventCancellationItem(item: EventCancellationAdapterItem) {

        item.description?.let {
            val descRes = item.getDescription()
            setDescription(item.replacePlaceHolderWithRealDate(getString(descRes)))
        }
    }

}