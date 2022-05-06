package com.sohohouse.seven.housepay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams
import androidx.fragment.app.Fragment
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.getAttributeColor
import com.sohohouse.seven.databinding.FragmentHousePayOnboardingBinding

class FragmentHousepayOnboarding : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHousePayOnboardingBinding.inflate(inflater, container, false)
        binding.btnCancel.setOnClickListener { activity?.onBackPressed() }
        binding.mainView.setCardBackgroundColor(requireContext().theme.getAttributeColor(R.attr.colorHousePay))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setFlags(
            LayoutParams.FLAG_FULLSCREEN,
            LayoutParams.FLAG_FULLSCREEN
        )
    }

}