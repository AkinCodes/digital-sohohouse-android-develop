package com.sohohouse.seven.connect.noticeboard.post_details

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.utils.collectLatest
import com.sohohouse.seven.common.views.ReloadableErrorStateView
import com.sohohouse.seven.common.views.TextWatcherAdapter
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.common.views.showSafe
import com.sohohouse.seven.connect.filter.base.Filter
import com.sohohouse.seven.connect.noticeboard.*
import com.sohohouse.seven.connect.noticeboard.PostDetailsResult.Action.*
import com.sohohouse.seven.connect.noticeboard.post_details.adapter.NoticeboardPostReplyRenderer
import com.sohohouse.seven.connect.noticeboard.user_reactions.UserReactionsBottomSheet
import com.sohohouse.seven.databinding.ActivityNoticeboardPostDetailsBinding
import com.sohohouse.seven.home.houseboard.RendererDiffAdapter
import com.sohohouse.seven.network.core.models.Reaction
import com.sohohouse.seven.profile.view.ProfileViewerFragment

class NoticeboardPostDetailsActivity : BaseMVVMActivity<NoticeboardPostDetailsViewModel>(),
    Loadable.View,
    ErrorViewStateViewController,
    ErrorDialogViewController {

    val binding by viewBinding(ActivityNoticeboardPostDetailsBinding::bind)

    override fun getContentLayout(): Int {
        return R.layout.activity_noticeboard_post_details
    }

    private val originalPostId: String? by lazy {
        intent.getStringExtra(BundleKeys.ID)
            ?: intent.data?.getQueryParameter(BundleKeys.ID)
            ?: intent.data?.pathSegments?.last()
    }

    override val viewModelClass: Class<NoticeboardPostDetailsViewModel>
        get() = NoticeboardPostDetailsViewModel::class.java

    override fun onBackPressed() {
        viewModel.onBackPressed()
        super.onBackPressed()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        val adapter = RendererDiffAdapter().apply {
            registerRenderers(
                NoticeboardPostRenderer(
                    onProfileClick = ::onProfileClick,
                    onReplyClick = { onReplyClick() },
                    onDeleteClick = ::onDeletePostClick,
                    onTopicClick = ::onTopicClick,
                    onCityClick = ::onCityClick,
                    onHouseClick = ::onHouseClick,
                    onReactionClick = ::onReactionClick,
                    onReactToPostClick = ::onReactToPost,
                    onRemoveReactionClick = ::onRemoveReactionClicked,
                    onReactionLongPress = ::onReactionsLongPress
                ),
                NoticeboardPostReplyRenderer(),
                LoadingItemRenderer()
            )
        }

        with(binding) {
            initViewModel()
            setUpRv(adapter)
            setUpPullToRefresh()
            setUpToolbar()
            observeLoadingState(lifecycleOwner)
            observeErrorViewEvents()
            observeErrorDialogEvents()
            observeAdapterItems(adapter)
            setUpReplyBox()
            observeOpDeletedEvent()
            setUpPostNotFoundView()
        }
    }

    private fun onReactionClick(postId: String) {
        UserReactionsBottomSheet()
            .apply { arguments = bundleOf(BundleKeys.ID to postId) }
            .showSafe(supportFragmentManager, UserReactionsBottomSheet.TAG)
    }

    private fun onReactToPost(reaction: Reaction, postID: NoticeboardPost) {
        viewModel.reactToPost(reaction, postID)
    }

    private fun onRemoveReactionClicked(reaction: Reaction, postID: NoticeboardPost) {
        viewModel.removeReactionFromPost(reaction, postID)
    }

    private fun ActivityNoticeboardPostDetailsBinding.setUpPostNotFoundView() {
        viewModel.postNotFoundEvent.observe(lifecycleOwner) { postNotFoundView.setVisible() }
        postNotFoundView.buttonClick { finish() }
    }

    private fun observeOpDeletedEvent() {
        viewModel.originalPostDeletedEvent.observe(lifecycleOwner) {
            setResult(POST_DELETED)
            finish()
        }
    }

    private fun ActivityNoticeboardPostDetailsBinding.setUpReplyBox() {
        showHideReplyBtn(replyInput.text)
        replyInput.addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable?) {
                showHideReplyBtn(s)
            }
        })
        submitReplyBtn.clicks {
            viewModel.onReplySubmitted(replyInput.text.toString().trim())
            setResult(POST_UPDATED)
        }

        replyBox.clicks {
            focusReplyBox()
        }

        viewModel.replyLoadingState.observe(lifecycleOwner) {
            when (it) {
                LoadingState.Loading -> {
                    replyInput.isEnabled = false
                    replyLoading.setVisible()
                    submitReplyBtn.setInvisible()
                }
                LoadingState.Idle -> {
                    replyInput.isEnabled = true
                    showHideReplyBtn(replyInput.text)
                    replyLoading.setInvisible()
                }
            }
        }
    }

    private fun setResult(action: PostDetailsResult.Action) {
        if (originalPostId == null) return
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(BundleKeys.POST_DETAILS_RESULT, PostDetailsResult(originalPostId!!, action))
        })
    }

    private fun focusReplyBox() {
        binding.replyInput.focusAndShowKeyboard()
    }

    private fun showHideReplyBtn(s: Editable?) {
        binding.submitReplyBtn.visibility =
            if (s?.isNotBlank() == true) View.VISIBLE else View.INVISIBLE
    }

    private fun ActivityNoticeboardPostDetailsBinding.setUpPullToRefresh() {
        swipeRefreshLayout.setOnRefreshListener { viewModel.refresh() }
    }

    private fun initViewModel() {
        viewModel.init(originalPostId)
    }

    private fun ActivityNoticeboardPostDetailsBinding.setUpToolbar() {
        noticeboardPostDetailsToolbar.toolbarBackBtn.setOnClickListener { finish() }
        noticeboardPostDetailsToolbar.toolbarTitle.text = getString(R.string.label_post)
    }

    override fun getErrorStateView(): ReloadableErrorStateView {
        return binding.errorView
    }

    private fun ActivityNoticeboardPostDetailsBinding.observeAdapterItems(adapter: RendererDiffAdapter) {
        viewModel.items.observe(lifecycleOwner) {
            replyInput.text = null
            adapter.setItems(it)
        }
    }

    private fun ActivityNoticeboardPostDetailsBinding.setUpRv(adapter: RendererDiffAdapter) {
        recyclerview.apply {
            addItemDecoration(
                DividerItemDecoration(
                    this@NoticeboardPostDetailsActivity,
                    DividerItemDecoration.VERTICAL
                ).apply {
                    setDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.divider_24dp,
                            this@NoticeboardPostDetailsActivity.theme
                        )!!
                    )
                })
            this.adapter = adapter
        }

        viewModel.showProfile.collectLatest(lifecycleOwner) {
            onProfileClick(it)
        }

        viewModel.onDeleteClick.collectLatest(lifecycleOwner) {
            deleteReplyClicked(it)
        }
    }

    private fun onProfileClick(profile: ProfileItem) {
        ProfileViewerFragment.withProfile(profile)
            .showSafe(supportFragmentManager, ProfileViewerFragment.TAG)
    }

    override val swipeRefreshLayout: SwipeRefreshLayout
        get() = binding.swipeRefreshLayout

    private fun onReplyClick() {
        focusReplyBox()
    }

    private fun deleteReplyClicked(postID: String) {
        NoticeboardUtil.createDeletePostConfirmDialog(context = this,
            onConfirmClick = {
                setResult(POST_UPDATED)
                viewModel.deleteReply(postID)
            },
            onCancelClick = { viewModel.onCancelReplyDelete(postID) }
        ).showSafe(supportFragmentManager, NoticeboardUtil.DELETE_POST_DIALOG_TAG)
    }

    private fun onDeletePostClick(post: NoticeboardPost) {
        NoticeboardUtil.createDeletePostConfirmDialog(context = this,
            onConfirmClick = {
                setResult(POST_UPDATED)
                viewModel.deletePost(
                    post.postId,
                    post.house?.id ?: "",
                    post.city?.id ?: "",
                    post.topic?.id ?: ""
                )
            },
            onCancelClick = { viewModel.onCancelDelete(post) }
        ).showSafe(supportFragmentManager, NoticeboardUtil.DELETE_POST_DIALOG_TAG)
    }

    private fun onHouseClick(filter: Filter) {
        viewModel.updateFiltersHouse(filter)
        setResult(TAG_SELECTED)
        finish()
    }

    private fun onCityClick(filter: Filter) {
        viewModel.updateFiltersCity(filter)
        setResult(TAG_SELECTED)
        finish()
    }

    private fun onTopicClick(filter: Filter) {
        viewModel.updateFiltersTopic(filter)
        setResult(TAG_SELECTED)
        finish()
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

        const val TAG = "noticeboard_post_details_activity"

        fun getIntent(context: Context, postId: String): Intent {
            return Intent(context, NoticeboardPostDetailsActivity::class.java).apply {
                putExtra(BundleKeys.ID, postId)
            }
        }
    }
}