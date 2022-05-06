package com.sohohouse.seven.more.profile.crop.preview

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.ErrorDialogViewModel
import com.sohohouse.seven.base.mvvm.ErrorDialogViewModelImpl
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.analytics.AnalyticsEvent
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.network.base.model.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class CropPreviewFragmentViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    private val dispatcher: CoroutineDispatcher,
    private val firebaseEventTracking: AnalyticsManager,
    private val imageCropperInteractor: ProfileImageCropperInteractor,
) : BaseViewModel(analyticsManager), Loadable.ViewModel by Loadable.ViewModelImpl(),
    ErrorDialogViewModel by ErrorDialogViewModelImpl() {

    private val _newProfileUrl = MutableStateFlow("")
    val newProfileUrl = _newProfileUrl.asStateFlow()

    fun uploadNewProfileImage(bitmap: Bitmap) {
        setLoading()
        firebaseEventTracking.logEventAction(AnalyticsManager.Action.Upload)

        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            when (val result = imageCropperInteractor.uploadImage(bitmap)) {
                is Either.Value -> {
                    analyticsManager.track(AnalyticsEvent.PhotoUpload.Success)
                    _newProfileUrl.value = result.value.imageUrl
                }
                is Either.Error -> {
                    setIdle()
                    handleError(result.error)
                    analyticsManager.track(AnalyticsEvent.PhotoUpload.Failure(result.error.toString()))
                }
            }
        }
    }

}