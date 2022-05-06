package com.sohohouse.seven.connect.mynetwork.connections

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.design.adapter.PagedRendererAdapter
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.common.views.custom.CustomButtonWithNumberIndicator
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.common.views.showSafe
import com.sohohouse.seven.connect.discover.DiscoverMembersAdapterItem
import com.sohohouse.seven.connect.discover.DiscoverMembersRenderer
import com.sohohouse.seven.connect.match.RecommendationListActivity
import com.sohohouse.seven.connect.match.RecommendationsOptInBottomSheet
import com.sohohouse.seven.connect.message.chat.ChatActivity
import com.sohohouse.seven.connect.mynetwork.blockedprofiles.BlockedProfilesBottomSheet
import com.sohohouse.seven.connect.shareprofile.ShareProfileRenderer
import com.sohohouse.seven.databinding.FragmentEmptyStateRecyclerViewBinding
import com.sohohouse.seven.profile.share.ShareProfileBottomSheet
import com.sohohouse.seven.profile.view.ProfileViewerFragment

class ConnectionsFragment : BaseMVVMFragment<ConnectionsViewModel>(), Loadable.View {

    private var customButtonWithNumberIndicator: CustomButtonWithNumberIndicator? = null
    private val shouldShowBottomButton by lazy {
        arguments?.getBoolean(SHOULD_SHOW_BOTTOM_BUTTON) ?: false
    }

    override val viewModelClass: Class<ConnectionsViewModel>
        get() = ConnectionsViewModel::class.java

    override val swipeRefreshLayout: SwipeRefreshLayout
        get() = binding.swipeRefreshLayout

    val binding by viewBinding(FragmentEmptyStateRecyclerViewBinding::bind)

    override val contentLayoutId: Int
        get() = R.layout.fragment_empty_state_recycler_view

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(MY_CONNECTIONS_CHANGED) { _, _ ->
            viewModel.refresh()
        }
        viewModel.setScreenName(name= AnalyticsManager.Screens.Connections.name)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PagedRendererAdapter<DiffItem>().apply {
            registerRenderer(ShareProfileRenderer(::onShareProfileClick, viewModel.profileImageURL))
            registerRenderer(
                DiscoverMembersRenderer(
                    ::onDiscoverMembersClick,
                    viewModel.profileImageURL
                )
            )
            registerRenderer(ProfileItemRenderer(::onClick, ::onMessageClick))
        }

        with(binding) {
            recyclerView.adapter = adapter

            swipeRefreshLayout.setOnRefreshListener { viewModel.refresh() }

            emptyStateTitle.setText(R.string.my_connections_no_connections_title)
            emptyStateSubtitle.setText(R.string.my_connections_no_connections_subtitle)
            emptyStateSubtitleAlt.setText(R.string.my_connections_no_connections_subtitle_alt)
        }

        viewModel.connections.observe(viewLifecycleOwner) { adapter.submitList(it) }
        observeLoadingState(viewLifecycleOwner) {
            showEmptyState(LoadingState.Idle == it && adapter.itemCount == 0)
        }
        viewModel.navigateToChatScreen.observe(viewLifecycleOwner) { (id, _, memberProfileId) ->
            startActivity(
                ChatActivity.newIntentViaID(
                    context = requireContext(),
                    channelId = id,
                    memberId = memberProfileId
                )
            )
        }

        if (shouldShowBottomButton) createButtonAtTheTopOfTheBottomBar()
    }

    private fun createButtonAtTheTopOfTheBottomBar() {
        val topOfBottomBar =
            requireActivity().findViewById<FrameLayout?>(R.id.topOfBottomNavigationView)
                ?: return
        customButtonWithNumberIndicator = CustomButtonWithNumberIndicator(
            ContextThemeWrapper(requireContext(), R.style.BaseTheme), null
        ).apply {
            text = context.getString(R.string.blocked_members)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            isVisible = false
            topOfBottomBar.addView(this)

            setOnClickListener {
                BlockedProfilesBottomSheet().showSafe(
                    childFragmentManager,
                    BlockedProfilesBottomSheet.TAG
                )
            }
        }
    }

    override fun onPause() {
        customButtonWithNumberIndicator?.setGone()
        super.onPause()
    }

    override fun onResume() {
        customButtonWithNumberIndicator?.setVisible()
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val topOfBottomBar =
            requireActivity().findViewById<FrameLayout?>(R.id.topOfBottomNavigationView)
                ?: return
        topOfBottomBar.children.find { it.tag == customButtonWithNumberIndicator?.tag }
            ?.let(topOfBottomBar::removeView)
    }

    private fun onClick(item: ProfileItem) {
        ProfileViewerFragment.withProfile(item)
            .showSafe(parentFragmentManager, ProfileViewerFragment.TAG)
    }

    private fun onMessageClick(item: ProfileItem) {
        viewModel.message(item)
    }

    private fun showEmptyState(isEmpty: Boolean) {
        binding.emptyState.setVisible(isEmpty)
        binding.recyclerView.setVisible(!isEmpty)
    }


    private fun onShareProfileClick() {
        ShareProfileBottomSheet().showSafe(
            childFragmentManager,
            ShareProfileBottomSheet.TAG
        )
    }

    private fun onDiscoverMembersClick(item: DiscoverMembersAdapterItem) {
        when (item) {
            DiscoverMembersAdapterItem.ShowOptInPrompt -> RecommendationsOptInBottomSheet.newInstance()
                .showSafe(parentFragmentManager, RecommendationsOptInBottomSheet.TAG)
            DiscoverMembersAdapterItem.ShowCompleteProfilePrompt -> TODO()
            DiscoverMembersAdapterItem.ShowSuggestedPeople -> {
                startActivity(RecommendationListActivity.getIntent(requireContext()))
            }
        }
    }

    companion object {
        const val MY_CONNECTIONS_CHANGED = "my_connection_changed"
        private const val SHOULD_SHOW_BOTTOM_BUTTON = "SHOULD_SHOW_BOTTOM_BUTTON"

        fun createInstance(shouldShowBottomButton: Boolean = false): ConnectionsFragment {
            return ConnectionsFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(SHOULD_SHOW_BOTTOM_BUTTON, shouldShowBottomButton)
                }
            }
        }
    }
}