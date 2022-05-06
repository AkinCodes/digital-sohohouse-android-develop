package com.sohohouse.seven.housepay.checkdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseBottomSheet
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.databinding.FragmentU27DiscountDetailsBinding

class Under27DiscountDetailsBottomSheet : BaseBottomSheet() {
    override val contentLayout: Int
        get() = R.layout.fragment_u27_discount_details

    private lateinit var binding: FragmentU27DiscountDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentU27DiscountDetailsBinding.inflate(
            inflater,
            container,
            false
        ).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.u27DiscountDetailsClose.clicks {
            dismiss()
        }

    }
}