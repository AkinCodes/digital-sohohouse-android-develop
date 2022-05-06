package com.sohohouse.seven.profile.view

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.context
import com.sohohouse.seven.common.extensions.resources
import com.sohohouse.seven.common.utils.StringProviderImpl
import com.sohohouse.seven.databinding.ItemProfileFieldBinding

class ViewProfileFieldViewHolder(private val binding: ItemProfileFieldBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ViewProfileAdapterItem.Field) {
        val field = item.field
        val label = field.getLabel(StringProviderImpl(resources))
        with(binding) {
            profileFieldLabel.text = label
            profileFieldLabel.contentDescription = label
            profileFieldValue.text =
                field.getPublicDisplayValue(StringProviderImpl(context.resources))
        }
    }
}