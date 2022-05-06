package com.sohohouse.seven.profile.edit

import android.text.InputType
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.bottomsheet.BottomSheetFactory
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.profile.ProfileField
import com.sohohouse.seven.profile.edit.interests.EditInterestsBottomSheet
import com.sohohouse.seven.profile.edit.pronouns.EditPronounsFragment
import com.sohohouse.seven.profile.edit.socialmedia.EditSocialMediaBottomSheet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EditProfileFlowManager @Inject constructor(private val eventTracking: AnalyticsManager) {

    companion object {
        const val REQ_CODE_EDIT_ASK_ME_ABOUT = 222
        const val REQ_CODE_EDIT_PHONE = 333
    }

    fun createEditorBottomSheet(
        profileField: ProfileField<*>,
        stringProvider: StringProvider
    ): BottomSheetFactory? {
        return when (profileField) {
            is ProfileField.AskMeAbout -> {
                eventTracking.logEventAction(AnalyticsManager.Action.EditProfileLetsChat)
                TextAreaBottomSheet.Companion.Factory(
                    profileField.getLabel(stringProvider),
                    profileField.getEditDisplayValue(stringProvider),
                    profileField.placeholder,
                    profileField.maxChars,
                    REQ_CODE_EDIT_ASK_ME_ABOUT
                )
            }
            is ProfileField.SocialMedia -> {
                eventTracking.logEventAction(AnalyticsManager.Action.EditProfileSocialAccounts)
                EditSocialMediaBottomSheet.Companion.Factory(profileField)
            }
            is ProfileField.Occupation -> {
                eventTracking.logEventAction(AnalyticsManager.Action.EditProfileOccupation)
                EditOccupationBottomSheet.Companion.Factory(
                    profileField.getEditDisplayValue(
                        stringProvider
                    )
                )
            }
            is ProfileField.City -> {
                eventTracking.logEventAction(AnalyticsManager.Action.EditProfileCity)
                EditCityBottomSheet.Companion.Factory(
                    profileField.getEditDisplayValue(
                        stringProvider
                    )
                )
            }
            is ProfileField.Phone -> {
                eventTracking.logEventAction(AnalyticsManager.Action.EditProfilePhoneNumber)
                TextAreaBottomSheet.Companion.Factory(
                    profileField.getLabel(stringProvider),
                    profileField.getEditDisplayValue(stringProvider),
                    profileField.placeholder,
                    requestCode = REQ_CODE_EDIT_PHONE,
                    inputType = InputType.TYPE_CLASS_PHONE,
                    maxLines = 1
                )
            }
            is ProfileField.Interests -> {
                eventTracking.logEventAction(AnalyticsManager.Action.EditProfileInterests)
                EditInterestsBottomSheet.Companion.Factory(profileField.data)
            }
            is ProfileField.Pronouns -> {
                EditPronounsFragment.Companion.Factory(profileField.stringValues().toMutableList())
            }
            else -> null
        }
    }
}