package com.sohohouse.seven.book

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.Keep
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.book.base.BaseBookTabFragment
import com.sohohouse.seven.book.eventdetails.EventDetailsActivity
import com.sohohouse.seven.book.eventdetails.eventstatus.EventStatusActivity
import com.sohohouse.seven.book.eventdetails.eventstatus.EventStatusItem
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.Scrollable
import com.sohohouse.seven.common.adapterhelpers.OnPageChangeListenerAdapter
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.apihelpers.SohoWebHelper
import com.sohohouse.seven.common.extensions.addOnTabSelectedListener
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.utils.Backable
import com.sohohouse.seven.common.views.inappnotification.InAppNotification
import com.sohohouse.seven.common.views.inappnotification.InAppNotificationAdapterItem
import com.sohohouse.seven.common.views.showSafe
import com.sohohouse.seven.common.views.webview.WebViewBottomSheetFragment
import com.sohohouse.seven.databinding.FragmentBookBinding
import com.sohohouse.seven.main.MainActivity
import com.sohohouse.seven.main.MainNavigationController
import com.sohohouse.seven.network.core.models.Event

@Keep
class BookFragment : BaseMVVMFragment<BookViewModel>(), Scrollable, Backable {

    companion object {
        const val EXPLORE_NOTIFICATION_EVENT_ID = "eventId"
        const val SELECTED_BOOK_TAB_REQ_KEY = "selected_book_tab_req_key"
        const val BOOK_TAB_TAG = "book_tab_tag"
    }

    private val binding by viewBinding(FragmentBookBinding::bind)

    override val contentLayoutId: Int
        get() = R.layout.fragment_book

    override val viewModelClass: Class<BookViewModel>
        get() = BookViewModel::class.java

    private val adapter: BookViewPagerAdapter by lazy {
        BookViewPagerAdapter(requireContext(), childFragmentManager)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.addTabs(viewModel.tabs)

        with(binding) {
            setupTabs()
            filter.clicks {
                val fragment = adapter.currentFragment as? BaseBookTabFragment<*>
                fragment?.onFilterButtonClicked()
            }
        }
        observeViewModel()

        if (savedInstanceState != null) {
            (activity as? MainNavigationController)?.setLoadingState(LoadingState.Idle)
        }

        viewModel.setScreenName(name= AnalyticsManager.Screens.Booking.name)
    }

    private fun observeViewModel() {
        viewModel.apply {
            showBookAVisitWebView.observe(viewLifecycleOwner) { showBookAVisitWebView() }
            showEventDetail.observe(viewLifecycleOwner) { showEventDetail(it) }
            showEventStatus.observe(viewLifecycleOwner) { showEventStatus(it) }
            showFilter.observe(viewLifecycleOwner) { showFilter(it) }
            showInvalidDialog.observe(viewLifecycleOwner) { showInvalidDialog() }
            showInvalidEventDialog.observe(viewLifecycleOwner) { showInvalidEventDialog() }
        }
    }

    override fun onBackPressed(): Boolean {
        val frag = adapter.currentFragment
        return (frag as? Backable)?.onBackPressed() ?: false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                EventDetailsActivity.REQUEST_CODE_EVENT_DETAILS -> {
                    val serializable = data?.getSerializableExtra(BundleKeys.EVENT)
                    serializable?.let {
                        val event = it as Event
                        val fragment = (binding.viewpager.adapter as BookViewPagerAdapter)
                            .getItem(binding.viewpager.currentItem) as BaseBookTabFragment<*>
                        fragment.updateListItem(event)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.trackBackToAppFromBookBedroom(binding.viewpager.currentItem)
        (activity as? MainNavigationController)?.indicateCurrentTab(tag)
    }

    private fun FragmentBookBinding.setupTabs() {
        val pageChangeListener = object : OnPageChangeListenerAdapter() {
            override fun onPageSelected(position: Int) {
                viewModel.onPageSelected(position)
            }
        }
        viewpager.adapter = adapter

        tabLayout.setupWithViewPager(viewpager)
        viewpager.addOnPageChangeListener(pageChangeListener)

        showNotificationModal()

        tabLayout.addOnTabSelectedListener(tabReselected = { scrollToPosition(0) })

        requireActivity().supportFragmentManager.setFragmentResultListener(
            SELECTED_BOOK_TAB_REQ_KEY,
            viewLifecycleOwner
        ) { s: String, bundle: Bundle ->
            if (s == SELECTED_BOOK_TAB_REQ_KEY) {
                val bookTab = bundle.getSerializable(BOOK_TAB_TAG) as BookTab
                selectTab(bookTab, pageChangeListener)
            }
        }
    }

    private fun showFilter(visible: Boolean) {
        binding.filter.isVisible = visible
    }

    override fun scrollToPosition(position: Int) {
        val fragment = adapter.currentFragment
        if (fragment is Scrollable) {
            fragment.scrollToPosition(position)
        }
    }

    private fun showNotificationModal() {
        val eventId = arguments?.getString(BundleKeys.ID).orEmpty()
        if (eventId.isNotEmpty() && eventId != "null") viewModel.showNotificationModal(eventId)
    }

    private fun selectTab(
        tab: BookTab,
        pageChangeListener: ViewPager.OnPageChangeListener
    ) {
        val index = viewModel.tabs.indexOf(tab).takeIf { it != -1 } ?: 0
        binding.tabLayout.getTabAt(index)?.select()
        pageChangeListener.onPageSelected(index)
        viewModel.onPageSelected(index)
    }

    private fun showEventStatus(bookingSuccessItem: EventStatusItem) {
        startActivity(EventStatusActivity.getIntent(requireContext(), bookingSuccessItem))
    }

    private fun showInvalidDialog() {
        val item = InAppNotificationAdapterItem(
            imageDrawableId = R.drawable.icon_no_network_detected,
            status = getString(R.string.server_error_header),
            textBody = getString(R.string.server_error_supporting),
            primaryButtonString = getString(R.string.server_error_home_cta),
            isTextBodyVisible = true,
            isSecondaryButtonVisible = false,
            primaryClicked = {
                MainActivity.start(requireContext(), R.id.menu_home)
            })

        (activity as? InAppNotification)?.showInAppNotification(requireContext(), item)
    }

    private fun showInvalidEventDialog() {
        val item = InAppNotificationAdapterItem(
            imageDrawableId = R.drawable.icon_link_your_calendar,
            status = getString(R.string.event_unavailable_header),
            textBody = getString(R.string.event_unavailable_supporting),
            primaryButtonString = getString(R.string.event_unavailable_explore_cta),
            secondaryButtonString = getString(R.string.event_unavailable_dismiss_cta),
            isTextBodyVisible = true,
            isSecondaryButtonVisible = true
        )

        (activity as? InAppNotification)?.showInAppNotification(requireContext(), item)
    }

    private fun showEventDetail(event: Event) {
        startActivity(EventDetailsActivity.getIntent(activity, event.id, event.images?.large))
    }

    private fun showBookAVisitWebView() {
        WebViewBottomSheetFragment.withKickoutType(SohoWebHelper.KickoutType.BOOK_A_VISIT)
            .showSafe(requireActivity().supportFragmentManager, WebViewBottomSheetFragment.TAG)
    }

}
