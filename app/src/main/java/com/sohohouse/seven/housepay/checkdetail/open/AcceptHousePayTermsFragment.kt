package com.sohohouse.seven.housepay.checkdetail.open

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setFragmentResult
import com.sohohouse.seven.common.views.dialog.CustomDialogBuilder
import com.sohohouse.seven.databinding.FragmentAcceptHousePayTermsDialogBinding
import com.sohohouse.seven.housepay.terms.HousePayTermActivity

class AcceptHousePayTermsFragment : AppCompatDialogFragment() {

    class Builder : CustomDialogBuilder<AcceptHousePayTermsFragment>() {
        override fun newInstance(): AcceptHousePayTermsFragment {
            return AcceptHousePayTermsFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentAcceptHousePayTermsDialogBinding.inflate(
        layoutInflater, container, false
    ).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAcceptHousePayTermsDialogBinding.bind(view)
        val termsMessageStr = getString(R.string.housepay_accept_terms_and_conditions_message)
        val spannableString = SpannableString(termsMessageStr)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                gotoHousePayTerms()
            }
        }

        spannableString.setSpan(
            clickableSpan,
            53,
            spannableString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        with(binding) {
            housePayTermsMessage.text = spannableString
            housePayTermsMessage.movementMethod = LinkMovementMethod.getInstance()
            housePayTermsSwitch.setOnClickListener {
                housePayTermsContinue.isEnabled = housePayTermsSwitch.isChecked
            }
            housePayTermsContinue.clicks {
                setFragmentResult(CustomDialogBuilder.REQ_KEY_POSITIVE_BTN_CLICK)
                dismiss()
            }
            housePayTermsCancel.clicks {
                setFragmentResult(CustomDialogBuilder.REQ_KEY_NEGATIVE_BTN_CLICK)
                dismiss()
            }
        }
    }

    private fun gotoHousePayTerms() {
        val args = bundleOf(HousePayTermActivity.KEY_IS_ONBOARDING to false)
        startActivity(Intent(context, HousePayTermActivity::class.java).putExtras(args))
        dismiss()
    }
}
