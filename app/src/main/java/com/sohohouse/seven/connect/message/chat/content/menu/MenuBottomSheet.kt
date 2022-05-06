package com.sohohouse.seven.connect.message.chat.content.menu

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMBottomSheet
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.dagger.Injectable
import com.sohohouse.seven.common.views.showSafe
import com.sohohouse.seven.connect.message.chat.content.menu.block.BlockUserBottomSheet
import com.sohohouse.seven.connect.message.chat.content.menu.block.BlockUserBottomSheetViewModel
import com.sohohouse.seven.connect.message.chat.content.menu.report.ReportUserBottomSheet
import com.sohohouse.seven.databinding.BottomSheetMessagingMenuBinding
import javax.inject.Inject

class MenuBottomSheet : BaseMVVMBottomSheet<BlockUserBottomSheetViewModel>(), Injectable {

    private val recipientUserID by lazy {
        arguments?.getString(BundleKeys.ID) ?: ""
    }

    private val areMessagesEmpty by lazy {
        arguments?.getBoolean(IS_MESSAGES_EMPTY) ?: true
    }

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    @Inject
    lateinit var assistedFactory: BlockUserBottomSheetViewModel.Factory

    override val viewModel: BlockUserBottomSheetViewModel by lazy {
        assistedFactory.create(recipientUserID, areMessagesEmpty)
    }

    override val fixedHeight: Int = ViewGroup.LayoutParams.WRAP_CONTENT

    override val contentLayout: Int = R.layout.bottom_sheet_messaging_menu

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        BottomSheetMessagingMenuBinding.bind(view).initView()
    }

    private fun BottomSheetMessagingMenuBinding.initView() {

        viewModel.isCurrentMemberBlocked.observe(viewLifecycleOwner) {
            if (it) {
                blockMember.setText(R.string.messaging_menu_unblock_member)
            } else {
                blockMember.setText(R.string.messaging_menu_block_member)
            }
        }

        blockMember.setOnClickListener {
            if (viewModel.isCurrentMemberBlocked.value == true) {
                viewModel.unblockMember()
            } else {
                BlockUserBottomSheet.with(recipientUserID, areMessagesEmpty)
                    .showSafe(parentFragmentManager, BlockUserBottomSheet.TAG)

                logAnalyticsActions(AnalyticsManager.Action.MessagingUserBlock)
            }
            dismiss()
        }

        reportMember.setOnClickListener {
            ReportUserBottomSheet.with(recipientUserID, areMessagesEmpty)
                .showSafe(parentFragmentManager, ReportUserBottomSheet.TAG)

            logAnalyticsActions(AnalyticsManager.Action.MessagingUserReport)
            dismiss()
        }

        close.setOnClickListener {
            logAnalyticsActions(AnalyticsManager.Action.MessagingUserBlockCancel)
            dismiss()
        }
    }

    private fun logAnalyticsActions(action: AnalyticsManager.Action) {
        analyticsManager.logEventAction(
            action,
            Bundle().apply {
                putString(
                    AnalyticsManager.Parameters.MessagingRecipientGlobalID.value,
                    recipientUserID
                )
                putBoolean(
                    AnalyticsManager.Parameters.MessagingMessagesAreEmpty.value,
                    areMessagesEmpty
                )
            }
        )
    }

    override val viewModelClass: Class<BlockUserBottomSheetViewModel>
        get() = BlockUserBottomSheetViewModel::class.java

    companion object {
        const val TAG = "messaging_menu_bottom_sheet"
        const val IS_MESSAGES_EMPTY = "IS_MESSAGES_EMPTY"

        fun with(userID: String, isMessagesEmpty: Boolean): MenuBottomSheet {
            return MenuBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(BundleKeys.ID, userID)
                    putBoolean(IS_MESSAGES_EMPTY, isMessagesEmpty)
                }
            }
        }
    }
}
