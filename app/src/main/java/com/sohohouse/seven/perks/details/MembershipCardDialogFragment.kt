package com.sohohouse.seven.perks.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.dagger.Injectable
import com.sohohouse.seven.databinding.FragmentMembershipCardBottomSheetBinding
import com.sohohouse.seven.network.core.models.Perk
import javax.inject.Inject

class MembershipCardDialogFragment : BottomSheetDialogFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[MembershipCardViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMembershipCardBottomSheetBinding.inflate(inflater, container, false)

        with(binding) {
            button.setOnClickListener { dismiss() }

            if (arguments?.getBoolean(BundleKeys.MEMBERSHIP_CARD_SHOW_FRONT) == true) {
                membershipCard.showFront()
            } else {
                membershipCard.showBack()
            }

            viewModel.membership.observe(viewLifecycleOwner) {
                membershipCard.setMembership(it)
            }
        }

        viewModel.getMembershipInfo()
        return binding.root
    }

    companion object {
        const val TAG = "membership_card_dialog"

        fun withBenefitType(benefitType: String?): MembershipCardDialogFragment {
            return MembershipCardDialogFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(
                        BundleKeys.MEMBERSHIP_CARD_SHOW_FRONT,
                        benefitType == Perk.PERK_BEDROOM
                    )
                }
            }
        }
    }
}