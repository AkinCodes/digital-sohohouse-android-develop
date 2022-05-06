package com.sohohouse.seven.housepay.checkdetail.render.viewholder

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.getDrawable
import com.sohohouse.seven.common.extensions.inflateLayout
import com.sohohouse.seven.common.extensions.setCompoundDrawableStart
import com.sohohouse.seven.databinding.ItemReceiptPaymentDetailsBinding
import com.sohohouse.seven.housepay.checkdetail.ReceiptPaymentDetailsInfo

class ReceiptPaymentDetailsViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(parent.inflateLayout(R.layout.item_receipt_payment_details)) {

    private val binding by viewBinding(ItemReceiptPaymentDetailsBinding::bind)

    fun bind(info: ReceiptPaymentDetailsInfo) {
        binding.housepayAmountChargedValue.text = info.amountCharged
        binding.housepayPaymentMethodUsedValue.text = info.label
        binding.housepayPaymentMethodUsedValue.setCompoundDrawableStart(getDrawable(info.icon))
    }

}
