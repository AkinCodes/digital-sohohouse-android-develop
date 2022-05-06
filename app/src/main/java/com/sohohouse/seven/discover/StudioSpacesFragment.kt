package com.sohohouse.seven.discover

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseFragment
import com.sohohouse.seven.common.Scrollable
import com.sohohouse.seven.common.apihelpers.SohoWebHelper
import com.sohohouse.seven.common.views.webview.WebViewBottomSheetFragment
import com.sohohouse.seven.databinding.FragmentStudioSpacesBinding

class StudioSpacesFragment : BaseFragment(), Scrollable {

    val binding by viewBinding(FragmentStudioSpacesBinding::bind)

    override val contentLayoutId = R.layout.fragment_studio_spaces

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.exploreStudioSpacesCta.setOnClickListener { openStudioSpacesWebview() }
    }

    override fun scrollToPosition(position: Int) {
        binding.scrollView.smoothScrollTo(0, position)
    }

    private fun openStudioSpacesWebview() {
        WebViewBottomSheetFragment.withKickoutType(type = SohoWebHelper.KickoutType.STUDIO_SPACES)
            .show(requireActivity().supportFragmentManager, WebViewBottomSheetFragment.TAG)
    }

    companion object {
        internal const val TAG = "discover_studio_spaces"
    }
}