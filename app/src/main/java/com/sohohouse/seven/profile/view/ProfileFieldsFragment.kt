package com.sohohouse.seven.profile.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.fragmentViewModel
import com.sohohouse.seven.common.BundleKeys.PROFILE_ID_KEY
import com.sohohouse.seven.common.extensions.setFragmentResult
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.ReloadableErrorStateView
import com.sohohouse.seven.databinding.FragmentViewProfileBinding
import com.sohohouse.seven.home.completeyourprofile.SetUpAppPromptItem
import com.sohohouse.seven.home.houseboard.RendererDiffAdapter
import com.sohohouse.seven.more.AccountFragment
import com.sohohouse.seven.more.SettingsActivity
import com.sohohouse.seven.profile.ResultListener
import com.sohohouse.seven.profile.edit.EditProfileActivity
import javax.inject.Inject

class ProfileFieldsFragment : BaseMVVMFragment<ProfileFieldsViewModel>(), Errorable.View,
    Loadable.View, ResultListener {
    override val viewModelClass: Class<ProfileFieldsViewModel>
        get() = ProfileFieldsViewModel::class.java

    val binding by viewBinding(FragmentViewProfileBinding::bind)

    @Inject
    lateinit var assistedFactory: ProfileFieldsViewModel.Factory

    override val viewModel: ProfileFieldsViewModel by fragmentViewModel {
        assistedFactory.create(arguments?.getString(PROFILE_ID_KEY) ?: "")
    }

    override val loadingView: LoadingView
        get() = binding.fragmentViewProfileLoadingView

    override val errorStateView: ReloadableErrorStateView
        get() = binding.errorState

    private val onCompleteProfileClick = { item: SetUpAppPromptItem -> onPromptClick(item) }

    private val adapter = RendererDiffAdapter().apply {
        registerRenderers(
            ViewProfileFieldRenderer(),
            CompleteYourProfileRenderer(onCompleteProfileClick)
        )
    }

    init {
        lifecycleScope.launchWhenResumed { viewModel.fetchData() }
    }

    override val contentLayoutId: Int
        get() = R.layout.fragment_view_profile

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
        observeDataItems()
        observeLoadingState(this)
        observeErrorState(this, { viewModel.fetchData() })
    }

    override fun onResume() {
        super.onResume()
        viewModel.logViewed()
    }

    override fun onResult(requestKey: String, bundle: Bundle) {
        viewModel.refreshData()
    }

    private fun onPromptClick(item: SetUpAppPromptItem) {
        when (item.prompt) {
            SetUpAppPromptItem.Prompt.COMPLETE_PROFILE -> onEditClick()
            SetUpAppPromptItem.Prompt.CUSTOMISE_NOTIFICATIONS -> startActivity(
                Intent(
                    requireContext(),
                    SettingsActivity::class.java
                )
            )
        }
    }

    private fun observeDataItems() {
        viewModel.profileListItems.observe(
            viewLifecycleOwner,
            { adapter.setItems(it ?: emptyList()) })
    }

    private fun setUpRecyclerView() {
        binding.apply {
            viewProfileRv.setHasFixedSize(true)
            viewProfileRv.layoutManager = LinearLayoutManager(requireContext())
            viewProfileRv.adapter = adapter
        }
    }

    private fun onEditClick() {
        viewModel.logEditClick()
        startActivityForResult(
            Intent(requireContext(), EditProfileActivity::class.java),
            REQ_CODE_EDIT_PROFILE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE_EDIT_PROFILE && resultCode == Activity.RESULT_OK) {
            viewModel.refreshData()
            requireActivity().setResult(Activity.RESULT_OK)
            setFragmentResult(AccountFragment.PROFILE_EDIT_REQUEST)
        }
    }

    companion object {
        const val REQ_CODE_EDIT_PROFILE = 1055

        fun withProfileId(id: String): ProfileFieldsFragment {
            return ProfileFieldsFragment().apply {
                arguments = Bundle().apply { putString(PROFILE_ID_KEY, id) }
            }
        }
    }
}