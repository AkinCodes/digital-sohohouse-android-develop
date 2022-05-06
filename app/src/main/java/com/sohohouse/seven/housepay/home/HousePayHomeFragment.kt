package com.sohohouse.seven.housepay.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.FragmentHousePayHomeBinding

class HousePayHomeFragment : Fragment() {

    private var _binding: FragmentHousePayHomeBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHousePayHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = HousePayHomePagerAdapter(this, resources)
        with(binding) {
            housePayHomeToolbar.apply {
                toolbarTitle.text = getString(R.string.housepay_dashboard_title)
                toolbarBackBtn.setOnClickListener {
                    requireActivity().onBackPressed()
                }
            }
            housePayHomeViewpager.adapter = adapter

            TabLayoutMediator(
                housePayHomeTablayout,
                housePayHomeViewpager
            ) { tab, position ->
                tab.text = resources.getString(adapter.getTitleStringId(position))
            }.attach()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

