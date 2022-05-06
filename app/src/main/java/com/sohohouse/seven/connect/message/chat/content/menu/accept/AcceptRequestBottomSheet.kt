package com.sohohouse.seven.connect.message.chat.content.menu.accept

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMBottomSheet
import com.sohohouse.seven.common.dagger.Injectable
import com.sohohouse.seven.common.views.CustomDialogFactory
import com.sohohouse.seven.databinding.AcceptUserBottomSheetBinding

class AcceptRequestBottomSheet : BaseMVVMBottomSheet<AcceptRequestBottomSheetViewModel>(),
    Injectable {

    override val contentLayout: Int = R.layout.accept_user_bottom_sheet

    override val viewModelClass: Class<AcceptRequestBottomSheetViewModel>
        get() = AcceptRequestBottomSheetViewModel::class.java

    override val fixedHeight: Int = ViewGroup.LayoutParams.WRAP_CONTENT

    private val inviterName by lazy {
        arguments?.getString(INVITER) ?: ""
    }
    private val channelUrl by lazy {
        arguments?.getString(CHANNEL_URL) ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AcceptUserBottomSheetBinding.bind(view).initView()

        viewModel.dismiss.observe(viewLifecycleOwner) {
            dismiss()
        }
        viewModel.goBack.observe(viewLifecycleOwner) {
            activity?.onBackPressed()
        }
    }

    private fun AcceptUserBottomSheetBinding.initView() {

        acceptBottomSheetMessage.text = getString(R.string.message_request_dialog_body, inviterName)

        messageRequestAcceptBtn.setOnClickListener {
            viewModel.acceptInvitation(channelUrl)
        }
        messageRequestDeclineBtn.setOnClickListener {
            showDeclineConfirmationDialog(inviterName, channelUrl)
        }
    }

    private fun showDeclineConfirmationDialog(inviter: String, channelUrl: String) {
        CustomDialogFactory.createThemedAlertDialog(
            context = requireContext(),
            title = getString(R.string.decline_message_dialog_title),
            message = getString(R.string.decline_message_dialog_text, inviter),
            positiveClickListener = { _, _ ->
                viewModel.declineInvitation(channelUrl)
            },
            positiveButtonText = getString(R.string.message_request_decline),
            negativeButtonText = getString(R.string.messaging_cancel),
        ).show()
    }

    companion object {
        const val TAG = "accept_messaging_request_bottom_sheet"
        const val INVITER = "inviter"
        const val CHANNEL_URL = "channel_url"

        fun with(inviterName: String, channelUrl: String): AcceptRequestBottomSheet {
            return AcceptRequestBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(INVITER, inviterName)
                    putString(CHANNEL_URL, channelUrl)
                }
                isCancelable = false
            }
        }
    }
}