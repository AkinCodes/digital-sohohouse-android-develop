package com.sohohouse.seven.profile.edit

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.base.BaseRecyclerDiffAdapter
import com.sohohouse.seven.profile.ProfileField
import com.sohohouse.seven.profile.edit.EditProfileAdapterItemType.*

interface EditProfileListener {
    fun onEditProfileFieldClick(field: EditProfileAdapterItem.Field<*>)
    fun onDropdownOptionSelected(field: ProfileField<out PickerItem?>, option: PickerItem?)
    fun onEditPhotoClick()
    fun onContactUsClick()
    fun onUserFocusQuestion(question: EditProfileAdapterItem.Question)
    fun onQuestionAnswerChange(question: EditProfileAdapterItem.Question)
}

class EditProfileAdapter(private val listener: EditProfileListener) :
    BaseRecyclerDiffAdapter<RecyclerView.ViewHolder, EditProfileAdapterItem>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (values()[viewType]) {
            HEADER -> EditProfileHeaderViewHolder(parent, listener)
            STANDARD_FIELD -> EditProfileStandardFieldViewHolder(parent, listener)
            DROPDOWN_FIELD -> EditProfileDropdownFieldViewHolder(parent, listener)
            DROPDOWN_SELECTOR -> EditProfileDropdownSelectorViewHolder(parent, listener)
            MULTIPLE_VALUES -> EditProfileMultipleValuesViewHolder(parent, listener)
            PRIVATE_INFO_HEADER -> EditProfilePrivateInfoViewHolder(parent, listener)
            LEGAL_DISCLAIMER -> ProfileLegalDisclaimerViewHolder(parent)
            QUESTION -> EditProfileQuestionViewHolder(parent, listener)
            SECTION_HEADER -> EditProfileSectionHeaderViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        p1: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, p1, payloads)
        when (holder) {
            is EditProfileHeaderViewHolder -> holder.bind(currentItems[p1] as EditProfileAdapterItem.Header)
            is EditProfileStandardFieldViewHolder -> holder.bind(currentItems[p1] as EditProfileAdapterItem.Field<*>)
            is EditProfileDropdownFieldViewHolder -> holder.bind(currentItems[p1] as EditProfileAdapterItem.Field<*>)
            is EditProfileMultipleValuesViewHolder -> holder.bind(currentItems[p1] as EditProfileAdapterItem.Field<*>)
            is EditProfileDropdownSelectorViewHolder -> holder.bind(currentItems[p1] as EditProfileAdapterItem.DropdownSelector)
            is EditProfileSectionHeaderViewHolder -> holder.bind(currentItems[p1] as EditProfileAdapterItem.SectionHeader)
            is EditProfileQuestionViewHolder -> holder.bind(
                currentItems[p1] as EditProfileAdapterItem.Question,
                payloads.firstOrNull()
            )
        }
    }


    override fun getItemViewType(position: Int): Int {
        return currentItems[position].cellType.ordinal
    }
}