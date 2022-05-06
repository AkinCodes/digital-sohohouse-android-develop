package com.sohohouse.seven.profile.edit

import com.sohohouse.seven.common.extensions.nullIfBlank
import com.sohohouse.seven.network.core.models.Interest
import com.sohohouse.seven.network.core.models.Profile
import com.sohohouse.seven.network.core.models.ProfileAccountUpdate
import com.sohohouse.seven.profile.ProfileField
import com.sohohouse.seven.profile.SocialMediaItem
import moe.banana.jsonapi2.HasMany
import moe.banana.jsonapi2.ResourceIdentifier
import java.util.*

object SaveProfileBuilder {

    fun buildProfileForSave(
        existingProfile: Profile,
        fields: List<ProfileField<*>>,
        confirmed: Boolean = false
    ) //confirmed = if the user is confirming (prepopulated) profile info
            : ProfileUpdate {

        val socialMediaField = fields.getField(ProfileField.SocialMedia::class.java)
        val occupationField = fields.getField(ProfileField.Occupation::class.java)
        val industryField = fields.getField(ProfileField.Industry::class.java)
        val askMeAboutField = fields.getField(ProfileField.AskMeAbout::class.java)
        val interestsField = fields.getField(ProfileField.Interests::class.java)
        val bioField = fields.getField(ProfileField.Question::class.java)
        val cityField = fields.getField(ProfileField.City::class.java)
        val pronounsField = fields.getField(ProfileField.Pronouns::class.java)

        val newProfile = existingProfile.copy(
            _imageUrl = existingProfile.imageUrl.nullIfBlank(),
            occupation = if (occupationField != null) occupationField.data else existingProfile.occupation,
            industry = if (industryField != null) industryField.data?.value else existingProfile.industry,
            instagramHandle = if (socialMediaField != null) getHandle(
                socialMediaField,
                SocialMediaItem.Type.INSTAGRAM
            ) else existingProfile.instagramHandle,
            instagramUrl = if (socialMediaField != null) getUrl(
                socialMediaField,
                SocialMediaItem.Type.INSTAGRAM
            ) else existingProfile.instagramUrl,
            twitterHandle = if (socialMediaField != null) getHandle(
                socialMediaField,
                SocialMediaItem.Type.TWITTER
            ) else existingProfile.twitterHandle,
            twitterUrl = if (socialMediaField != null) getUrl(
                socialMediaField,
                SocialMediaItem.Type.TWITTER
            ) else existingProfile.twitterUrl,
            spotifyUrl = if (socialMediaField != null) getUrl(
                socialMediaField,
                SocialMediaItem.Type.SPOTIFY
            ) else existingProfile.spotifyUrl,
            spotifyHandle = if (socialMediaField != null) getHandle(
                socialMediaField,
                SocialMediaItem.Type.SPOTIFY
            ) else existingProfile.spotifyHandle,
            youtubeUrl = if (socialMediaField != null) getUrl(
                socialMediaField,
                SocialMediaItem.Type.YOUTUBE
            ) else existingProfile.youtubeUrl,
            youtubeHandle = if (socialMediaField != null) getHandle(
                socialMediaField,
                SocialMediaItem.Type.YOUTUBE
            ) else existingProfile.youtubeHandle,
            linkedInUrl = if (socialMediaField != null) getUrl(
                socialMediaField,
                SocialMediaItem.Type.LINKEDIN
            ) else existingProfile.linkedInUrl,
            linkedInHandle = if (socialMediaField != null) getHandle(
                socialMediaField,
                SocialMediaItem.Type.LINKEDIN
            ) else existingProfile.linkedInHandle,
            website = if (socialMediaField != null) getUrl(
                socialMediaField,
                SocialMediaItem.Type.WEBSITE
            ) else existingProfile.website,
            socialsOptIn = if (socialMediaField != null) socialMediaField.optIn else existingProfile.socialsOptIn,
            askMeAbout = if (askMeAboutField != null) askMeAboutField.data else existingProfile.askMeAbout,
            interestsResource = if (interestsField != null) buildInterestsRelationship(fields) else existingProfile.interestsResource,
            bio = if (bioField != null) bioField.data?.answer else existingProfile.bio,
            bioQuestion = if (bioField != null) bioField.data?.question else existingProfile.bioQuestion,
            city = if (cityField != null) cityField.data else existingProfile.city,
            confirmedAt = if (confirmed) Date() else existingProfile.confirmedAt,
            pronouns = if (pronounsField != null) pronounsField.data.map { it.name } else existingProfile.pronouns)

        val phoneNumber = fields.getField(ProfileField.Phone::class.java)?.data
        var accountUpdate: ProfileAccountUpdate? = null
        if (phoneNumber.nullIfBlank() != existingProfile.account?.phoneNumber.nullIfBlank()) {
            accountUpdate = ProfileAccountUpdate(phoneNumber)
        }
        return ProfileUpdate(newProfile, accountUpdate)
    }

    private fun buildInterestsRelationship(fields: List<ProfileField<*>>): HasMany<Interest> {
        val field = fields.getField(ProfileField.Interests::class.java) ?: return HasMany()
        val interests = field.data ?: return HasMany()
        val resources = interests.map { ResourceIdentifier("interests", it.id) }
        return HasMany(*resources.toTypedArray())
    }

    private fun getHandle(
        socialAccounts: ProfileField.SocialMedia?,
        type: SocialMediaItem.Type
    ): String? {
        return socialAccounts?.data?.getSocialMediaItem(type)?.handle
    }

    private fun getUrl(
        socialAccounts: ProfileField.SocialMedia?,
        type: SocialMediaItem.Type
    ): String? {
        return socialAccounts?.data?.getSocialMediaItem(type)?.url
    }

    fun <T : ProfileField<*>> List<ProfileField<*>>.getField(clazz: Class<T>): T? {
        return filterIsInstance(clazz).firstOrNull()
    }

    private fun List<SocialMediaItem>.getSocialMediaItem(type: SocialMediaItem.Type): SocialMediaItem? {
        return firstOrNull { it.type == type }
    }

}

data class ProfileUpdate(val profile: Profile, val accountUpdate: ProfileAccountUpdate?) {
    val includesAccountUpdate = accountUpdate != null
}
