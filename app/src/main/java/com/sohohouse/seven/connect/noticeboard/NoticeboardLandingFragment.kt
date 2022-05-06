package com.sohohouse.seven.connect.noticeboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.error.ErrorDialogHelper
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.base.mvvm.fragmentViewModel
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.common.views.ItemPaddingDecoration
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.common.views.showSafe
import com.sohohouse.seven.connect.filter.FilterBottomSheetFragment
import com.sohohouse.seven.connect.filter.FilterBottomSheetFragment.Companion.REQUEST_CODE_FILTERS
import com.sohohouse.seven.connect.filter.base.Filter
import com.sohohouse.seven.connect.noticeboard.PostDetailsResult.Action
import com.sohohouse.seven.connect.noticeboard.create_post.NoticeboardCreatePostFragment
import com.sohohouse.seven.connect.noticeboard.post_details.NoticeboardPostDetailsActivity
import com.sohohouse.seven.connect.noticeboard.user_reactions.UserReactionsBottomSheet
import com.sohohouse.seven.databinding.FragmentNoticeboardLandingBinding
import com.sohohouse.seven.home.houseboard.RendererDiffAdapter
import com.sohohouse.seven.main.MainNavigationController
import com.sohohouse.seven.network.core.models.Reaction
import com.sohohouse.seven.profile.view.ProfileViewerFragment
import javax.inject.Inject

