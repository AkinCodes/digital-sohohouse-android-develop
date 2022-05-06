package com.sohohouse.seven.branding

import android.content.Intent
import android.os.Bundle
import androidx.annotation.Keep
import androidx.lifecycle.lifecycleScope
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.branding.AppIconViewModel.Companion.APP_ICON_CHANGE_REQUEST
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.asEnumOrDefault
import com.sohohouse.seven.common.user.IconType
import com.sohohouse.seven.common.user.AppManager
import javax.inject.Inject

@Keep
class AppIconFragment : BaseMVVMFragment<AppIconViewModel>() {

    @Inject
    lateinit var appManager: AppManager

    override val viewModelClass: Class<AppIconViewModel>
        get() = AppIconViewModel::class.java

    override val contentLayoutId: Int
        get() = R.layout.activity_splash

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            showAppIconChangeDialog()
        }
        viewModel.setScreenName(name= AnalyticsManager.Screens.AppIcon.name)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            APP_ICON_CHANGE_REQUEST -> {
                val iconType =
                    data?.getStringExtra(BundleKeys.ICON_TYPE)?.asEnumOrDefault(IconType.DEFAULT)
                        ?: IconType.DEFAULT
                viewModel.updateAppIcon(requireContext(), iconType)
                requireActivity().finish()
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun showAppIconChangeDialog() {
        if (parentFragmentManager.findFragmentByTag(AppIconChangeDialog.TAG) != null) return

        AppIconChangeDialog().also {
            it.arguments = Bundle().apply {
                putString(BundleKeys.ICON_TYPE, appManager.iconType.name)
            }
            it.setTargetFragment(this, APP_ICON_CHANGE_REQUEST)
        }.show(parentFragmentManager, AppIconChangeDialog.TAG)
    }

}