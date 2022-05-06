package com.sohohouse.seven.connect.message.chat.content.menu.report

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.base.ErrorDialogFragment
import com.sohohouse.seven.base.mvvm.BaseMVVMBottomSheet
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.dagger.Injectable
import com.sohohouse.seven.common.extensions.onTextChanged
import com.sohohouse.seven.common.views.showSafe
import com.sohohouse.seven.connect.message.chat.content.menu.block.BlockUserBottomSheet
import com.sohohouse.seven.databinding.ReportUserBottomSheetBinding
import javax.inject.Inject

class ReportUserBottomSheet : BaseMVVMBottomSheet<ReportUserBottomSheetViewModel>(), Injectable {

    private val recipientUserID by lazy {
        arguments?.getString(BundleKeys.ID) ?: ""
    }

    private val areMessagesEmpty by lazy {
        arguments?.getBoolean(BlockUserBottomSheet.IS_MESSAGES_EMPTY) ?: true
    }

    @Inject
    lateinit var assistedFactory: ReportUserBottomSheetViewModel.Factory

    override val viewModel: ReportUserBottomSheetViewModel by lazy {
        assistedFactory.create(recipientUserID, areMessagesEmpty)
    }

    override val fixedHeight: Int = ViewGroup.LayoutParams.WRAP_CONTENT

    override val contentLayout: Int = R.layout.report_user_bottom_sheet

    override val viewModelClass: Class<ReportUserBottomSheetViewModel> =
        ReportUserBottomSheetViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ReportUserBottomSheetBinding.bind(view).initView()
    }

    private fun ReportUserBottomSheetBinding.initView() {
        reportMemberCancel.setOnClickListener {
            viewModel.logAnalyticsActions(AnalyticsManager.Action.MessagingUserReportBack)
            dismiss()
        }

        viewModel.loadingState.observe(viewLifecycleOwner) {
            messagingReport.isEnabled = it == LoadingState.Idle
        }

        messagingReport.setOnClickListener {
            viewModel.reportUser(reportMessage.text.toString())
        }

        viewModel.error.observe(viewLifecycleOwner) { showErrorDialog() }

        reportMessage.onTextChanged { text, count ->
            messagingReport.isEnabled = text?.isNotEmpty() == true
        }

        viewModel.goToFinish.observe(viewLifecycleOwner) {
            ReportUserFinishBottomSheet()
                .showSafe(parentFragmentManager, ReportUserFinishBottomSheet.TAG)
            dismiss()
        }
    }

    private fun showErrorDialog() {
        ErrorDialogFragment().showSafe(childFragmentManager, ErrorDialogFragment.TAG)
    }


    companion object {
        const val TAG = "report_user_bottom_sheet"
        const val IS_MESSAGES_EMPTY = "IS_MESSAGES_EMPTY"

        fun with(userID: String, areMessagesEmpty: Boolean): ReportUserBottomSheet {
            return ReportUserBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(BundleKeys.ID, userID)
                    putBoolean(IS_MESSAGES_EMPTY, areMessagesEmpty)
                }
            }
        }
    }
}


