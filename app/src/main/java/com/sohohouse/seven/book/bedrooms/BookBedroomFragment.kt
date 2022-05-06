package com.sohohouse.seven.book.bedrooms

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
import com.sohohouse.seven.databinding.FragmentBookBedroomBinding
import com.sohohouse.seven.main.MainNavigationController

class BookBedroomFragment : BaseFragment(), Scrollable {

    private val binding by viewBinding(FragmentBookBedroomBinding::bind)

    override val contentLayoutId get() = R.layout.fragment_book_bedroom


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.browseCta.setOnClickListener { openBookBedroomWebView() }
        (requireActivity() as? MainNavigationController)?.setLoadingState(LoadingState.Idle)
    }

    override fun scrollToPosition(position: Int) {
        binding.scrollView.smoothScrollTo(0, position)
    }

    private fun openBookBedroomWebView() {
        WebViewBottomSheetFragment.withKickoutType(SohoWebHelper.KickoutType.BOOK_HOTEL)
            .showSafe(requireActivity().supportFragmentManager, WebViewBottomSheetFragment.TAG)
    }
}