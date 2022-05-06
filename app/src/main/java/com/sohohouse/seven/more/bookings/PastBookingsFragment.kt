package com.sohohouse.seven.more.bookings

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.base.mvvm.ErrorDialogViewController
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingViewController
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.apihelpers.SohoWebHelper
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.webview.WebViewBottomSheetFragment
import com.sohohouse.seven.databinding.FragmentPastBookingsBinding
import com.sohohouse.seven.more.bookings.detail.EventBookingDetailsActivity
import com.sohohouse.seven.more.bookings.recycler.EventBookingAdapterItem
import com.sohohouse.seven.more.bookings.recycler.PastBookingsCollapsableMonthItem
import com.sohohouse.seven.more.bookings.recycler.RoomBookingAdapterItem
import com.sohohouse.seven.more.bookings.recycler.TableBookingAdapterItem

class PastBookingsFragment : BaseMVVMFragment<PastBookingsViewModel>(),
    MorePastBookingsAdapterListener,
    Loadable.View, ErrorDialogViewController {

    private val binding by viewBinding(FragmentPastBookingsBinding::bind)

    private val adapter = MyBookingsAdapter(this)

    override val viewModelClass: Class<PastBookingsViewModel>
        get() = PastBookingsViewModel::class.java

    override val loadingView: LoadingView
        get() = (requireActivity() as LoadingViewController).loadingView

    override val swipeRefreshLayout: SwipeRefreshLayout
        get() = binding.bookingsSrl

    override val contentLayoutId: Int
        get() {
            return R.layout.fragment_past_bookings
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRv()
        observeItems()
        observeLoadingState(viewLifecycleOwner)
        observeErrorDialogEvents()
        viewModel.fetchItems()
    }

    private fun observeItems() {
        viewModel.adapterItems.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.itemChangeEvent.observe(viewLifecycleOwner) {
            adapter.notifyItemChanged(it.index, it.payload)
        }
    }

    private fun setUpRv() = with(binding) {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        bookingsSrl.setOnRefreshListener { viewModel.reload() }
    }

    override fun onEventBookingClick(item: EventBookingAdapterItem) {
        viewModel.logEventBookingClickEvent(item.eventBooking)
        startActivity(Intent(requireContext(), EventBookingDetailsActivity::class.java).apply {
            putExtra(BundleKeys.EVENT_BOOKING, item.eventBooking)
        })
    }

    override fun onTableBookingClick(tableBookingAdapterItem: TableBookingAdapterItem) {
        //do nothing; we dont show table bookings here
    }

    override fun onExploreButtonClicked() {
        (activity as? Listener)?.onExploreButtonClick()
    }

    override fun onReloadButtonClicked() {
        reload()
    }

    private fun reload() {
        viewModel.reload()
    }

    override fun onMonthHeaderClick(item: PastBookingsCollapsableMonthItem) {
        viewModel.onMonthHeaderClick(item)
    }

    override fun onRoomBookingClick(it: RoomBookingAdapterItem) {
        viewModel.logRoomBookingClickEvent(it.roomBooking)
        WebViewBottomSheetFragment.withKickoutType(
            type = SohoWebHelper.KickoutType.ROOM_BOOKING,
            id = it.roomBooking.id
        )
            .show(requireActivity().supportFragmentManager, WebViewBottomSheetFragment.TAG)
    }

    interface Listener {
        fun onExploreButtonClick()
    }

}
