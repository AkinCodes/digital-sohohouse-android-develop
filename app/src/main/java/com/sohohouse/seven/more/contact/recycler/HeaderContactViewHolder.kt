package com.sohohouse.seven.more.contact.recycler

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.getString
import com.sohohouse.seven.databinding.ViewContactHeaderBinding

class HeaderContactViewHolder(private val binding: ViewContactHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(isBarredAccount: Boolean) = with(binding) {
        textView.text =
            getString(if (isBarredAccount) R.string.more_contact_supporting else R.string.enquiry_supporting)
        visitFaqsButton.setup(R.drawable.icon_faq, R.string.enquiry_faq_cta, true)
        visitFaqsButton.isVisible = !isBarredAccount
        whitePadding.isVisible = !isBarredAccount
        noFaqPadding.isVisible = isBarredAccount
    }

    fun clicks(onClickListener: (Any) -> Unit) {
        binding.visitFaqsButton.clicks(onClickListener)
    }
}