package com.sohohouse.seven.profile.edit

import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.common.form.FormItem
import com.sohohouse.seven.common.form.FormRowType
import com.sohohouse.seven.profile.ProfileField
import com.sohohouse.seven.profile.QuestionAndAnswer

sealed class EditProfileAdapterItem(val cellType: EditProfileAdapterItemType) : DiffItem {
    data class Header(val name: String, val imageUrl: String) :
        EditProfileAdapterItem(EditProfileAdapterItemType.HEADER)

    sealed class Field<T : ProfileField<*>>(open var field: T) :
        EditProfileAdapterItem(field.editCellType) {

        data class Dropdown<T : ProfileField<out PickerItem?>>(
            private val _field: T,
            var state: State = State.CLOSED,
            var multipleChoiceOptions: List<PickerItem>
        ) : Field<T>(_field) {

            fun toggle() {
                state = if (state == State.CLOSED) State.OPEN else State.CLOSED
            }

            enum class State {
                OPEN,
                CLOSED;
            }
        }

        data class Standard<T : ProfileField<*>>(private val _field: T) : Field<T>(_field)

        override val key: Any?
            get() = this.field::class
    }

    data class DropdownSelector(
        val profileField: ProfileField<out PickerItem?>,
        val values: List<PickerItem>
    ) : EditProfileAdapterItem(EditProfileAdapterItemType.DROPDOWN_SELECTOR)

    data class SectionHeader(val title: String, val subtitle: String? = null) :
        EditProfileAdapterItem(EditProfileAdapterItemType.SECTION_HEADER)

    data class Question(val question: QuestionAndAnswer, override val rowType: FormRowType) :
        EditProfileAdapterItem(EditProfileAdapterItemType.QUESTION), FormItem

    object PrivateInfoHeader
        : EditProfileAdapterItem(EditProfileAdapterItemType.PRIVATE_INFO_HEADER)

    object LegalDisclaimer
        : EditProfileAdapterItem(EditProfileAdapterItemType.LEGAL_DISCLAIMER)
}

interface PickerItem {
    val value: String
}

data class IndustryOption(override val value: String) : PickerItem

enum class EditProfileAdapterItemType {
    HEADER,
    STANDARD_FIELD,
    DROPDOWN_FIELD,
    DROPDOWN_SELECTOR,
    MULTIPLE_VALUES,
    PRIVATE_INFO_HEADER,
    LEGAL_DISCLAIMER,
    SECTION_HEADER,
    QUESTION
}
