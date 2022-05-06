package com.sohohouse.seven.common.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.databinding.FragmentFullScreenPromptBinding

class FullScreenPromptFragment : Fragment() {

    companion object {

        private const val HEADER_TEXT = "FullScreenPromptFragment.HeaderText"
        private const val SUPPORTING_TEXT = "FullScreenPromptFragment.SupportingText"
        private const val PRIMARY_BUTTON_TEXT = "FullScreenPromptFragment.PrimaryButtonText"
        private const val SECONDARY_BUTTON_TEXT = "FullScreenPromptFragment.SecondaryButtonText"
        private const val SECONDARY_BUTTON_VISIBILITY =
            "FullScreenPromptFragment.SecondaryButtonVisibility"

        fun createInstance(
            headerText: String, supportingText: String, primaryBtnText: String,
            secondaryBtnText: String? = null, secondaryBtnVisibility: Boolean = false
        ): FullScreenPromptFragment {
            val fragment = FullScreenPromptFragment()
            val args = Bundle()
            args.putString(HEADER_TEXT, headerText)
            args.putString(SUPPORTING_TEXT, supportingText)
            args.putString(PRIMARY_BUTTON_TEXT, primaryBtnText)
            secondaryBtnText?.let { args.putString(SECONDARY_BUTTON_TEXT, secondaryBtnText) }
            args.putBoolean(SECONDARY_BUTTON_VISIBILITY, secondaryBtnVisibility)
            fragment.arguments = args
            return fragment
        }
    }

    interface ButtonListener {
        fun onStatusPrimaryButtonClicked()
        fun onStatusSecondaryButtonClicked()
    }

    private val buttonListener: ButtonListener?
        get() {
            return try {
                requireActivity().takeIf { it is ButtonListener }?.let { it as ButtonListener }
            } catch (e: Exception) {
                null
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentFullScreenPromptBinding
            .inflate(layoutInflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpLayout(FragmentFullScreenPromptBinding.bind(view))
    }

    private fun setUpLayout(binding: FragmentFullScreenPromptBinding) = with(binding) {
        fullScreenPromptHeader.text = arguments?.getString(HEADER_TEXT)
        fullScreenPromptSupporting.text = arguments?.getString(SUPPORTING_TEXT)
        fullScreenPromptButton.text = arguments?.getString(PRIMARY_BUTTON_TEXT)
        fullScreenPromptButton.clicks { buttonListener?.onStatusPrimaryButtonClicked() }
        if (arguments?.getBoolean(SECONDARY_BUTTON_VISIBILITY) == true) {
            fullScreenPromptSecondaryButton.setVisible()
            fullScreenPromptSecondaryButton.text = arguments?.getString(SECONDARY_BUTTON_TEXT)
            fullScreenPromptSecondaryButton.clicks { buttonListener?.onStatusSecondaryButtonClicked() }
        }
    }
}
