package com.sohohouse.seven.payment.housepay

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.FragmentAddHousePayBinding

class AddHousePayFragment : Fragment(R.layout.fragment_add_house_pay) {

    private val binding by viewBinding(FragmentAddHousePayBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addHousePayViewpager.adapter =
            AddHousePayPagerAdapter(childFragmentManager, resources)
        binding.addHousePayTablayout.setupWithViewPager(binding.addHousePayViewpager)
        binding.addHousePayToolbar.toolbarTitle.text = getString(R.string.housepay_dashboard_title)
        binding.addHousePayToolbar.toolbarBackBtn.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    class AddHousePayPagerAdapter(fm: FragmentManager, val resources: Resources) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(index: Int): Fragment {
            return when (index) {
                0 -> HousePayOffersFragment()
                1 -> HousePaymentMethodFragment()
                else -> HousePayCreditsFragment()
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> resources.getString(R.string.housepay_offers_header)
                1 -> resources.getString(R.string.housepay_payment_methods_header)
                else -> resources.getString(R.string.housepay_credits_header)
            }
        }

        override fun getCount() = 3
    }
}