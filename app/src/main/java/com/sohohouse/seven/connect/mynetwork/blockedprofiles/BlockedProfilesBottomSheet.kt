package com.sohohouse.seven.connect.mynetwork.blockedprofiles

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMBottomSheet
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.dagger.Injectable
import com.sohohouse.seven.databinding.FragmentBlockedMembersDialogBinding

class BlockedProfilesBottomSheet : BaseMVVMBottomSheet<BlockedProfileBottomSheetViewModel>(),
    Injectable {

    override val viewModelClass: Class<BlockedProfileBottomSheetViewModel>
        get() = BlockedProfileBottomSheetViewModel::class.java

    override val contentLayout: Int = R.layout.fragment_blocked_members_dialog

    override val fixedHeight: Int = ViewGroup.LayoutParams.WRAP_CONTENT

    val binding by viewBinding(FragmentBlockedMembersDialogBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setupViews()
    }

    private fun FragmentBlockedMembersDialogBinding.setupViews() {
        blockedContacts.setOnClickListener {
            viewModel.logAnalyticsActions(AnalyticsManager.Action.ConnectConnectionsMenuBlockedOpen)
            startActivity(Intent(requireContext(), BlockedProfilesActivity::class.java))
            dismiss()
        }

        close.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val TAG = "blocked_members_bottom_dialog"
    }

}