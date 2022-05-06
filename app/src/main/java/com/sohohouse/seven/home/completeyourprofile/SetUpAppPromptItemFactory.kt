package com.sohohouse.seven.home.completeyourprofile

import com.sohohouse.seven.R
import com.sohohouse.seven.common.prefs.PrefsManager
import com.sohohouse.seven.network.core.models.Profile

class SetUpAppPromptItemFactory(
    private val profile: Profile,
    private val prefsManager: PrefsManager
) {


    fun createItems(): List<SetUpAppPromptItem> {
        return ArrayList<SetUpAppPromptItem>().apply {
            val fields = listOf(
                profile.interests,
                profile.bio,
                profile.askMeAbout,
                profile.website,
                profile.twitterUrl,
                profile.youtubeUrl,
                profile.spotifyUrl,
                profile.instagramUrl,
                profile.linkedInUrl,
                profile.city,
                profile.industry,
                profile.occupation
            )

            val completedFields = fields.filterNotNull()

            val profileHasBeenEdited = completedFields.isNotEmpty()
            val hasProfilePhoto = profile.imageUrl.isNotEmpty()

            if (!profileHasBeenEdited || !hasProfilePhoto) {
                add(createCompleteYourProfileItem())
            }

            if (!prefsManager.notificationsCustomised) {
                add(createCustomiseNotificationsItem())
            }
        }
    }

    private fun createCustomiseNotificationsItem(): SetUpAppPromptItem {
        return SetUpAppPromptItem(
            image = "",
            placeholder = R.drawable.ic_add_branding,
            title = R.string.customise_your_notifications,
            subtitle = R.string.customise_your_notifications_secondary,
            prompt = SetUpAppPromptItem.Prompt.CUSTOMISE_NOTIFICATIONS
        )
    }

    private fun createCompleteYourProfileItem(): SetUpAppPromptItem {
        return SetUpAppPromptItem(
            profile.imageUrl,
            R.drawable.ic_add_branding,
            R.string.complete_your_profile,
            R.string.complete_your_profile_subtext,
            SetUpAppPromptItem.Prompt.COMPLETE_PROFILE
        )
    }

}