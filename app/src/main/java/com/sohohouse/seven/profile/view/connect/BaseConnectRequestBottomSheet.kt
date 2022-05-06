package com.sohohouse.seven.profile.view.connect

import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sohohouse.seven.base.ErrorDialogFragment
import com.sohohouse.seven.base.LoadingDialogFragment
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.base.mvvm.fragmentViewModel
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.dagger.Injectable
import com.sohohouse.seven.common.extensions.setFragmentResult
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.common.views.showSafe
import javax.inject.Inject

abstract class BaseConnectRequestBottomSheet : BottomSheetDialogFragment(),
    Injectable,
    Loadable.View,
    Errorable.View {

    @Inject
    lateinit var assistedFactory: ConnectRequestViewModel.Factory

    override val viewModel: ConnectRequestViewModel by fragmentViewModel {
        assistedFactory.create(
            profile.id
        )
    }

    protected val profile: ProfileItem by lazy {
        arguments?.getParcelable(BundleKeys.PROFILE) ?: ProfileItem()
    }

    protected fun sendRequest() {
        viewModel.sendRequest()
    }

    protected fun dismissWithResult(requestCode: String) {
        setFragmentResult(requestCode)
        dismiss()
    }

    protected fun showErrorDialog() {
        ErrorDialogFragment().showSafe(childFragmentManager, ErrorDialogFragment.TAG)
    }

    protected fun onLoadingStateChanged(state: LoadingState) {
        when (state) {
            LoadingState.Loading -> {
                LoadingDialogFragment().showSafe(childFragmentManager, LoadingDialogFragment.TAG)
            }
            LoadingState.Idle -> {
                val dialog = childFragmentManager.findFragmentByTag(LoadingDialogFragment.TAG)
                (dialog as? DialogFragment)?.dismiss()
            }
        }
    }
}