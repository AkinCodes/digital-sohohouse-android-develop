package com.sohohouse.seven.profile.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sohohouse.seven.base.error.ErrorHelper
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.addTo
import com.sohohouse.seven.common.extensions.asEnumOrDefault
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.models.Account
import com.sohohouse.seven.network.core.models.Interest
import com.sohohouse.seven.network.core.models.Occupation
import com.sohohouse.seven.network.core.models.Profile
import com.sohohouse.seven.profile.*
import com.sohohouse.seven.profile.edit.pronouns.GenderPronoun
import io.reactivex.SingleTransformer
import io.reactivex.functions.Consumer
import javax.inject.Inject
import kotlin.reflect.KClass

class EditProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val industriesRepository: IndustriesRepository,
    private val profileQuestionsRepository: ProfileQuestionsRepository,
    private val stringProvider: StringProvider,
    analyticsManager: AnalyticsManager
) : BaseViewModel(analyticsManager),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    ErrorViewStateViewModel by ErrorViewStateViewModelImpl(),
    ErrorDialogViewModel by ErrorDialogViewModelImpl() {

    val profileAdapterItems = MutableLiveData<List<EditProfileAdapterItem>>()
    val itemChangeEvent = LiveEvent<ItemChangeEvent>().also {
        it.observeForever { if (it.payload != DropDownStateToggle) _changesMade.value = true }
    }
    val openEditorEvent = LiveEvent<ProfileField<*>>()
    val saveSuccessEvent = LiveEvent<Any>()
    val profilePhotoUpdatedEvent = LiveEvent<Profile>()
    val showDiscardChangesMsgEvent = LiveEvent<Any>()
    val closeScreenEvent = LiveEvent<Any>()
    private val _changesMade = MutableLiveData(false)
    val changesMade: LiveData<Boolean> get() = _changesMade
    val showConfirmChangeQuestionDialog = LiveEvent<(confirmed: Boolean) -> Unit>()

    private var existingProfile: Profile? = null
    private var existingAccount: Account? = null

    fun fetchData() {
        profileRepository.getMyAccountWithProfile()
            .compose(loadTransformer())
            .compose(errorViewStateTransformer())
            .subscribe(Consumer {
                when (it) {
                    is Either.Value -> {
                        existingProfile = it.value.profile
                        existingAccount = it.value
                        profileAdapterItems.postValue(
                            createListItems(
                                existingProfile!!,
                                existingAccount!!
                            )
                        )
                    }
                }
            }).addTo(compositeDisposable)
    }

    private fun createListItems(profile: Profile, account: Account): List<EditProfileAdapterItem> {
        return ProfileAdapterItemFactory.createEditProfileItems(
            profile,
            account,
            industriesRepository.getIndustries(),
            profileQuestionsRepository.questions.toMutableList(),
            stringProvider
        )
    }

    override fun reloadDataAfterError() {
        fetchData()
    }

    override fun onScreenViewed() {
        setScreenNameInternal(AnalyticsManager.Screens.EditProfile.name)
    }

    fun onEditProfileFieldClick(item: EditProfileAdapterItem.Field<*>) {
        if (item is EditProfileAdapterItem.Field.Dropdown) {
            insertDropdownSelectorItem(item)
        } else {
            openEditorEvent.value = item.field
        }
    }

    private fun insertDropdownSelectorItem(item: EditProfileAdapterItem.Field.Dropdown<*>) {
        val items = ArrayList(profileAdapterItems.value ?: return)

        val itemIndex = items.indexOf(item)

        if (item.state == EditProfileAdapterItem.Field.Dropdown.State.CLOSED) {
            // Add the dropdown selector item with the field options below the dropdown field item
            val dropdownSelectorItem =
                EditProfileAdapterItem.DropdownSelector(item.field, item.multipleChoiceOptions)
            items.add(itemIndex + 1, dropdownSelectorItem)

            when (val field: ProfileField<*> = item.field) {
                is ProfileField.Industry -> analyticsManager.logEventAction(AnalyticsManager.Action.EditProfileNatureOfBusiness)
            }
        } else {
            //remove the dropdown selector
            (items[itemIndex + 1] as? EditProfileAdapterItem.DropdownSelector)?.let {
                items.remove(
                    it
                )
            }
        }

        //toggle the state of the dropdown field
        item.toggle()

        itemChangeEvent.value = ItemChangeEvent(itemIndex, DropDownStateToggle)
        profileAdapterItems.value = items
    }

    fun <T : PickerItem?> onOptionSelected(field: ProfileField<out T>, option: T?) {
        val items = ArrayList(profileAdapterItems.value ?: return)

        val changeIndex =
            items.indexOfFirst { it is EditProfileAdapterItem.Field.Dropdown<*> && it.field == field }

        when (field) {
            is ProfileField.Industry -> {
                field.data = option as IndustryOption?
            }
        }

        onFieldEdited(field, changeIndex)
    }

    private inline fun <reified T : ProfileField<*>> updateField(field: T) {
        val items = profileAdapterItems.value ?: return

        val adapterItem = items.getItem(T::class) ?: return
        adapterItem.field = field
        onFieldEdited(field, items.indexOf(adapterItem))
    }

    private fun onFieldEdited(field: ProfileField<*>, changeIndex: Int) {
        field.clearErrors()
        itemChangeEvent.value = ItemChangeEvent(changeIndex)
    }

    fun onAskMeAboutUpdated(value: String) {
        updateField(ProfileField.AskMeAbout(value))
    }

    fun onConnectedAccountsUpdated(field: ProfileField.SocialMedia) {
        updateField(field)
    }

    fun onOccupationUpdated(occupation: Occupation?) {
        updateField(ProfileField.Occupation(occupation?.name))
    }

    fun onCityUpdated(city: String?) {
        updateField(ProfileField.City(city))
    }

    fun onPhoneUpdated(phone: String) {
        updateField(ProfileField.Phone(phone))
    }

    fun <T : ProfileField<*>> List<EditProfileAdapterItem>.getItem(field: KClass<out T>): EditProfileAdapterItem.Field<T>? {
        return filterIsInstance<EditProfileAdapterItem.Field<*>>()
            .associateBy { it.field::class }
            .get(field) as EditProfileAdapterItem.Field<T>?
    }

    fun onInterestsUpdated(interests: List<Interest>) {
        updateField(ProfileField.Interests(interests))
    }

    fun onSaveClick() {
        analyticsManager.logEventAction(AnalyticsManager.Action.SaveProfileDidTap)
        if (existingProfile != null && existingAccount != null && profileAdapterItems.value != null) {
            val fields =
                profileAdapterItems.value!!.filterIsInstance(EditProfileAdapterItem.Field::class.java)
                    .map {
                        it.field
                    }.toMutableList().apply {
                        val questionItem =
                            profileAdapterItems.value?.filterIsInstance<EditProfileAdapterItem.Question>()
                                ?.firstOrNull { it.question.answer?.isNotEmpty() == true }
                        add(
                            ProfileField.Question(
                                QuestionAndAnswer(
                                    questionItem?.question?.question ?: "",
                                    questionItem?.question?.answer
                                )
                            )
                        )
                    }
            saveProfileWithAccount(
                SaveProfileBuilder.buildProfileForSave(
                    existingProfile!!,
                    fields,
                    confirmed = false
                )
            )
        }
    }

    private fun saveProfileWithAccount(data: ProfileUpdate) {
        profileRepository.saveProfileWithAccountUpdate(data.profile, data.accountUpdate)
            .compose(loadTransformer())
            .compose(errorDialogTransformer())
            .compose(errorStatesTransformer())
            .subscribe(Consumer {
                when (it) {
                    is Either.Value -> {
                        analyticsManager.logEventAction(AnalyticsManager.Action.SaveProfileSuccess)
                        saveSuccessEvent.postEvent()
                    }
                }
            }).addTo(compositeDisposable)
    }

    private fun <T> errorStatesTransformer(): SingleTransformer<Either<ServerError, T>, Either<ServerError, T>> {
        return SingleTransformer { single ->
            return@SingleTransformer single.doAfterSuccess {
                if (it is Either.Error<*>) {
                    analyticsManager.logEventAction(AnalyticsManager.Action.SaveProfileFail)
                    if (it.error is ServerError.ApiError) {
                        setErrorsOnFields((it.error as ServerError.ApiError).errorCodes)
                    }
                }
            }
        }
    }

    private fun setErrorsOnFields(errorCodes: Array<out String>) {
        val adapterItems = profileAdapterItems.value ?: return

        errorCodes.forEach { errorCode ->
            val field = ProfileField.ERROR_CODES_MAP.get(errorCode) ?: return
            val adapterItem = adapterItems.getItem(field) ?: return
            adapterItem.field.addError(Error(ErrorHelper.errorCodeMap.get(errorCode), errorCode))
            itemChangeEvent.postValue(ItemChangeEvent(adapterItems.indexOf(adapterItem)))
        }
    }

    fun onAttemptGoBack() {
        if (changesMade()) {
            showDiscardChangesMsgEvent.emitEvent()
        } else {
            closeScreenEvent.emitEvent()
        }
    }

    private fun changesMade() = changesMade.value == true

    fun updateProfileImage(newImageUrl: String) {
        if (existingProfile != null && existingAccount != null) {
            existingProfile!!.imageUrl = newImageUrl
            profileAdapterItems.value = createListItems(existingProfile!!, existingAccount!!)
            profilePhotoUpdatedEvent.value = existingProfile
        }
    }

    fun logSaveProfileImage() {
        analyticsManager.logEventAction(AnalyticsManager.Action.EditProfileImageSaveTap)
    }

    fun logEditPhotoClick() {
        analyticsManager.logEventAction(AnalyticsManager.Action.EditProfileImageSettings)
    }

    fun onUserFocusQuestion(question: EditProfileAdapterItem.Question) {
        val questionItems = profileAdapterItems.value
            ?.filterIsInstance(EditProfileAdapterItem.Question::class.java)
        val filledQuestion = questionItems
            ?.find { !it.question.answer.isNullOrEmpty() }
            ?: return

        if (filledQuestion != question) {
            showConfirmChangeQuestionDialog.value = { confirmed ->
                if (confirmed) {
                    clearAnswerFrom(filledQuestion)
                } else {
                    clearFocusFrom(question)
                }
            }
        }
    }

    private fun clearFocusFrom(question: EditProfileAdapterItem.Question) {
        val index = profileAdapterItems.value?.indexOf(question) ?: return
        itemChangeEvent.value = ItemChangeEvent(index, ClearFocus)
    }

    private fun clearAnswerFrom(question: EditProfileAdapterItem.Question) {
        val index = profileAdapterItems.value?.indexOf(question) ?: return
        question.question.answer = null
        itemChangeEvent.value = ItemChangeEvent(index)
    }

    fun onQuestionAnswerChange(it: EditProfileAdapterItem.Question) {
        _changesMade.value = true
    }

    fun onPronounsUpdated(pronouns: List<String>) {
        updateField(ProfileField.Pronouns(pronouns.mapNotNull {
            it.asEnumOrDefault<GenderPronoun>(
                null
            )
        }))
    }

    data class ItemChangeEvent(val index: Int, val payload: Any? = null)    //TODO remove

    object DropDownStateToggle
    object RequestFocus
    object ClearFocus
}
