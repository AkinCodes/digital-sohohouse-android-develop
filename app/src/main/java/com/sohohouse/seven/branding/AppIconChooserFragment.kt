package com.sohohouse.seven.branding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.branding.AppIconViewModel.Companion.APP_ICON_CHANGE_REQUEST
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.extensions.asEnumOrDefault
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.user.AppManager
import com.sohohouse.seven.common.user.IconType
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.views.showSafe
import com.sohohouse.seven.databinding.AppIconChooserFragmentBinding
import javax.inject.Inject

class AppIconChooserFragment : BaseMVVMFragment<AppIconViewModel>() {

    override val contentLayoutId get() = R.layout.app_icon_chooser_fragment

    override val viewModelClass: Class<AppIconViewModel>
        get() = AppIconViewModel::class.java

    @Inject
    lateinit var appManager: AppManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = AppIconChooserFragmentBinding.bind(view)
        binding.setupViews()
    }

    private fun AppIconChooserFragmentBinding.setupViews() {
        val currentIconType = appManager.iconType
        mainIcon.clicks { showAppIconChangeDialog(IconType.DEFAULT) }
        mainIconCardView.isSelected = currentIconType == IconType.DEFAULT

        if (appManager.userManager.subscriptionType == SubscriptionType.FRIENDS) {
            setupIcon(
                iconV1,
                R.mipmap.ic_launcher_friends,
                IconType.FRIENDS,
                iconV1CardView,
                currentIconType
            )
            setupIcon(
                iconV2,
                R.mipmap.ic_launcher_friends_v1,
                IconType.FRIENDS_V1,
                iconV2CardView,
                currentIconType
            )
        } else {
            setupIcon(
                iconV1,
                R.mipmap.ic_launcher_v1,
                IconType.DEFAULT_V1,
                iconV1CardView,
                currentIconType
            )
            setupIcon(
                iconV2,
                R.mipmap.ic_launcher_v2,
                IconType.DEFAULT_V2,
                iconV2CardView,
                currentIconType
            )
        }
    }

    private fun setupIcon(
        imageView: ImageView,
        @DrawableRes iconResId: Int,
        iconType: IconType,
        cardView: CardView,
        currentIconType: IconType,
    ) {
        imageView.apply {
            setImageResource(iconResId)
            clicks { showAppIconChangeDialog(iconType) }
        }
        cardView.isSelected = iconType == currentIconType
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            APP_ICON_CHANGE_REQUEST -> {
                val iconType =
                    data?.getStringExtra(BundleKeys.ICON_TYPE)?.asEnumOrDefault(IconType.DEFAULT)
                        ?: IconType.DEFAULT
                viewModel.updateAppIcon(requireContext(), iconType)
                requireActivity().finishAffinity()
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun showAppIconChangeDialog(iconType: IconType) {
        AppIconChangeDialog().also {
            it.arguments = Bundle().apply {
                putString(BundleKeys.ICON_TYPE, iconType.name)
            }
            it.setTargetFragment(this, APP_ICON_CHANGE_REQUEST)
        }.showSafe(parentFragmentManager, AppIconChangeDialog.TAG)
    }

}