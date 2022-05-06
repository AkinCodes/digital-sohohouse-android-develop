package com.sohohouse.seven.profile.edit.socialmedia

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseBottomSheet
import com.sohohouse.seven.base.mvvm.BaseMVVMBottomSheet
import com.sohohouse.seven.base.mvvm.fragmentViewModel
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.bottomsheet.BottomSheetFactory
import com.sohohouse.seven.common.form.FormItemDecoration
import com.sohohouse.seven.databinding.BottomsheetConnectedAccountsBinding
import com.sohohouse.seven.profile.ProfileField
import javax.inject.Inject

class EditSocialMediaBottomSheet : BaseMVVMBottomSheet<EditSocialMediaViewModel>() {

    companion object {
        class Factory(val field: ProfileField.SocialMedia) : BottomSheetFactory {
            override fun create(): BaseBottomSheet {
                return EditSocialMediaBottomSheet().apply {
                    arguments = Bundle().apply {
                        putSerializable(BundleKeys.SOCIAL_MEDIA, this@Factory.field)
                    }
                }
            }
        }
    }

    override val viewModelClass: Class<EditSocialMediaViewModel> =
        EditSocialMediaViewModel::class.java

    @Inject
    lateinit var assistedFactory: EditSocialMediaViewModel.Factory

    override val viewModel: EditSocialMediaViewModel by fragmentViewModel {
        assistedFactory.create(
            field
        )
    }

    private val adapter = EditSocialMediaAdapter()

    private val field: ProfileField.SocialMedia by lazy {
        arguments?.getSerializable(BundleKeys.SOCIAL_MEDIA) as ProfileField.SocialMedia
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = BottomsheetConnectedAccountsBinding.bind(view)
        setUpRv(binding)
        with(binding.header) {
            editProfileModalFieldTitle.text = getString(R.string.profile_connected_accounts_label)
            editProfileModalDone.setOnClickListener { onConfirmed() }
        }
        viewModel.items.observe(lifecycleOwner, Observer { adapter.submitItems(it) })
    }

    private fun setUpRv(binding: BottomsheetConnectedAccountsBinding) {
        binding.socialsRv.addItemDecoration(FormItemDecoration(requireContext(), adapter))
        binding.socialsRv.adapter = this.adapter
    }

    private fun onConfirmed() {
        (context as Listener).onConnectedAccountsConfirmed(field)
        dismiss()
    }

    override val contentLayout: Int
        get() = R.layout.bottomsheet_connected_accounts

    interface Listener {
        fun onConnectedAccountsConfirmed(field: ProfileField.SocialMedia)
    }

}

