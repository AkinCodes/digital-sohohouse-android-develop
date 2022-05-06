package com.sohohouse.seven.housepay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.setChildFragmentResultListener
import com.sohohouse.seven.databinding.FragmentHousePayCheckBinding
import com.sohohouse.seven.housepay.checkdetail.open.OpenCheckFragment

class FragmentHousepayCheck : Fragment() {
    private var _binding: FragmentHousePayCheckBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHousePayCheckBinding.inflate(inflater, container, false)
        val view = binding.root
        setChildFragmentResultListener(OpenCheckFragment.REQ_KEY_CHECK_DISMISSED) { _, _ ->
            requireActivity().finish()
        }

        val openCheckFragment = OpenCheckFragment()
        childFragmentManager.beginTransaction().replace(R.id.container, openCheckFragment)
            .commitAllowingStateLoss()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}