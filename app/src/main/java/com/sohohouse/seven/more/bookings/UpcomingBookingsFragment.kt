package com.sohohouse.seven.more.bookings

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.base.mvvm.ErrorViewStateViewController
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.book.eventdetails.EventDetailsActivity
import com.sohohouse.seven.book.table.booked.BookedTableDetailsActivity
import com.sohohouse.seven.book.table.model.BookedTable
import com.sohohouse.seven.common.apihelpers.SohoWebHelper
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.ReloadableErrorStateView
import com.sohohouse.seven.common.views.webview.WebViewBottomSheetFragment
import com.sohohouse.seven.databinding.FragmentUpcomingBookingsBinding
import com.sohohouse.seven.more.bookings.recycler.EventBookingAdapterItem
import com.sohohouse.seven.more.bookings.recycler.RoomBookingAdapterItem
import com.sohohouse.seven.more.bookings.recycler.TableBookingAdapterItem

class UpcomingBookingsFragment : BaseMVVMFragment<UpcomingBookingsViewModel>(),
    MorePastBookingsAdapterListener,
    Loadable.View, ErrorViewStateViewController {

    private val binding by viewBinding(FragmentUpcomingBookingsBinding::bind)

    private val adapter = MyBookingsAdapter(this)

    override val contentLayoutId: Int
        get() {
            return R.layout.fragment_upcoming_bookings
        }

    override val viewModelClass: Class<UpcomingBookingsViewModel> =
        UpcomingBookingsViewModel::class.java

    override val loadingView: LoadingView
        get() = binding.fragmentUpcomingBookingsLoadingView

    override val swipeRefreshLayout: SwipeRefreshLayout
        get() = binding.bookingsSrl

    override fun onStart() {
        super.onStart()
        viewModel.fetchBookings()
        viewModel.logTabSelectedAction()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRv()
        observeLoadingState(viewLifecycleOwner)
        observeAdapterItems()
        observeErrorViewEvents()
    }

    private fun observeAdapterItems() {
        viewModel.adapterItems.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
            }
        }
        viewModel.bookedTableDetails.observe(viewLifecycleOwner) { gotoBookedTable(it) }
    }

    private fun setUpRv() = with(binding) {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        bookingsSrl.setOnRefreshListener {
            errorState.setGone()
            viewModel.fetchBookings()
        }
    }

    private fun gotoBookedTable(details: BookedTable) {
        startActivity(BookedTableDetailsActivity.newIntent(activity, details.id))
    }

    override fun onExploreButtonClicked() {
        (activity as? Listener)?.onExploreButtonClick()
    }

    override fun onEventBookingClick(item: EventBookingAdapterItem) {
        viewModel.logEventBookingClickEvent(item.eventBooking)
        startActivity(
            EventDetailsActivity.getIntent(
                activity,
                item.eventBooking.event?.id,
                item.eventBooking.event?.images?.large
            )
        )
    }

    override fun onRoomBookingClick(it: RoomBookingAdapterItem) {
        viewModel.logRoomClickEvent(it.roomBooking)
        WebViewBottomSheetFragment.withKickoutType(
            type = SohoWebHelper.KickoutType.ROOM_BOOKING,
            id = it.roomBooking.id
        )
            .show(requireActivity().supportFragmentManager, WebViewBottomSheetFragment.TAG)
    }

    override fun onTableBookingClick(item: TableBookingAdapterItem) {
        viewModel.tableBookingSelected(item)
    }

    override fun onReloadButtonClicked() {
        reload()
    }

    private fun reload() {
        viewModel.reloadDataAfterError()
    }

    interface Listener {
        fun onExploreButtonClick()
    }

    override fun getErrorStateView(): ReloadableErrorStateView {
        return binding.errorState
    }

}
