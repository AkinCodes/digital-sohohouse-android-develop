package com.sohohouse.seven.profile.view.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sohohouse.seven.R
import com.sohohouse.seven.base.ErrorDialogFragment
import com.sohohouse.seven.base.LoadingDialogFragment
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.base.mvvm.fragmentViewModel
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.dagger.Injectable
import com.sohohouse.seven.common.design.adapter.RendererAdapter
import com.sohohouse.seven.common.extensions.setFragmentResult
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.common.views.showSafe
import com.sohohouse.seven.databinding.FragmentMoreOptionsBinding
import com.sohohouse.seven.profile.view.ProfileViewerFragment.Companion.CONNECTION_STATUS_CHANGED
import com.sohohouse.seven.profile.view.model.Block
import com.sohohouse.seven.profile.view.model.ProfileAction
import com.sohohouse.seven.profile.view.model.Remove
import com.sohohouse.seven.profile.view.renderer.ConnectActionRenderer
import javax.inject.Inject

class MoreOptionsBottomSheet : BottomSheetDialogFragment(), Injectable,
    Loadable.View, Errorable.View {

    private val profile: ProfileItem by lazy {
        arguments?.getParcelable(BundleKeys.PROFILE) ?: ProfileItem()
    }

    @Inject
    lateinit var assistedFactory: MoreOptionsViewModel.Factory

    override val viewModel: MoreOptionsViewModel by fragmentViewModel {
        assistedFactory.create(profile = profile)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_more_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FragmentMoreOptionsBinding.bind(view).setupView()
        setupViewModel()
    }

    private fun FragmentMoreOptionsBinding.setupView() {
        close.setOnClickListener { dismiss() }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = object : RendererAdapter<ProfileAction>() {
            init {
                registerRenderer(ConnectActionRenderer(::onClick))
                submitItems(viewModel.moreOptions)
            }

            override fun getItemViewType(position: Int): Int = ProfileAction::class.hashCode()
        }
    }

    private fun setupViewModel() {
        viewModel.statusChanged.observe(viewLifecycleOwner) { dismissWithResult() }
        viewModel.error.observe(viewLifecycleOwner) { showErrorDialog() }
        observeLoadingState(viewLifecycleOwner) { showLoadingDialog(LoadingState.Loading == it) }
    }

    private fun dismissWithResult() {
        setFragmentResult(CONNECTION_STATUS_CHANGED + profile.id)
        dismiss()
    }

    private fun showErrorDialog() {
        ErrorDialogFragment().showSafe(childFragmentManager, ErrorDialogFragment.TAG)
    }

    private fun showLoadingDialog(show: Boolean) {
        if (show) {
            LoadingDialogFragment().showSafe(childFragmentManager, LoadingDialogFragment.TAG)
        } else {
            (childFragmentManager.findFragmentByTag(LoadingDialogFragment.TAG) as? DialogFragment)?.dismiss()
        }
    }

    private fun onClick(action: ProfileAction) {
        when (action) {
            Remove -> viewModel.removeFromConnection()
            Block -> viewModel.blockMember()
            else -> {
            }
        }
    }

    companion object {
        const val TAG = "more_options_bottom_sheet"

        fun with(profile: ProfileItem): MoreOptionsBottomSheet {
            return MoreOptionsBottomSheet().apply {
                arguments = Bundle().apply { putParcelable(BundleKeys.PROFILE, profile) }
            }
        }
    }

}