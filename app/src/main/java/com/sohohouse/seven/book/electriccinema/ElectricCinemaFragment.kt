package com.sohohouse.seven.book.electriccinema

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseFragment
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.Scrollable
import com.sohohouse.seven.common.apihelpers.SohoWebHelper
import com.sohohouse.seven.common.views.showSafe
import com.sohohouse.seven.common.views.webview.WebViewBottomSheetFragment
import com.sohohouse.seven.databinding.FragmentElectricCinemaBinding
import com.sohohouse.seven.main.MainNavigationController

class ElectricCinemaFragment : BaseFragment(), Scrollable {

    override val contentLayoutId get() = R.layout.fragment_electric_cinema

    private val binding by viewBinding(FragmentElectricCinemaBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.exploreCta.setOnClickListener { openElectricCinemaWebView() }
        (requireActivity() as? MainNavigationController)?.setLoadingState(LoadingState.Idle)
    }

    override fun scrollToPosition(position: Int) {
        binding.scrollView.smoothScrollTo(0, position)
    }

    private fun openElectricCinemaWebView() {
        WebViewBottomSheetFragment.withKickoutType(SohoWebHelper.KickoutType.ELECTRIC_CINEMA)
            .showSafe(requireActivity().supportFragmentManager, WebViewBottomSheetFragment.TAG)
    }
}