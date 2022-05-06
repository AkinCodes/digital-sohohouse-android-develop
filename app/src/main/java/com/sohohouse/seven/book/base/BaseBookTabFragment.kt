package com.sohohouse.seven.book.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.PinToTopAdapterDataObserver
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.book.adapter.model.EventCarousel
import com.sohohouse.seven.book.adapter.model.EventCarouselHeader
import com.sohohouse.seven.book.adapter.model.EventItem
import com.sohohouse.seven.book.adapter.renderer.*
import com.sohohouse.seven.book.eventdetails.EventDetailsActivity
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.Scrollable
import com.sohohouse.seven.common.design.adapter.PagedRendererAdapter
import com.sohohouse.seven.common.design.carousel.CarouselHeaderRenderer
import com.sohohouse.seven.common.design.carousel.CarouselRenderer
import com.sohohouse.seven.common.extensions.observeOnce
import com.sohohouse.seven.databinding.FragmentExploreViewPagerBinding
import com.sohohouse.seven.main.MainNavigationController
import com.sohohouse.seven.network.core.models.Event
import java.lang.ref.WeakReference

abstract class BaseBookTabFragment<VM : BaseBookTabViewModel> : BaseMVVMFragment<VM>(),
    Scrollable,
    Loadable.View {

    private val adapter: PagedRendererAdapter<DiffItem> = PagedRendererAdapter<DiffItem>().apply {
        registerRenderers(
            FeatureEventRenderer(::onItemClicked),
            ListEventRenderer(::onItemClicked),
            DividerRenderer(),
            LoadingStateRenderer(),
            ZeroStateItemRenderer(),
            CarouselHeaderRenderer(EventCarouselHeader::class.java, ::onSeeAllOnDemandEvents),
            CarouselRenderer(EventCarousel::class.java, ::onItemClicked),
            ErrorStateRenderer(::invalidate),
            FilterStateRenderer()
        )
    }


    override val viewModel: VM by lazy {
        ViewModelProvider(requireActivity(), viewModelFactory).get(viewModelClass)
    }

    override val swipeRefreshLayout: SwipeRefreshLayout?
        get() = binding.swipeRefreshLayout

    private val pinToTopAdapterObserver by lazy {
        PinToTopAdapterDataObserver(
            WeakReference(binding.exploreRecyclerView)
        )
    }

    private val binding by viewBinding(FragmentExploreViewPagerBinding::bind)

    override val contentLayoutId: Int
        get() = R.layout.fragment_explore_view_pager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setupView()
        observeViewModel()
    }

    override fun onDestroyView() {
        binding.exploreRecyclerView.adapter = null
        super.onDestroyView()
    }

    private fun FragmentExploreViewPagerBinding.setupView() {
        exploreRecyclerView.adapter = adapter
        swipeRefreshLayout.setOnRefreshListener { viewModel.invalidate() }
    }

    private fun observeViewModel() {
        observeLoadingState(viewLifecycleOwner) { setLoadingState(it) }
        viewModel.events.observe(viewLifecycleOwner) { adapter.submitList(it) }
        viewModel.isReady.observeOnce(viewLifecycleOwner) {}
    }

    fun updateListItem(event: Event) {
        viewModel.getEventIndex(event)?.let { adapter.notifyItemChanged(it) }
    }

    private fun invalidate() {
        viewModel.invalidate()
    }

    override fun scrollToPosition(position: Int) {
        binding.exploreRecyclerView.smoothScrollToPosition(position)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                EventDetailsActivity.REQUEST_CODE_EVENT_DETAILS -> {
                    val serializable = data?.getSerializableExtra(BundleKeys.EVENT)
                    serializable?.let {
                        val event = it as Event
                        viewModel.getEventIndex(event)
                            ?.let { pos -> adapter.notifyItemChanged(pos) }
                    }
                }
            }
        }
    }

    private fun setLoadingState(state: LoadingState) {
        if (isVisible && isResumed) {
            (requireActivity() as? MainNavigationController)?.setLoadingState(state)
        }
    }

    abstract fun onFilterButtonClicked()

    protected fun onItemClicked(item: EventItem, imageView: ImageView?, position: Int) {
        viewModel.logEventClick(item, position)
        startTransitionToDetail(item.id, item.imageUrl, imageView)
    }

    private fun startTransitionToDetail(id: String, imageUrl: String?, imageView: ImageView?) {
        val intent = EventDetailsActivity.getIntent(activity, id, imageUrl)
        val options = if (imageView != null) {
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                (activity as Activity),
                imageView,
                getString(R.string.events_event_image_description)
            )
        } else {
            null
        }
        startActivityForResult(
            intent,
            EventDetailsActivity.REQUEST_CODE_EVENT_DETAILS,
            options?.toBundle()
        )
    }

    protected open fun onSeeAllOnDemandEvents() {
    }
}
