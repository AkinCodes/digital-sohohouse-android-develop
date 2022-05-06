package com.sohohouse.seven.profile.edit

import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.setVisible

class EditProfileDropdownFieldViewHolder(parent: ViewGroup, listener: EditProfileListener) :
    EditProfileStandardFieldViewHolder(parent, listener) {

    init {
        binding.editProfileFieldDropdownArrow.setImageResource(R.drawable.ic_chevron_down_rounded)
    }

    override fun bind(item: EditProfileAdapterItem.Field<*>) = with(binding) {
        super.bind(item)

        val dropdownField = item as? EditProfileAdapterItem.Field.Dropdown ?: return

        editProfileFieldDropdownArrow.setVisible()

        val rotation =
            if (EditProfileAdapterItem.Field.Dropdown.State.CLOSED == dropdownField.state) 0f else 180f
        if (editProfileFieldDropdownArrow.rotation == rotation) return

        editProfileFieldDropdownArrow.animate().cancel()
        editProfileFieldDropdownArrow.animate().rotation(rotation).start()
    }

}
