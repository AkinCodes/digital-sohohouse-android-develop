package com.sohohouse.seven.profile

import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.fullName
import com.sohohouse.seven.common.extensions.isNotEmpty
import com.sohohouse.seven.common.form.FormRowType
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.network.core.models.Account
import com.sohohouse.seven.network.core.models.Profile
import com.sohohouse.seven.profile.edit.EditProfileAdapterItem
import com.sohohouse.seven.profile.edit.IndustryOption
import com.sohohouse.seven.profile.view.ViewProfileAdapterItem

object ProfileAdapterItemFactory {

    fun createViewProfileItems(
        profile: Profile,
        stringProvider: StringProvider
    ): List<ViewProfileAdapterItem> {

        return ProfileFieldFactory.getViewPublicFields(profile, stringProvider)
            .filter {
                it is ProfileField.AskMeAbout || it is ProfileField.Industry ||
                        it is ProfileField.Interests || it is ProfileField.Question
            }
            .map { ViewProfileAdapterItem.Field(it) }
    }

    fun createConnectedAccount(profile: Profile): List<SocialMediaItem> {
        if (!profile.socialsOptIn) return emptyList()
        return ProfileFieldFactory.getSocialAccounts(profile).filter { it.hasValue }
    }

    fun createEditProfileItems(
        profile: Profile,
        account: Account,
        industryOptions: List<String>,
        predefinedQuestions: MutableList<String>,
        stringProvider: StringProvider
    ): List<EditProfileAdapterItem> {
        return ArrayList<EditProfileAdapterItem>().apply {
            add(EditProfileAdapterItem.Header(profile.fullName, profile.imageUrl))

            ProfileFieldFactory.getEditPublicFields(profile, account).forEach { profileField ->
                when (profileField) {
                    is ProfileField.Industry -> {
                        add(
                            EditProfileAdapterItem.Field.Dropdown(
                                profileField,
                                multipleChoiceOptions = industryOptions.map { IndustryOption(it) })
                        )
                    }
                    is ProfileField.Question -> {
                        add(
                            EditProfileAdapterItem.SectionHeader(
                                title = stringProvider.getString(R.string.label_edit_profile_questions),
                                subtitle = stringProvider.getString(R.string.description_edit_profile_questions)
                            )
                        )

                        val savedQuestion = profileField.data?.question
                        val savedAnswer = profileField.data?.answer
                        if (savedQuestion.isNotEmpty()
                            && savedAnswer.isNotEmpty()
                            && predefinedQuestions.none { it == savedQuestion }
                        ) {
                            //If answered question is unrecognised, include it
                            predefinedQuestions.add(savedQuestion!!)
                        }
                        predefinedQuestions.forEachIndexed { index, question ->
                            val answer = if (savedQuestion == question) savedAnswer else null
                            val rowType = FormRowType.rowTypeFor(predefinedQuestions.size, index)
                            add(
                                EditProfileAdapterItem.Question(
                                    QuestionAndAnswer(question, answer),
                                    rowType
                                )
                            )
                        }

                    }
                    else -> add(EditProfileAdapterItem.Field.Standard(profileField))
                }
            }

            add(EditProfileAdapterItem.PrivateInfoHeader)

            ProfileFieldFactory.getEditPrivateFields(profile, account).forEach { profileField ->
                add(EditProfileAdapterItem.Field.Standard(profileField))
            }

            add(EditProfileAdapterItem.LegalDisclaimer)
        }
    }

}
