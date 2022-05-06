package com.sohohouse.seven.profile.view.connect

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.Keep
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sohohouse.seven.R
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.extensions.focusAndShowKeyboard
import com.sohohouse.seven.common.extensions.hideKeyboard
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.databinding.FragmentConnectComposeMessageBinding

@Keep
class ComposeMessageBottomSheet : BaseConnectRequestBottomSheet() {

    private val binding by viewBinding(FragmentConnectComposeMessageBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetEditDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_connect_compose_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setupViews()
        setupViewModel()
    }

    private fun FragmentConnectComposeMessageBinding.setupViews() {
        sendRequest.setOnClickListener {
            editText.hideKeyboard()
            viewModel.sendRequest(editText.text.toString())
        }
        backArrow.setOnClickListener { dismiss() }
        cancel.setOnClickListener { dismiss() }
    }

    private fun setupViewModel() {
        viewModel.requestSent.observe(viewLifecycleOwner, { dismissWithResult(REQUEST_KEY) })
        viewModel.error.observe(viewLifecycleOwner) { showErrorDialog() }
        observeLoadingState(viewLifecycleOwner, { onLoadingStateChanged(it) })
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener { setupBottomSheet(it as BottomSheetDialog) }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.onCancelComposeMessage()
        binding.editText.hideKeyboard()
    }

    private fun setupBottomSheet(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet: View = bottomSheetDialog.findViewById(R.id.design_bottom_sheet) ?: return
        bottomSheet.layoutParams =
            bottomSheet.layoutParams.apply { height = WindowManager.LayoutParams.MATCH_PARENT }

        BottomSheetBehavior.from<View?>(bottomSheet).apply {
            this.state = BottomSheetBehavior.STATE_EXPANDED
            this.peekHeight = 0
            this.skipCollapsed = true

            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            binding.editText.focusAndShowKeyboard()
                        }
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            dismiss()
                        }
                        else -> {
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }
    }

    companion object {
        const val TAG = "compose_message_bottom_sheet"
        const val REQUEST_KEY = "connect_request_with_message"

        fun withProfile(profile: ProfileItem): ComposeMessageBottomSheet {
            return ComposeMessageBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(BundleKeys.PROFILE, profile)
                }
            }
        }
    }
}