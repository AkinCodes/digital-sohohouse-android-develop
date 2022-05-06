package com.sohohouse.seven.connect.match

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMBottomSheet
import com.sohohouse.seven.base.mvvm.ErrorDialogViewController
import com.sohohouse.seven.common.extensions.setFragmentResult
import com.sohohouse.seven.databinding.RecommendationsOptinBottomSheetBinding

class RecommendationsOptInBottomSheet : BaseMVVMBottomSheet<RecommendationsOptInViewModel>(),
    ErrorDialogViewController {

    override val contentLayout: Int = R.layout.recommendations_optin_bottom_sheet

    override val viewModelClass: Class<RecommendationsOptInViewModel>
        get() = RecommendationsOptInViewModel::class.java

    val binding by viewBinding(RecommendationsOptinBottomSheetBinding::bind)

    override val fixedHeight = ViewGroup.LayoutParams.WRAP_CONTENT

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.optInAcceptBtn.isEnabled = binding.optInSwitch.isChecked
        binding.optInSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.optInAcceptBtn.isEnabled = isChecked
        }

        observeErrorDialogEvents()
        binding.optInAcceptBtn.setOnClickListener {
            if (binding.optInSwitch.isChecked)
                viewModel.saveProfile()
            else
                dismiss()
        }
        binding.optInDeclineBtn.setOnClickListener {
            dismiss()
        }
        viewModel.saveState.observe(viewLifecycleOwner) {
            if (it) {
                setFragmentResult(REQUEST_KAY_OPTED_IN)
                dismiss()
            }
        }
    }

    companion object {
        const val TAG = "DecideOptInBottomSheet"
        const val REQUEST_KAY_OPTED_IN = "DecideOptInBottomSheet_REQUEST_KAY"

        fun newInstance(): RecommendationsOptInBottomSheet {
            return RecommendationsOptInBottomSheet()
        }
    }
}