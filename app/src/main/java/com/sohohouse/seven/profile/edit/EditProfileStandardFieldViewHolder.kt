package com.sohohouse.seven.profile.edit

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.utils.StringProviderImpl
import com.sohohouse.seven.databinding.ItemEditProfileStandardFieldBinding

open class EditProfileStandardFieldViewHolder(
    parent: ViewGroup,
    private val listener: EditProfileListener,
    protected val binding: ItemEditProfileStandardFieldBinding =
        ItemEditProfileStandardFieldBinding.bind(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_edit_profile_standard_field, parent, false)
        )
) : RecyclerView.ViewHolder(binding.root) {

    open fun bind(item: EditProfileAdapterItem.Field<*>) = with(binding) {
        val field = item.field

        profileFieldFilledIndicator.isActivated =
            !field.getPublicDisplayValue(StringProviderImpl(resources)).isNullOrBlank()
        val label = field.getLabel(StringProviderImpl(resources))
        editProfileFieldLabel.text = label
        editProfileFieldLabel.contentDescription = label
        editProfileFieldValue.hint = getString(field.placeholder)
        editProfileFieldValue.text = field.getEditDisplayValue(context.stringProvider)

        if (field.isEditable) {
            profileFieldFilledIndicator.setVisible()
            editProfileFieldValue.isEnabled = true
            root.isClickable = true
            root.setOnClickListener {
                listener.onEditProfileFieldClick(item)
            }
        } else {
            profileFieldFilledIndicator.setGone()
            editProfileFieldValue.isEnabled = false
            root.isClickable = false
        }

        if (field.hasError) {
            profileFieldFilledIndicator.imageTintList =
                ColorStateList.valueOf(itemView.getAttributeColor(R.attr.colorError))
            editProfileFieldError.text = field.getConcatenatedErrorMessages(context.resources)
            editProfileFieldError.setVisible()
        } else {
            profileFieldFilledIndicator.imageTintList = null
            editProfileFieldError.text = field.getConcatenatedErrorMessages(context.resources)
            editProfileFieldError.setGone()
        }
    }
}
