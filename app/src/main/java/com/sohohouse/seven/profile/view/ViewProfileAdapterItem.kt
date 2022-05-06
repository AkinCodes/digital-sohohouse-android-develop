package com.sohohouse.seven.profile.view

import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.profile.ProfileField
import com.sohohouse.seven.profile.SocialMediaItem

sealed class ViewProfileAdapterItem(val cellType: ViewProfileAdapterItemType) : DiffItem {

    data class Header constructor(
        val name: String,
        val imageUrl: String?,
        val occupation: String?,
        val city: String?,
        val placeholder: Int
    ) : ViewProfileAdapterItem(ViewProfileAdapterItemType.HEADER) {
        override val key: Any?
            get() = cellType
    }

    data class Field(val field: ProfileField<*>) :
        ViewProfileAdapterItem(ViewProfileAdapterItemType.TEXT_FIELD) {
        override val key: Any?
            get() = this.field::class
    }

    data class QuestionAndAnswer(val field: ProfileField.Question) :
        ViewProfileAdapterItem(ViewProfileAdapterItemType.QUESTION_AND_ANSWER) {
        override val key: Any?
            get() = this.field::class
    }

    data class ConnectedAccounts(val socialMediaItems: List<SocialMediaItem>) :
        ViewProfileAdapterItem(ViewProfileAdapterItemType.CONNECTED_ACCOUNTS) {
        override val key: Any?
            get() = cellType
    }
}


enum class ViewProfileAdapterItemType {
    HEADER,
    CONNECTED_ACCOUNTS,
    TEXT_FIELD,
    QUESTION_AND_ANSWER
}

