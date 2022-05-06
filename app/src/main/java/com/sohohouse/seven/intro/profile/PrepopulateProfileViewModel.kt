package com.sohohouse.seven.intro.profile

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.R
import com.sohohouse.seven.authentication.AuthenticationFlowManager
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.intro.profile.PrepopulateProfileViewModel.UiState.COMPLETED
import com.sohohouse.seven.intro.profile.PrepopulateProfileViewModel.UiState.EDITING
import com.sohohouse.seven.network.core.models.Account
import com.sohohouse.seven.network.core.models.Profile
import com.sohohouse.seven.profile.IndustriesRepository
import com.sohohouse.seven.profile.ProfileField
import com.sohohouse.seven.profile.ProfileFieldFactory
import com.sohohouse.seven.profile.ProfileRepository
import com.sohohouse.seven.profile.edit.IndustryOption
import com.sohohouse.seven.profile.edit.SaveProfileBuilder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class PrepopulateProfileViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    private val flowManager: AuthenticationFlowManager,
    private val profileRepo: ProfileRepository,
    private val industriesRepo: IndustriesRepository,
    private val userManager: UserManager,
    dispatcher: CoroutineDispatcher
) : BaseViewModel(analyticsManager, dispatcher), Loadable.ViewModel by Loadable.ViewModelImpl(),
    ErrorViewStateViewModel by ErrorViewStateViewModelImpl(),
    ErrorDialogViewModel by ErrorDialogViewModelImpl() {

    private val _intent = MutableLiveData<Intent>()
    val intent: LiveData<Intent> get() = _intent

    private val _state = MutableLiveData<UiState>()
    val state: LiveData<UiState> get() = _state

    private val _profileImage = MutableLiveData<String>()
    val profileImage: LiveData<String> get() = _profileImage

    private val _profileName = MutableLiveData<String>()
    val profileName: LiveData<String> get() = _profileName

    private val _fields = MutableLiveData<List<ProfileField<*>>>()
    val fields: LiveData<List<ProfileField<*>>> get() = _fields

    private val _itemChangeEvent = LiveEvent<Int>()
    val itemChangeEvent: LiveData<Int> get() = _itemChangeEvent

    private lateinit var profile: Profile

    val industryOptions: ArrayList<String> get() = ArrayList(industriesRepo.getIndustries())

    init {
        _state.value = EDITING
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)
            profileRepo.getMyAccountWithProfileV2().fold(ifValue = { account ->
                if (account.profile != null) {
                    this@PrepopulateProfileViewModel.profile = account.profile!!
                    onData(profile, account)
                } else {
                    showErrorView()
                }
            }, ifEmpty = {
            }, ifError = {
                showErrorView()
            })
            setLoadingState(LoadingState.Idle)
        }
    }

    fun onProceedBtnClick(context: Context) {
        when (state.value) {
            EDITING -> {
                analyticsManager.logEventAction(AnalyticsManager.Action.PrepopulateProfileChangesConfirm)
                saveProfileAndProceed()
            }
            COMPLETED -> {
                analyticsManager.logEventAction(AnalyticsManager.Action.PrepopulatedReviewProfile)
                userManager.confirmProfile = false
                _intent.value = flowManager.navigateFrom(context, reviewProfile = true)
            }
        }
    }

    fun onCompleteCloseBtnClick(context: Context) {
        analyticsManager.logEventAction(AnalyticsManager.Action.PrepopulatedClose)
        _intent.postValue(flowManager.navigateFrom(context))
    }

    private fun saveProfileAndProceed() {
        val fields = _fields.value ?: return
        val profileUpdate =
            SaveProfileBuilder.buildProfileForSave(profile, fields, confirmed = true)
        viewModelScope.launch(viewModelContext) {
            setLoading()
            profileRepo.saveProfileWithAccountUpdateV2(
                profileUpdate.profile,
                profileUpdate.accountUpdate
            ).fold(ifValue = {
                _state.postValue(COMPLETED)
            }, ifEmpty = {
            }, ifError = {
                handleError(it)
            })
            setIdle()
        }
    }

    fun onBackPress(): Boolean {
        if (_state.value == COMPLETED) {
            _state.value = EDITING
            return true
        }
        return false
    }

    private fun onData(profile: Profile, account: Account) {
        viewModelScope.launch(Dispatchers.Main) {
            _profileName.postValue(profile.firstName)
            _profileImage.postValue(profile.imageUrl)
            val fields = ProfileFieldFactory.getFields(
                profile,
                account,
                ProfileField.Industry::class.java,
                ProfileField.Occupation::class.java,
                ProfileField.City::class.java
            )
                .filter { it.hasValue }
                .sortedBy { it.formSortOrder }
            _fields.value = fields
        }
    }

    fun onCityPick(city: String?) {
        if (city.isNullOrEmpty()) return //do nothing; we don't allow them to remove a value
        analyticsManager.logEventAction(
            AnalyticsManager.Action.PrepopulateProfileFieldEdit, bundleOf(
                AnalyticsManager.ProfileFields.CITY to city
            )
        )
        val adapterItems = fields.value ?: return
        val adapterItem = adapterItems.filterIsInstance(ProfileField.City::class.java).firstOrNull()
            ?: return
        adapterItem.data = city
        _itemChangeEvent.value = adapterItems.indexOf(adapterItem)
    }

    fun onOccupationPick(occupation: String?) {
        if (occupation.isNullOrEmpty()) return //do nothing; we don't allow them to remove a value
        analyticsManager.logEventAction(
            AnalyticsManager.Action.PrepopulateProfileFieldEdit, bundleOf(
                AnalyticsManager.ProfileFields.OCCUPTATION to occupation
            )
        )
        val adapterItems = fields.value ?: return
        val adapterItem =
            adapterItems.filterIsInstance(ProfileField.Occupation::class.java).firstOrNull()
                ?: return
        adapterItem.data = occupation
        _itemChangeEvent.value = adapterItems.indexOf(adapterItem)
    }

    fun onIndustryPick(industry: String?) {
        if (industry.isNullOrEmpty()) return //do nothing; we don't allow them to remove a value
        analyticsManager.logEventAction(
            AnalyticsManager.Action.PrepopulateProfileFieldEdit, bundleOf(
                AnalyticsManager.ProfileFields.INDUSTRY to industry
            )
        )
        val adapterItems = fields.value ?: return
        val adapterItem =
            adapterItems.filterIsInstance(ProfileField.Industry::class.java).firstOrNull()
                ?: return
        adapterItem.data = IndustryOption(industry)
        _itemChangeEvent.value = adapterItems.indexOf(adapterItem)
    }

    private val ProfileField<*>.formSortOrder: Int
        get() = when (this) {
            is ProfileField.Occupation -> 0
            is ProfileField.Industry -> 1
            is ProfileField.City -> 2
            else -> -1
        }

    enum class UiState(val pagerPos: Int, @StringRes val buttonText: Int) {
        EDITING(0, R.string.cta_confirm),
        COMPLETED(1, R.string.cta_review_profile)
    }
}
