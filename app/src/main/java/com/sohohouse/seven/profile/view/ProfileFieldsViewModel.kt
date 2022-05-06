package com.sohohouse.seven.profile.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.prefs.PrefsManager
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.home.completeyourprofile.SetUpAppPromptItemFactory
import com.sohohouse.seven.network.core.models.Profile
import com.sohohouse.seven.profile.ProfileAdapterItemFactory
import com.sohohouse.seven.profile.ProfileRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFieldsViewModel @AssistedInject constructor(
    private val profileRepository: ProfileRepository,
    private val userManager: UserManager,
    private val prefsManager: PrefsManager,
    private val stringProvider: StringProvider,
    @Assisted private val profileId: String,
    analyticsManager: AnalyticsManager,
    dispatcher: CoroutineDispatcher = Dispatchers.Unconfined
) : BaseViewModel(analyticsManager, dispatcher),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    Errorable.ViewModel by Errorable.ViewModelImpl() {

    private val isMyProfile: Boolean
        get() = profileId == userManager.profileID

    val profileListItems = MutableLiveData<List<DiffItem>>()

    fun fetchData() {
        viewModelScope.launch(viewModelContext) {
            setLoading()
            getProfile().fold(
                ifValue = { createAndEmitItems(it) },
                ifError = { showError(it.toString()) },
                ifEmpty = { }
            )
            setIdle()
        }
    }

    private fun createAndEmitItems(profile: Profile) {
        val items = createListItems(profile)
        if (isMyProfile && items.none { it is ViewProfileAdapterItem.Field }) {
            val elements = SetUpAppPromptItemFactory(profile, prefsManager).createItems()
            if (elements.isNotEmpty()) items.add(
                BaseAdapterItem.SetUpAppPromptItem.Container(
                    elements
                )
            )
        }
        profileListItems.postValue(items)
    }

    fun refreshData() {
        fetchData()
    }

    private fun createListItems(profile: Profile): MutableList<DiffItem> {
        return ProfileAdapterItemFactory.createViewProfileItems(profile, stringProvider)
            .toMutableList()
    }

    private fun getProfile() =
        if (isMyProfile) profileRepository.getMyProfile() else profileRepository.getProfile(
            profileId ?: ""
        )


    override fun onScreenViewed() {
        setScreenNameInternal(AnalyticsManager.Screens.ViewProfile.name)
    }

    fun logViewed() {
        if (isMyProfile) {
            analyticsManager.logEventAction(AnalyticsManager.Action.ViewPersonalProfile)
        } else {
            analyticsManager.logEventAction(AnalyticsManager.Action.ViewPublicProfile)
        }
    }

    fun logEditClick() {
        analyticsManager.logEventAction(AnalyticsManager.Action.EditProfileSettings)
    }

    @AssistedFactory
    interface Factory {
        fun create(id: String): ProfileFieldsViewModel
    }
}
