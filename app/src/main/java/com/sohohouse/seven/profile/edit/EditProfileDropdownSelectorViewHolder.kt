package com.sohohouse.seven.profile.edit

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.getString
import com.sohohouse.seven.common.extensions.setUp
import com.sohohouse.seven.databinding.ItemEditProfileDropdownSelectorBinding

class EditProfileDropdownSelectorViewHolder(
    parent: ViewGroup,
    private val listener: EditProfileListener,
    private val binding: ItemEditProfileDropdownSelectorBinding =
        ItemEditProfileDropdownSelectorBinding.bind(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_edit_profile_dropdown_selector, parent, false)
        )
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: EditProfileAdapterItem.DropdownSelector) {
        val placeholder = getString(R.string.enquiry_dropdown_placeholder)

        binding.picker.setUp(
            values = item.values,
            currentValue = item.profileField.data,
            onValueChange = {
                val realNewValue = if (it in item.values) it else null
                listener.onDropdownOptionSelected(item.profileField, realNewValue)
            },
            placeholder = if (item.profileField.hasValue) null else placeholder
        )
    }
}