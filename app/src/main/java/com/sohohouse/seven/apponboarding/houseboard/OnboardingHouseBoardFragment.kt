package com.sohohouse.seven.apponboarding.houseboard

import android.os.Bundle
import android.view.View
import com.sohohouse.seven.App
import com.sohohouse.seven.R
import com.sohohouse.seven.apponboarding.AppOnboardScreen
import com.sohohouse.seven.apponboarding.AppOnboardingActivity
import com.sohohouse.seven.base.BaseFragment
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.databinding.FragmentOnboardingHouseBoardBinding
import com.sohohouse.seven.houseboard.list.viewholders.HouseBoardCheckInViewHolder
import com.sohohouse.seven.houseboard.list.viewholders.bindOnboardingData

class OnboardingHouseBoardFragment : BaseFragment() {

    override val contentLayoutId = R.layout.fragment_onboarding_house_board

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.appComponent.analyticsManager.setScreenName(requireActivity().localClassName, screen = AnalyticsManager.Screens.OnboardNoticeboard.name)

        with(FragmentOnboardingHouseBoardBinding.bind(view)) {
            onboardingHouseCard.setup(
                R.string.app_onboarding_house_board_header,
                R.string.app_onboarding_house_board_supporting
            )
            onboardingHouseAcceptButton.clicks {
                val activity = activity as AppOnboardingActivity
                activity.navigateToNext(AppOnboardScreen.HOUSE_BOARD)
            }

            HouseBoardCheckInViewHolder(houseBoardPost1).apply {
                bindOnboardingData(
                    imgRes = R.drawable.ic_avatar_fay_estes,
                    nameLabel = R.string.onboarding_house_board_fay_estes,
                    timeLabel = R.string.connect_board_post_now_label,
                    content = R.string.onboarding_house_board_post_1
                )
            }
            HouseBoardCheckInViewHolder(houseBoardPost2).apply {
                bindOnboardingData(
                    imgRes = R.drawable.ic_avatar_kobi_dawson,
                    nameLabel = R.string.onboarding_house_board_kobi_dawson,
                    timeLabel = R.string.onboarding_house_board_post_timestamp_2_min,
                    content = R.string.onboarding_house_board_post_2
                )
            }
            HouseBoardCheckInViewHolder(houseBoardPost3).apply {
                bindOnboardingData(
                    imgRes = R.drawable.ic_avatar_kimberly_aberman,
                    nameLabel = R.string.onboarding_house_board_kimberly_aberman,
                    timeLabel = R.string.onboarding_house_board_post_timestamp_2_hour,
                    content = R.string.onboarding_house_board_post_3
                )
            }
        }
    }
}