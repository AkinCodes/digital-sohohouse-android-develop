package com.sohohouse.seven.profile.edit

import android.view.ViewGroup
import androidx.core.view.isVisible
import com.sohohouse.seven.common.extensions.setVisible

class EditProfileMultipleValuesViewHolder(parent: ViewGroup, listener: EditProfileListener) :
    EditProfileStandardFieldViewHolder(parent, listener) {
    override fun bind(item: EditProfileAdapterItem.Field<*>) = with(binding) {
        super.bind(item)

        editProfileFieldAddBtn.setVisible()
        editProfileFieldValue.isVisible = item.field.hasValue
        editProfileFieldAddBtn.isVisible = !item.field.hasValue
    }
}
