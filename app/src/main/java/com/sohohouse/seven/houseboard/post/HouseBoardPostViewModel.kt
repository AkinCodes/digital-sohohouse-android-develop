package com.sohohouse.seven.houseboard.post

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.common.analytics.AnalyticsEvent
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.isToday
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.ZipRequestsUtil
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.core.request.DeleteRollCallRequest
import com.sohohouse.seven.network.core.request.GetNoticeboardPostsRequest
import com.sohohouse.seven.network.core.request.PostRollCallRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class HouseBoardPostViewModel @Inject constructor(
    private val zipRequestsUtil: ZipRequestsUtil,
    private val userManager: UserManager,
    analyticsManager: AnalyticsManager,
    dispatcher: CoroutineDispatcher
) : BaseViewModel(analyticsManager, dispatcher),
    Loadable.ViewModel by Loadable.ViewModelImpl(),
    Errorable.ViewModel by Errorable.ViewModelImpl() {

    var postId: String? = null
    lateinit var venueId: String
    private lateinit var postText: String

    private val _userData = MutableLiveData<HouseBoardUserData>()
    val userData: LiveData<HouseBoardUserData>
        get() = _userData

    private val _post = MutableLiveData<String>()
    val post: LiveData<String>
        get() = _post

    private val _postSuccess = LiveEvent<Any>()
    val postSuccess: LiveEvent<Any>
        get() = _postSuccess

    private val _errorDialog = LiveEvent<Int>()
    val errorDialog: LiveEvent<Int>
        get() = _errorDialog

    override fun onScreenViewed() {
        setScreenNameInternal(AnalyticsManager.Screens.HouseBoardPost.name)
    }

    fun fetchPostInfo() {
        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)

            _userData.postValue(HouseBoardUserData(userManager))
            zipRequestsUtil.issueApiCall(GetNoticeboardPostsRequest(profileId = userManager.profileID))
                .fold(
                    ifError = {
                        Timber.d(it.toString())
                    },
                    ifValue = { list ->
                        list.filter { it.createdAt?.isToday() == true }
                            .let { posts ->
                                postId = posts.firstOrNull()?.id
                                posts.firstOrNull {
                                    it.venueResource.get()?.firstOrNull()?.id == venueId
                                }
                                    ?.let { _post.postValue(it.status) }
                            }
                    },
                    ifEmpty = {}
                )
            setLoadingState(LoadingState.Idle)
        }
    }

    @SuppressLint("CheckResult")
    fun submitPost(post: CharSequence) {
        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)

            postId?.let {
                AnalyticsEvent.HouseBoard.UpdateNote(venueId)
                analyticsManager.logEventAction(AnalyticsManager.Action.NoticeboardPost)
                zipRequestsUtil.issueApiCall(DeleteRollCallRequest(it))
            } ?: let {
                analyticsManager.track(AnalyticsEvent.HouseBoard.PostNote(venueId))
            }

            zipRequestsUtil.issueApiCall(PostRollCallRequest(post.toString().trim(), venueId)).fold(
                ifError = {
                    _errorDialog.postValue(
                        when (it) {
                            ServerError.TIMEOUT,
                            ServerError.NO_INTERNET,
                            ServerError.INVALID_RESPONSE -> ERROR_DIALOG_NETWORK
                            ServerError.BAD_REQUEST -> ERROR_DIALOG_REJECTED
                            else -> ERROR_DIALOG_GENERIC
                        }
                    )
                },
                ifValue = { _postSuccess.postEvent() },
                ifEmpty = {}
            )

            setLoadingState(LoadingState.Idle)
        }
    }

    @SuppressLint("CheckResult")
    fun deletePost() {
        viewModelScope.launch(viewModelContext) {
            setLoadingState(LoadingState.Loading)

            analyticsManager.logEventAction(AnalyticsManager.Action.NoticeboadDeletePost)
            postId?.let { id ->
                zipRequestsUtil.issueApiCall(DeleteRollCallRequest(id)).fold(
                    ifError = { Timber.d(it.toString()) },
                    ifValue = { },
                    ifEmpty = { _postSuccess.postEvent() }
                )
            }

            setLoadingState(LoadingState.Idle)
        }
    }

    fun shouldEnableSubmitButton(text: CharSequence): Boolean {
        val trimmedString = text.toString().trim()
        return trimmedString.isNotBlank() && (!::postText.isInitialized || trimmedString.contentEquals(
            postText
        ))
    }

    fun isUserProfileImageSet(): Boolean {
        return userManager.profileImageURL.isNotEmpty()
    }

    fun onProfileImageEditClicked() {
        analyticsManager.track(AnalyticsEvent.More.EditPhoto)
    }

    fun updateProfileImage(imageURL: String) {
        userManager.updateInfo(userManager.profileFirstName, userManager.profileLastName, imageURL)
        _userData.postValue(
            HouseBoardUserData(
                userManager.profileFirstName,
                userManager.profileLastName,
                imageURL
            )
        )
    }

    companion object {
        const val ERROR_DIALOG_REJECTED = 0
        const val ERROR_DIALOG_NETWORK = 1
        const val ERROR_DIALOG_GENERIC = 2
    }

    data class HouseBoardUserData(
        val firstName: String,
        val lastName: String,
        val imageUrl: String
    ) {
        constructor(userManager: UserManager) : this(
            userManager.profileFirstName,
            userManager.profileLastName,
            userManager.profileImageURL
        )
    }

}