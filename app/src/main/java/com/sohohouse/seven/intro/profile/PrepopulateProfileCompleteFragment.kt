package com.sohohouse.seven.intro.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseFragment
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.databinding.FragmentPrepopulateProfileCompleteBinding

class PrepopulateProfileCompleteFragment : BaseFragment() {
    override val contentLayoutId get() = R.layout.fragment_prepopulate_profile_complete

    private val viewModel: PrepopulateProfileViewModel by activityViewModels()

    private lateinit var binding: FragmentPrepopulateProfileCompleteBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPrepopulateProfileCompleteBinding.bind(view)

        with(binding) {
            viewModel.profileName.observe(viewLifecycleOwner) {
                subtitle.text = getString(R.string.prepopulate_profile_complete_subtitle, it)
            }
            viewModel.profileImage.observe(viewLifecycleOwner) {
                profileImg.setImageFromUrl(it, isRound = true, placeholder = R.drawable.ic_profile)
            }

            closeBtn.clicks { viewModel.onCompleteCloseBtnClick(requireContext()) }
        }
    }
}