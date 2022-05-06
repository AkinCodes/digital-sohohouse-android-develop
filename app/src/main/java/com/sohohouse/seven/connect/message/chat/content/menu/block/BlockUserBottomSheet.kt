package com.sohohouse.seven.connect.message.chat.content.menu.block

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.base.ErrorDialogFragment
import com.sohohouse.seven.base.mvvm.BaseMVVMBottomSheet
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.dagger.Injectable
import com.sohohouse.seven.common.views.showSafe
import com.sohohouse.seven.databinding.BlockUserBottomSheetBinding
import javax.inject.Inject

class BlockUserBottomSheet : BaseMVVMBottomSheet<BlockUserBottomSheetViewModel>(), Injectable {

    private val userID by lazy {
        arguments?.getString(BundleKeys.ID) ?: ""
    }

    private val areMessagesEmpty by lazy {
        arguments?.getBoolean(IS_MESSAGES_EMPTY) ?: true
    }

    @Inject
    lateinit var assistedFactory: BlockUserBottomSheetViewModel.Factory

    override val viewModel: BlockUserBottomSheetViewModel by lazy {
        assistedFactory.create(userID, areMessagesEmpty)
    }

    override val fixedHeight: Int = ViewGroup.LayoutParams.WRAP_CONTENT

    override val contentLayout: Int = R.layout.block_user_bottom_sheet

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        BlockUserBottomSheetBinding.bind(view).initView()

        viewModel.dismiss.observe(viewLifecycleOwner) {
            dismiss()
        }
    }

    private fun BlockUserBottomSheetBinding.initView() {

        viewModel.isCurrentMemberBlocked.observe(viewLifecycleOwner) {
            if (it) {
                messagingBlock.setText(R.string.messaging_menu_unblock)
            } else {
                messagingBlock.setText(R.string.messaging_menu_block)
            }
        }

        messagingBlock.setOnClickListener {
            if (viewModel.isCurrentMemberBlocked.value == true) {
                viewModel.unblockMember()
            } else {
                viewModel.blockMember()
            }
        }

        blockMemberCancel.setOnClickListener {
            dismiss()
        }

        viewModel.error.observe(viewLifecycleOwner) {
            ErrorDialogFragment().showSafe(childFragmentManager, ErrorDialogFragment.TAG)
        }
    }

    companion object {
        const val TAG = "block_user_fragment"
        const val IS_MESSAGES_EMPTY = "IS_MESSAGES_EMPTY"

        fun with(userID: String, areMessagesEmpty: Boolean): BlockUserBottomSheet {
            return BlockUserBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(BundleKeys.ID, userID)
                    putBoolean(IS_MESSAGES_EMPTY, areMessagesEmpty)
                }
            }
        }
    }

    override val viewModelClass: Class<BlockUserBottomSheetViewModel>
        get() = BlockUserBottomSheetViewModel::class.java
}

