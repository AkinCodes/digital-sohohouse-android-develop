package com.sohohouse.seven.profile

import com.sohohouse.seven.R
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.common.extensions.nullIfBlank
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.network.core.models.Interest
import com.sohohouse.seven.profile.edit.EditProfileAdapterItemType
import com.sohohouse.seven.profile.edit.IndustryOption
import com.sohohouse.seven.profile.edit.pronouns.GenderPronoun
import java.io.Serializable
import kotlin.collections.HashSet

sealed class ProfileField<T>(open var data: T) : Errorable, DiffItem, Serializable {

    companion object {
        val ERROR_CODES_MAP = mapOf(
            "INVALID_WEBSITE" to SocialMedia::class,
            "INVALID_LINKEDIN_URL" to SocialMedia::class,
            "INVALID_SPOTIFY_URL" to SocialMedia::class,
            "INVALID_YOUTUBE_URL" to SocialMedia::class,
            "WEBSITE_TOO_LONG" to SocialMedia::class,
            "ASK_ME_ABOUT_TOO_LONG" to AskMeAbout::class,
            "INVALID_INTERESTS" to Interests::class,
            "TOO_MANY_INTERESTS" to Interests::class,
            "INVALID_PHONE_NUMBER" to Phone::class,
            "OBSCENE_BIO" to Question::class,
            "BIO_TOO_LONG" to Question::class,
            "OBSCENE_ASK_ME_ABOUT" to AskMeAbout::class
        )
    }

    override val key: Any?
        get() = javaClass

    data class Name(override var data: String?) : ProfileField<String?>(data)
    data class Dob(override var data: String?) : ProfileField<String?>(data)
    data class Address(override var data: String?) : ProfileField<String?>(data)
    data class Question(override var data: QuestionAndAnswer?) :
        ProfileField<QuestionAndAnswer?>(data)

    data class Industry(override var data: IndustryOption?) : ProfileField<IndustryOption?>(data)
    data class AskMeAbout(override var data: String?) : ProfileField<String?>(data)
    data class Interests(override var data: List<Interest>?) : ProfileField<List<Interest>?>(data)
    data class Phone(override var data: String?) : ProfileField<String?>(data)
    data class PreferredHouses(override var data: String?) : ProfileField<String?>(data)
    data class Occupation(override var data: String?) : ProfileField<String?>(data)
    data class City(override var data: String?) : ProfileField<String?>(data)
    data class Pronouns(override var data: List<GenderPronoun>) :
        ProfileField<List<GenderPronoun>>(data) {
        fun sortedList(): List<GenderPronoun> = data.sortedBy { it.type.order }
        fun stringValues(): List<String> = sortedList().map { it.name }
    }

    data class SocialMedia(override var data: List<SocialMediaItem>, var optIn: Boolean) :
        ProfileField<List<SocialMediaItem>>(data) {
        override fun clearErrors() {
            super.clearErrors()
            data.forEach { it.clearErrors() }
        }

        override fun addError(error: Error) {
            super.addError(error)
            data.associateBy { it.type }.get(SocialMediaItem.ERROR_CODES_MAP.get(error.errorCode))
                ?.addError(error)
        }
    }

    fun getPublicDisplayValue(stringProvider: StringProvider): String? {
        return when (this) {
            is Name -> data
            is Dob -> data
            is Address -> data
            is SocialMedia -> {
                if (optIn) data.filter { it.hasValue }.joinToString { it.name } else null
            }
            is Question -> data?.answer
            is Industry -> {
                val nonValues = arrayOf(
                    stringProvider.getString(R.string.prefer_not_to_say),
                    stringProvider.getString(R.string.none_of_the_above)
                )
                return if (data?.value in nonValues) null else data?.value
            }
            is AskMeAbout -> data
            is Interests -> data?.mapNotNull { it.name }?.joinToString().nullIfBlank()
            is Phone -> data
            is PreferredHouses -> data
            is Occupation -> {
                val nonValues = arrayOf(
                    stringProvider.getString(R.string.prefer_not_to_say),
                    stringProvider.getString(R.string.job_not_specified)
                )
                return if (data in nonValues) null else data
            }
            is City -> data
            is Pronouns -> {
                sortedList().joinToString(" · ") { it.name }
            }
        }
    }