class NoticeboardLandingFragment :
    BaseMVVMFragment<NoticeboardLandingViewModel>(), Loadable.View {

    val binding by viewBinding(FragmentNoticeboardLandingBinding::bind)

    override val contentLayoutId get() = R.layout.fragment_noticeboard_landing

    @Inject
    lateinit var assistedFactory: NoticeboardLandingViewModel.Factory

    override val viewModel: NoticeboardLandingViewModel by fragmentViewModel {
        assistedFactory.create(profileId)
    }

    override val viewModelClass: Class<NoticeboardLandingViewModel>
        get() = NoticeboardLandingViewModel::class.java

    private val profileId: String
        get() = arguments?.getString(BundleKeys.PROFILE_ID_KEY) ?: ""

    private val adapter = NoticeboardLandingAdapter(
        onProfileClick = ::onProfileClick,
        onReplyClick = ::onReplyClick,
        onDeletePostClick = ::onDeletePostClick,
        onCreatePostClick = ::showCreatePostFragment,
        onTopicClick = ::onTopicClick,
        onHouseClick = ::onHouseClick,
        onCityClick = ::onCityClick,
        onReactionClick = ::onReactionClick,
        onReactToPostClick = ::onReactToPost,
        onRemoveReactionClick = ::onRemoveReactionClicked,
        onReactionLongPress = ::onReactionsLongPress
    )

    private fun onRemoveReactionClicked(reaction: Reaction, postId: NoticeboardPost) {
        viewModel.removeReactionFromPost(reaction, postId)
    }

    private lateinit var postDetailsLauncher: ActivityResultLauncher<String>

    init {
        lifecycleScope.launchWhenCreated {
            postDetailsLauncher = registerForPostDetailsActivityResult()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val filterAdapter = RendererDiffAdapter().apply {
            registerRenderers(FilterRenderer(::onFilterClick))
        }
        with(binding) {
            setupViews(filterAdapter)
        }
        observeViewModel(filterAdapter)
    }

    private fun FragmentNoticeboardLandingBinding.setupViews(filterAdapter: RendererDiffAdapter) {
        setUpRv(filterAdapter)
        setUpPullToRefresh()
        setUpFilterBtn()
        setUpStickyFilterRow()
        errorStateView.reloadClicks { viewModel.refresh() }
    }

    private fun observeViewModel(filterAdapter: RendererDiffAdapter) {
        observeLoadingState()
        viewModel.error.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.errorStateView.visibility = View.VISIBLE
                binding.errorStateView.reloadClicks {
                    binding.errorStateView.visibility = View.GONE
                }
            } else {
                ErrorDialogHelper.showGenericErrorDialog(requireContext(), it)
            }
        }
        observeAdapterItems(filterAdapter)
    }

    //TODO use Activity Results API
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }

        when (requestCode) {
            REQ_CODE_POST_CREATED -> viewModel.refresh()
            REQUEST_CODE_FILTERS -> viewModel.checkUpdateFilters()
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun FragmentNoticeboardLandingBinding.setUpFilterBtn() {
        if (profileId.isNotEmpty()) {
            filterStripContainer.setGone()
            return
        }

        filterButton.setOnClickListener {
            if (parentFragmentManager.findFragmentByTag(FilterBottomSheetFragment.TAG) != null) return@setOnClickListener

            viewModel.onFilterButtonClick()
            NoticeboardFilterBottomSheet().also { dialog ->
                dialog.setTargetFragment(this@NoticeboardLandingFragment, REQUEST_CODE_FILTERS)
            }.show(parentFragmentManager, FilterBottomSheetFragment.TAG)
        }
    }

    private fun FragmentNoticeboardLandingBinding.setUpPullToRefresh() {
        swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
            errorStateView.setGone()
        }
    }

    private fun observeAdapterItems(filterAdapter: RendererDiffAdapter) {
        viewModel.items.observe(viewLifecycleOwner, { adapter.submitList(it) })
        viewModel.filters.observe(viewLifecycleOwner, { filterAdapter.setItems(it) })
    }

    private fun observeLoadingState() {
        observeLoadingState(viewLifecycleOwner) {
            if (isVisible && isResumed) {
                (requireActivity() as? MainNavigationController)?.setLoadingState(it)
                if (it == LoadingState.Idle)
                    binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun FragmentNoticeboardLandingBinding.setUpRv(filterAdapter: RendererDiffAdapter) {
        noticeboardRv.apply {
            adapter = this@NoticeboardLandingFragment.adapter
            addItemDecoration(
                ItemPaddingDecoration(
                    RecyclerView.VERTICAL,
                    resources.getDimensionPixelOffset(R.dimen.dp_24)
                ).apply { skipFirst = profileId.isEmpty() }
            )
        }

        filterRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = filterAdapter
        }

        viewModel.scrollToTopEvent.observe(lifecycleOwner, { noticeboardRv.scrollToPosition(0) })
    }

    private fun showCreatePostFragment() {
        viewModel.onWritePostClick()

        NoticeboardCreatePostFragment()
            .apply { setTargetFragment(this@NoticeboardLandingFragment, REQ_CODE_POST_CREATED) }
            .showSafe(parentFragmentManager, NoticeboardCreatePostFragment.TAG)
    }

    private fun FragmentNoticeboardLandingBinding.setUpStickyFilterRow() {
        if (profileId.isNotEmpty()) {
            return
        }

        noticeboardRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                repositionFilterRow()
            }
        })
    }

    private fun FragmentNoticeboardLandingBinding.repositionFilterRow() {
        val createPostContainer = noticeboardRv.findViewHolderForAdapterPosition(0)?.itemView
        if (createPostContainer == null) { //if create post VH is null, its scrolled out of view
            //pin filter row to top of RV
            filterStripContainer.y = noticeboardRv.y - createPostBtnOverlay.height
            return
        }
        val createPostBtn = createPostContainer.findViewById<View>(R.id.create_post_btn)
        val createPostPos = createPostContainer.y

        if ((createPostPos + createPostBtn.height) >= noticeboardRv.y) { // as long as create post btn is not fully scrolled out of view
            //move filter row up with it
            filterStripContainer.y = createPostPos
        }
    }

    private fun onReplyClick(post: NoticeboardPost) {
        viewModel.onReplyClick(post)
        postDetailsLauncher.launch(post.postId)
    }

    private fun registerForPostDetailsActivityResult(): ActivityResultLauncher<String> {
        return registerForActivityResult(object :
            ActivityResultContract<String, PostDetailsResult?>() {
            override fun createIntent(context: Context, input: String?): Intent {
                return NoticeboardPostDetailsActivity.getIntent(requireContext(), input ?: "")
            }

            override fun parseResult(resultCode: Int, intent: Intent?): PostDetailsResult? {
                if (resultCode == Activity.RESULT_OK) {
                    return intent?.getSerializableExtra(BundleKeys.POST_DETAILS_RESULT) as? PostDetailsResult?
                }
                return null
            }
        }) {
            when (it?.action) {
                Action.POST_UPDATED -> viewModel.onPostUpdated(it.postId)
                Action.POST_DELETED -> viewModel.onPostDeleted(it.postId)
                Action.TAG_SELECTED -> viewModel.checkUpdateFilters()
            }
        }
    }

    private fun onProfileClick(profile: ProfileItem) {
        if (profile.id == profileId) return

        viewModel.onProfileClick(profile.id)
        ProfileViewerFragment.withProfile(profile)
            .showSafe(parentFragmentManager, ProfileViewerFragment.TAG)
    }

    private fun onDeletePostClick(post: NoticeboardPost) {
        viewModel.onDeletePostClick(post)
        NoticeboardUtil.createDeletePostConfirmDialog(
            requireContext(),
            { viewModel.deletePost(post.postId) })
            .showSafe(
                requireActivity().supportFragmentManager,
                NoticeboardUtil.DELETE_POST_DIALOG_TAG
            )
    }

    private fun onFilterClick(filter: Filter) {
        viewModel.removeFilter(filter)
    }

    private fun onTopicClick(topic: Filter) {
        viewModel.onTopicClick(topic)
    }

    private fun onHouseClick(house: Filter) {
        viewModel.onHouseTagClick(house)
    }

    private fun onCityClick(city: Filter) {
        viewModel.onCityTagClick(city)
    }

    private fun onReactionClick(postId: String) {
        UserReactionsBottomSheet()
            .apply { arguments = bundleOf(BundleKeys.ID to postId) }
            .showSafe(parentFragmentManager, UserReactionsBottomSheet.TAG)
    }

    private fun onReactToPost(reaction: Reaction, post: NoticeboardPost) {
        viewModel.reactToPost(reaction, post)
    }

    private fun onReactionsLongPress(
        reaction: Reaction?,
        post: NoticeboardPost,
    ) {
        viewModel.logReactionEvents(
            post.postId,
            reaction,
            AnalyticsManager.Action.NoticeboardReactionLongPress
        )
    }

    companion object {
        private const val REQ_CODE_POST_CREATED = 333
        const val TAG = "Noticeboard_landing_fragment"

        fun withProfileId(id: String): NoticeboardLandingFragment {
            return NoticeboardLandingFragment().apply {
                arguments = Bundle().apply { putString(BundleKeys.PROFILE_ID_KEY, id) }
            }
        }
    }
}

