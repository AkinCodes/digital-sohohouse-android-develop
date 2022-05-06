package com.sohohouse.seven.connect.noticeboard.user_reactions

import android.os.Bundle
import android.view.View
import com.sohohouse.seven.R
import com.sohohouse.seven.base.error.ErrorDialogHelper
import com.sohohouse.seven.base.mvvm.BaseMVVMBottomSheet
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.utils.collectLatest
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.common.views.showSafe
import com.sohohouse.seven.databinding.UserReactionsBottomsheetBinding
import com.sohohouse.seven.profile.view.ProfileViewerFragment

class UserReactionsBottomSheet : BaseMVVMBottomSheet<UserReactionsViewModel>() {

    override val contentLayout: Int = R.layout.user_reactions_bottomsheet
    override val viewModelClass: Class<UserReactionsViewModel> = UserReactionsViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = UserReactionsBottomsheetBinding.bind(view).listOfReactions

        val adapter = UserReactionsAdapter()
        list.adapter = adapter

        val noticeboardPostId = arguments?.getString(BundleKeys.ID)
        if (noticeboardPostId == null) {
            ErrorDialogHelper.showErrorDialogByErrorCode(requireContext())
        } else {
            viewModel.loadReactionsForPost(noticeboardPostId)
        }

        viewModel.listOfReactions.collectLatest(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.showProfile.collectLatest(viewLifecycleOwner) {
            onProfileClick(it)
        }
    }

    private fun onProfileClick(profile: ProfileItem) {
        ProfileViewerFragment.withProfile(profile)
            .showSafe(parentFragmentManager, ProfileViewerFragment.TAG)
    }

    companion object {
        const val TAG = "UserReactionsBottomSheet"
    }

}