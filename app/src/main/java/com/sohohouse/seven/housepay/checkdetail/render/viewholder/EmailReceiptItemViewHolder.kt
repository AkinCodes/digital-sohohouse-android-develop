package com.sohohouse.seven.housepay.checkdetail.render.viewholder

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.getString
import com.sohohouse.seven.common.extensions.inflateLayout
import com.sohohouse.seven.common.extensions.setInvisible
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.databinding.ItemEmailReceiptBinding
import com.sohohouse.seven.housepay.checkdetail.EmailReceiptItemInfo
import com.sohohouse.seven.housepay.checkdetail.receipt.EmailReceiptState

class EmailReceiptItemViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(parent.inflateLayout(R.layout.item_email_receipt)) {

    private val binding by viewBinding(ItemEmailReceiptBinding::bind)

    private var item: EmailReceiptItemInfo? = null

    init {
        binding.emailReceiptBtn.setOnClickListener {
            item?.let { it.onClick(it.state) }
        }
    }

    fun bind(item: EmailReceiptItemInfo) {
        this.item = item

        when (item.state) {
            EmailReceiptState.SendCta -> {
                binding.emailReceiptBtn.text = getString(R.string.housepay_email_receipt_cta)
                binding.loadingSpinner.setInvisible()
            }
            EmailReceiptState.Sending -> {
                binding.emailReceiptBtn.text = ""
                binding.loadingSpinner.setVisible()
            }
            EmailReceiptState.Sent -> {
                binding.emailReceiptBtn.text = getString(R.string.housepay_email_receipt_sent)
                binding.loadingSpinner.setInvisible()
            }
        }
    }
}
