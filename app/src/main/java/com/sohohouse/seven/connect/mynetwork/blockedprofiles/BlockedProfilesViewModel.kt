package com.sohohouse.seven.connect.mynetwork.blockedprofiles

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.sohohouse.seven.R
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.connect.mynetwork.ConnectionRepository
import com.sohohouse.seven.network.core.models.Profile
import com.sohohouse.seven.profile.ProfileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

class BlockedProfilesViewModel @Inject constructor(
    private val connectionRepo: ConnectionRepository,
    private val profileRepo: ProfileRepository,
    analyticsManager: AnalyticsManager,
    private val ioDispatcher: CoroutineDispatcher,
) : BaseViewModel(analyticsManager),
    Loadable.ViewModel by Loadable.ViewModelImpl() {

    private val dataSourceFactory = BlockedProfileDataSource.Factory(
        this,
        loadable = this,
    )

    val blockedContacts: LiveData<PagedList<DiffItem>> = LivePagedListBuilder(
        object : DataSource.Factory<Int, DiffItem>() {
            override fun create(): DataSource<Int, DiffItem> = dataSourceFactory.create()
        },
        PagedList.Config.Builder().setEnablePlaceholders(false)
            .setPageSize(ConnectionRepository.ITEMS_PER_PAGE).build()
    ).build()

    private val _errorState = MutableSharedFlow<Int>()
    val errorState = _errorState.asSharedFlow()

    private fun refresh() {
        dataSourceFactory.invalidate()
    }

    fun getBlockedProfiles(
        callback: PageKeyedDataSource.LoadInitialCallback<Int, DiffItem>
    ) {
        setLoadingState(LoadingState.Loading)
        val profileData = mutableListOf<BlockedProfile>()
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            if (connectionRepo.blockedMembers.value.isEmpty()) connectionRepo.getBlockedMembers()
            supervisorScope {
                connectionRepo.blockedMembers.value.forEach { profileId ->
                    launch {
                        profileRepo.getProfile(profileId).ifValue {
                            addProfileToList(profileData, it, profileId)
                        }
                    }
                }
            }
        }.invokeOnCompletion {
            checkOnError(it)
            callback.onResult(profileData as List<DiffItem>, null, null)
            setLoadingState(LoadingState.Idle)
        }
    }

    private fun checkOnError(throwable: Throwable?) {
        throwable?.let {
            viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
                _errorState.emit(R.string.error_general)
            }
        }
    }

    private fun addProfileToList(
        profileData: MutableList<BlockedProfile>,
        profile: Profile,
        profileId: String
    ) {
        synchronized(profileData) {
            profileData.add(
                BlockedProfile(
                    profile.id,
                    profileId,
                    profile.firstName,
                    profile.lastName,
                    profile.firstName + " " + profile.lastName,
                    profile.occupation ?: "",
                    profile.imageUrl
                )
            )
        }
    }

    fun unblockContact(item: BlockedProfile) {
        setLoadingState(LoadingState.Loading)
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            connectionRepo.patchUnblockMember(item.profileId)
        }.invokeOnCompletion {
            setLoadingState(LoadingState.Idle)
            checkOnError(it)
            it ?: refresh()
        }
    }

}