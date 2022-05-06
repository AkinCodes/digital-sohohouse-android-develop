package com.sohohouse.seven.book.digital

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.book.adapter.model.EventListItem
import com.sohohouse.seven.book.eventdetails.EventDetailsActivity
import com.sohohouse.seven.common.design.adapter.PagedRendererAdapter
import com.sohohouse.seven.common.design.list.ListItemPaddingDecoration
import com.sohohouse.seven.common.design.list.ListItemRenderer
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.common.views.ReloadableErrorStateView
import com.sohohouse.seven.databinding.FragmentOnDemandEventsBinding

class DigitalEventsFragment : BaseMVVMFragment<DigitalEventsViewModel>(),
    Loadable.View, Errorable.View {

    override val viewModelClass: Class<DigitalEventsViewModel>
        get() = DigitalEventsViewModel::class.java

    override val errorStateView: ReloadableErrorStateView?
        get() = binding.errorView

    private val binding by viewBinding(FragmentOnDemandEventsBinding::bind)

    override val contentLayoutId: Int
        get() = R.layout.fragment_on_demand_events

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            (requireActivity() as? AppCompatActivity)?.apply {
                setSupportActionBar(toolbar)
                supportActionBar?.setDisplayShowTitleEnabled(false)
            }

            with(buildAdapter()) {
                setupView(this)
                setupViewModel(this)
            }
        }
    }

    private fun buildAdapter(): PagedRendererAdapter<EventListItem> {
        return PagedRendererAdapter<EventListItem>().apply {
            registerRenderer(ListItemRenderer(EventListItem::class.java, ::onItemClicked))
        }
    }

    private fun FragmentOnDemandEventsBinding.setupView(adapter: PagedRendererAdapter<EventListItem>) {
        val horizontalPadding = resources.getDimensionPixelSize(R.dimen.dp_16)
        val verticalPadding = resources.getDimensionPixelSize(R.dimen.dp_8)

        recyclerView.addItemDecoration(
            ListItemPaddingDecoration(
                RecyclerView.VERTICAL,
                verticalPadding,
                horizontalPadding
            )
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun FragmentOnDemandEventsBinding.setupViewModel(adapter: PagedRendererAdapter<EventListItem>) {
        viewModel.events.observe(viewLifecycleOwner, { adapter.submitList(it) })
        viewModel.loadingState.observe(
            viewLifecycleOwner,
            { progressBar.setVisible(LoadingState.Loading == it) })
        observeErrorState(viewLifecycleOwner)
    }

    private fun onItemClicked(item: EventListItem, imageView: ImageView?, position: Int) {
        val intent = EventDetailsActivity.getIntent(activity, item.id, item.imageUrl)

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
}