    fun getEditDisplayValue(stringProvider: StringProvider): String? = when (this) {
        is Name -> data
        is Dob -> data
        is Address -> data
        is SocialMedia -> {
            if (optIn) data.filter { it.hasValue }
                .joinToString { it.name } else stringProvider.getString(R.string.social_media_not_visible)
        }
        is Question -> data?.answer
        is Industry -> data?.value
        is AskMeAbout -> data
        is Interests -> data?.mapNotNull { it.name }?.joinToString().nullIfBlank()
        is Phone -> data
        is PreferredHouses -> data
        is Occupation -> data
        is City -> data
        is Pronouns -> {
            sortedList().joinToString(", ") { it.name }
        }
    }

    fun getLabel(stringProvider: StringProvider): String = when (this) {
        is Name -> stringProvider.getString(R.string.profile_name_label)
        is Dob -> stringProvider.getString(R.string.profile_dob_label)
        is Address -> stringProvider.getString(R.string.profile_address_label)
        is SocialMedia -> stringProvider.getString(R.string.profile_connected_accounts_label)
        is Question -> data?.question ?: ""
        is Industry -> stringProvider.getString(R.string.profile_industry_label)
        is AskMeAbout -> stringProvider.getString(R.string.profile_offer_label)
        is Interests -> stringProvider.getString(R.string.profile_interests_label)
        is Phone -> stringProvider.getString(R.string.profile_phone_label)
        is PreferredHouses -> stringProvider.getString(R.string.profile_preferred_houses_label)
        is Occupation -> stringProvider.getString(R.string.profile_occupation_label)
        is City -> stringProvider.getString(R.string.profile_city_label)
        is Pronouns -> stringProvider.getString(R.string.label_pronouns)
    }

    val viewSortOrder: Int
        get() = when (this) {
            is Question -> 0
            is AskMeAbout -> 1
            is Industry -> 2
            is Interests -> 3
            else -> -1
        }

    val placeholder: Int?
        get() = when (this) {
            is Question -> R.string.profile_bio_placeholder
            is PreferredHouses -> R.string.profile_preferred_houses_placeholder
            is Occupation -> R.string.profile_occupation_placeholder
            is City -> R.string.profile_city_placeholder
            is AskMeAbout -> R.string.profile_looking_label
            is Phone -> R.string.profile_phone_placeholder
            is Industry -> R.string.profile_industry_placeholder
            else -> null
        }

    val isPublicField: Boolean
        get() = when (this) {
            is Name, is Dob, is Address, is Phone -> false
            else -> true
        }

    val editCellType: EditProfileAdapterItemType
        get() = when (this) {
            is AskMeAbout, is Occupation, is City, is PreferredHouses, is Name, is Dob, is Address, is Phone, is Pronouns -> {
                EditProfileAdapterItemType.STANDARD_FIELD
            }
            is Industry -> EditProfileAdapterItemType.DROPDOWN_FIELD
            is SocialMedia, is Interests -> EditProfileAdapterItemType.MULTIPLE_VALUES
            is Question -> EditProfileAdapterItemType.SECTION_HEADER
        }

    val editSortOrder: Int
        get() = when (this) {
            is Occupation -> 0
            is Industry -> 1
            is City -> 2
            is Pronouns -> 3
            is PreferredHouses -> 4
            is AskMeAbout -> 5
            is Interests -> 6
            is SocialMedia -> 7
            is Question -> 8
            is Name -> 9
            is Dob -> 10
            is Address -> 11
            is Phone -> 12
        }

    val isEditable: Boolean
        get() = when (this) {
            is Name, is Dob, is Address -> false
            else -> true
        }

    val hasValue: Boolean
        get() = when (this) {
            is Name -> data.isNullOrEmpty().not()
            is Dob -> data.isNullOrEmpty().not()
            is Address -> data.isNullOrEmpty().not()
            is Question -> !data?.answer.isNullOrEmpty()
            is Industry -> !data?.value.isNullOrEmpty()
            is AskMeAbout -> data.isNullOrEmpty().not()
            is Interests -> data.isNullOrEmpty().not()
            is Phone -> data.isNullOrEmpty().not()
            is PreferredHouses -> data.isNullOrEmpty().not()
            is Occupation -> data.isNullOrEmpty().not()
            is City -> data.isNullOrEmpty().not()
            is SocialMedia -> data.isNullOrEmpty().not()
            is Pronouns -> data.isNullOrEmpty().not()
        }

    val maxChars: Int
        get() = when (this) {
            is Question, is AskMeAbout -> 200
            else -> -1
        }

    override val errors = HashSet<Error>()
}

data class QuestionAndAnswer(val question: String, var answer: String?)

