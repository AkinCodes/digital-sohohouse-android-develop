package com.sohohouse.seven.payment.housepay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sohohouse.seven.databinding.FragmentHousePayCreditsBinding

class HousePayCreditsFragment : Fragment() {

    private var _binding: FragmentHousePayCreditsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHousePayCreditsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
}