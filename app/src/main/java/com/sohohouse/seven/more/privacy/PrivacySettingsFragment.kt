package com.sohohouse.seven.more.privacy

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.base.mvvm.ErrorDialogViewController
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.databinding.PrivacySettingsFragmentBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PrivacySettingsFragment : BaseMVVMFragment<PrivacySettingsViewModel>(),
    Loadable.View,
    ErrorDialogViewController {

    override val viewModelClass: Class<PrivacySettingsViewModel>
        get() = PrivacySettingsViewModel::class.java

    override val contentLayoutId: Int
        get() {
            return R.layout.privacy_settings_fragment
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val boundView = PrivacySettingsFragmentBinding.bind(view)
        boundView.setUpView()

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isOptedIn.collect {
                        boundView.optInSwitch.isChecked = it
                    }
                }
            }
        }

        observeLoadingState(viewLifecycleOwner) {
            boundView.loadingView.toggleSpinner(it is LoadingState.Loading)
        }
        observeErrorDialogEvents()
        viewModel.setScreenName(name = AnalyticsManager.Screens.PrivacyPolicies.name)
    }

    private fun PrivacySettingsFragmentBinding.setUpView() {
        optInSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.optIn(isChecked)
        }
    }
}