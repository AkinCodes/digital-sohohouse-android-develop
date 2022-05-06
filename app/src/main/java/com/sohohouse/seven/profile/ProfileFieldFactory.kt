package com.sohohouse.seven.profile

import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.network.core.models.Account
import com.sohohouse.seven.network.core.models.Profile
import com.sohohouse.seven.profile.edit.IndustryOption
import com.sohohouse.seven.profile.edit.pronouns.GenderPronoun

object ProfileFieldFactory {
    private fun getAllFields(profile: Profile, account: Account?): List<ProfileField<*>> {
        return ArrayList<ProfileField<*>>().apply {

            val bioQuestionAndAnswer = if (profile.bioQuestion.isNullOrEmpty().not()
                && profile.bio.isNullOrEmpty().not()
            )
                QuestionAndAnswer(profile.bioQuestion!!, profile.bio!!)
            else null

            add(ProfileField.Question(bioQuestionAndAnswer))
            add(ProfileField.City(profile.city))
            add(ProfileField.Address(account?.address?.formatAddress().nullIfBlank()))
            add(ProfileField.Name(profile.fullName))
            add(ProfileField.Industry(profile.industry?.let { IndustryOption(it) }))
            add(ProfileField.Interests(profile.interests))
            add(ProfileField.SocialMedia(getSocialAccounts(profile), profile.socialsOptIn))
            add(ProfileField.Occupation(profile.occupation))
            add(ProfileField.Dob(account?.dateOfBirth?.getFormattedDate().nullIfBlank()))
            add(ProfileField.AskMeAbout(profile.askMeAbout))
            add(ProfileField.Phone(account?.phoneNumber))
            add(ProfileField.Pronouns(profile.pronouns.mapNotNull {
                it.asEnumOrDefault<GenderPronoun>(
                    null
                )
            }))
        }
    }

    fun getPronounsField(pronouns: List<String>): ProfileField.Pronouns {
        return ProfileField.Pronouns(pronouns.mapNotNull { it.asEnumOrDefault<GenderPronoun>(null) })
    }

    fun getFields(
        profile: Profile,
        account: Account?,
        vararg fields: Class<out ProfileField<*>>
    ): List<ProfileField<*>> {
        return getAllFields(profile, account).filter { it.javaClass in fields }
    }

    fun getViewPublicFields(
        profile: Profile,
        stringProvider: StringProvider
    ): List<ProfileField<*>> {
        return getAllFields(profile, account = null)
            .filter {
                it.isPublicField && !it.getPublicDisplayValue(stringProvider).isNullOrBlank()
            }
            .sortedBy { it.viewSortOrder }
    }

    fun getSocialAccounts(profile: Profile): List<SocialMediaItem> {
        return listOf(
            SocialMediaItem(
                SocialMediaItem.Type.TWITTER,
                profile.twitterUrl,
                profile.twitterHandle
            ),
            SocialMediaItem(SocialMediaItem.Type.YOUTUBE, profile.youtubeUrl),
            SocialMediaItem(SocialMediaItem.Type.SPOTIFY, profile.spotifyUrl),
            SocialMediaItem(SocialMediaItem.Type.WEBSITE, profile.website),
            SocialMediaItem(
                SocialMediaItem.Type.INSTAGRAM,
                profile.instagramUrl,
                profile.instagramHandle
            ),
            SocialMediaItem(SocialMediaItem.Type.LINKEDIN, profile.linkedInUrl)
        )
    }

    fun getEditPublicFields(profile: Profile, account: Account): List<ProfileField<*>> {
        return getAllFields(profile, account).filter { it.isPublicField }
            .sortedBy { it.editSortOrder }
    }

    fun getEditPrivateFields(profile: Profile, account: Account): List<ProfileField<*>> {
        return getAllFields(profile, account).filter { !it.isPublicField }
            .sortedBy { it.editSortOrder }
    }
}