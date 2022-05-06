package com.sohohouse.seven.apponboarding.houseboardpost

import android.os.Bundle
import android.view.View
import com.sohohouse.seven.App
import com.sohohouse.seven.R
import com.sohohouse.seven.apponboarding.AppOnboardScreen
import com.sohohouse.seven.apponboarding.AppOnboardingActivity
import com.sohohouse.seven.base.BaseFragment
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.databinding.FragmentOnboardingHouseBoardPostBinding
import com.sohohouse.seven.houseboard.list.viewholders.HouseBoardPostNoteViewHolder
import com.sohohouse.seven.houseboard.list.viewholders.bindOnboardingData

class OnboardingHouseBoardPostFragment : BaseFragment() {

    override val contentLayoutId get() = R.layout.fragment_onboarding_house_board_post

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.appComponent.analyticsManager.setScreenName(
            requireActivity().localClassName,
            AnalyticsManager.Screens.OnboardHouseBoardPost.name
        )

        with(FragmentOnboardingHouseBoardPostBinding.bind(view)) {
            onboardingHouseCard.setup(
                R.string.app_onboarding_posting_header,
                R.string.app_onboarding_posting_supporting
            )
            onboardingHouseAcceptButton.clicks {
                val activity = activity as AppOnboardingActivity
                activity.navigateToNext(AppOnboardScreen.HOUSE_BOARD_POST)
            }

            houseboardPostNote.profileImage.setImageResource(R.drawable.ic_avatar_kimberly_aberman)

            HouseBoardPostNoteViewHolder(houseboardPostNote).apply {
                bindOnboardingData(R.drawable.ic_avatar_kimberly_aberman)
            }

            houseboardPostNote.postANoteButton.apply {
                isEnabled = false
                isClickable = false
            }

            componentYourPostInclude.houseBoardPostEditText.apply {
                isFocusable = false
                isEnabled = false
            }
        }
    }
}