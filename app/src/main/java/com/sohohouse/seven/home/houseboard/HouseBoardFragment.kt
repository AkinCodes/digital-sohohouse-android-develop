package com.sohohouse.seven.home.houseboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.View
import androidx.annotation.Keep
import androidx.recyclerview.widget.ItemTouchHelper
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.book.eventdetails.EventDetailsActivity
import com.sohohouse.seven.book.table.booked.BookedTableDetailsActivity
import com.sohohouse.seven.book.table.model.BookedTable
import com.sohohouse.seven.common.adapterhelpers.StackItemAnimator
import com.sohohouse.seven.common.adapterhelpers.StackItemAnimator.Companion.STACK_OFFSET_FACTOR
import com.sohohouse.seven.common.adapterhelpers.StackItemDecoration
import com.sohohouse.seven.common.adapterhelpers.StickyHeaderGestureDetector
import com.sohohouse.seven.common.apihelpers.SohoWebHelper
import com.sohohouse.seven.common.deeplink.DeeplinkBuilder
import com.sohohouse.seven.common.extensions.startActivitySafely
import com.sohohouse.seven.common.navigation.NavigationScreen
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.inappnotification.InAppNotification
import com.sohohouse.seven.common.views.inappnotification.InAppNotificationImpl
import com.sohohouse.seven.common.views.webview.WebViewBottomSheetFragment
import com.sohohouse.seven.databinding.FragmentHouseBoardBinding
import com.sohohouse.seven.guests.NewGuestListActivity
import com.sohohouse.seven.home.houseboard.items.NavigationRowItem
import com.sohohouse.seven.home.houseboard.items.NotificationItem
import com.sohohouse.seven.home.houseboard.renderers.*
import com.sohohouse.seven.home.houseboard.viewmodels.HouseBoardViewModel
import com.sohohouse.seven.more.SettingsActivity
import com.sohohouse.seven.more.bookings.MyBookingsActivity
import com.sohohouse.seven.more.bookings.recycler.EventBookingAdapterItem
import com.sohohouse.seven.more.bookings.recycler.TableBookingAdapterItem
import com.sohohouse.seven.more.membershipdetails.Mode
import com.sohohouse.seven.more.membershipdetails.MoreMembershipDetailsActivity
import com.sohohouse.seven.network.core.models.RoomBooking
import com.sohohouse.seven.network.core.models.Venue

@Keep
class HouseBoardFragment : BaseMVVMFragment<HouseBoardViewModel>(),
    UpcomingBookingsListener,
    InAppNotification by InAppNotificationImpl(),
    Loadable.View {

    val binding by viewBinding(FragmentHouseBoardBinding::bind)

    override val viewModelClass: Class<HouseBoardViewModel> = HouseBoardViewModel::class.java

    private fun onLocalHouseClick() {
        WebViewBottomSheetFragment.withKickoutType(
            type = SohoWebHelper.KickoutType.HOUSES,
            id = viewModel.localVenueSlug
        ).show(requireActivity().supportFragmentManager, WebViewBottomSheetFragment.TAG)
    }

    override val loadingView: LoadingView
        get() = binding.houseBoardLoadingView

    // BaseFragment
    override val contentLayoutId: Int
        get() = R.layout.fragment_house_board

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            setupRecyclerView()
            setupViewModel()
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.startPolling()
    }

    override fun onResume() {
        super.onResume()
        viewModel.load()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopPolling()
    }

    private val adapter
        get() = binding.houseBoardRecyclerView.adapter as HouseBoardAdapter

    private fun FragmentHouseBoardBinding.setupRecyclerView() {
        val houseBoardAdapter = createAdapter()
        val stackItemDecoration = StackItemDecoration(houseBoardAdapter, STACK_OFFSET_FACTOR)

        // renders header and stack notifications when collapsed
        // handles swipe gestures when expanded
        val callback = SwipeCallback(
            requireContext(),
            houseBoardAdapter,
            ::onSwipeLeft,
            ::onSwipeRight,
            getString(R.string.clear_cta),
            getString(R.string.view_cta)
        )
        ItemTouchHelper(callback).attachToRecyclerView(houseBoardRecyclerView)

        // handles click event on sticky header
        val gestureDetector = StickyHeaderGestureDetector(stackItemDecoration)
        val touchListener = StickyHeaderRecyclerViewTouchListener(
            GestureDetector(
                requireContext(),
                gestureDetector
            )
        )
        houseBoardRecyclerView.addOnItemTouchListener(touchListener)
        houseBoardRecyclerView.addItemDecoration(stackItemDecoration)
        houseBoardRecyclerView.itemAnimator =
            StackItemAnimator(stackOffsetFactor = STACK_OFFSET_FACTOR)
        houseBoardRecyclerView.adapter = houseBoardAdapter

        viewModel.items.observe(viewLifecycleOwner) { items ->
            items.indexOf(items.firstOrNull { it is NotificationItem })
                .let { position ->
                    stackItemDecoration.stackPosition = position
                    (houseBoardRecyclerView.itemAnimator as? StackItemAnimator)
                        ?.stackPosition = position
                }
            adapter.setItems(items)
        }
    }

    private fun FragmentHouseBoardBinding.setupViewModel() {
        viewModel.loadingState.observe(viewLifecycleOwner) {
            houseBoardRecyclerView.visibility =
                if (it is LoadingState.Idle) View.VISIBLE else View.GONE
            houseBoardLoadingView.visibility =
                if (it is LoadingState.Loading) View.VISIBLE else View.GONE
        }
        viewModel.notifications.observe(viewLifecycleOwner) {
            viewModel.onNewNotificationItems()
        }
        viewModel.invitedChannelsLiveData.observe(viewLifecycleOwner) {
            viewModel.onNewNotificationItems()
        }

        viewModel.notificationBadge.observe(viewLifecycleOwner) {
            houseBoardRecyclerView.scrollToPosition(0)
        }
        viewModel.openCurrentHouseEvent.observe(viewLifecycleOwner) { venue ->
            venue?.let {
                viewModel.trackHouseClick()
                navigateToHouseWebView(it)
            }
        }
        viewModel.intent.observe(viewLifecycleOwner) { startActivity(it) }
        viewModel.openTableBooking.observe(viewLifecycleOwner, { openTableBooking(it) })
        observeLoadingState(this@HouseBoardFragment)
    }

    private fun createAdapter(): HouseBoardAdapter {
        return HouseBoardAdapter(::collapseNotifications, ::openNotificationMenu).apply {
            registerRenderers(
                UpcomingBookingsContainerRenderer(this@HouseBoardFragment),
                GreyButtonRenderer(::onButtonItemClick),
                DarkButtonRenderer(::onButtonItemClick),
                SecondaryButtonRenderer(::onButtonItemClick),
                HoursDisplayRenderer(::onHoursDisplayItemButtonClick, ::onLocalHouseClick),
                NavigationRowRenderer(::onNavigationRowItemClick),
                LargeNavigationRowRender(),
                NotificationRenderer {
                    viewModel.logNotificationTapView()
                    onNotificationClick(it)
                },
                InviteGuestsRenderer { onInviteGuestClick() },
                MembershipCardRenderer()
            )
        }
    }

    private fun onSwipeLeft(item: NotificationItem, position: Int) {
        if (adapter.notificationItemCount <= 2)
            binding.houseBoardRecyclerView.expanded = false
        viewModel.logNotificationTapClear()
        viewModel.patchNotification(notification = item, dismissed = true)
    }

    private fun onSwipeRight(item: NotificationItem, position: Int) {
        viewModel.logNotificationSwipeView()
        onNotificationClick(item)
        adapter.notifyItemChanged(position)
    }

    private fun navigateToHouseWebView(venue: Venue) {
        if (venue.slug.isNotEmpty()) {
            WebViewBottomSheetFragment.withKickoutType(
                type = SohoWebHelper.KickoutType.HOUSES,
                id = venue.slug
            ).show(requireActivity().supportFragmentManager, WebViewBottomSheetFragment.TAG)
        }
    }

    private fun navigateToMembershipCardScreen() {
        viewModel.logGoToMembershipCard()
        startActivity(MoreMembershipDetailsActivity.getIntent(requireContext(), Mode.CARD_ONLY))
    }

    private fun loadURLinWebView(
        kickoutType: SohoWebHelper.KickoutType,
        headerRes: Int,
        id: String?
    ) {
        WebViewBottomSheetFragment.withKickoutType(type = kickoutType, id = id)
            .show(requireActivity().supportFragmentManager, WebViewBottomSheetFragment.TAG)
    }

    private fun onButtonItemClick(action: String?) {
        when (action) {
            HouseBoardViewModel.BOOK_BEDROOM_ACTION -> loadURLinWebView(
                SohoWebHelper.KickoutType.BOOK_HOTEL,
                R.string.stay_webview_header,
                null
            )
            HouseBoardViewModel.VIEW_MEMBERSHIP_CARD_ACTION -> navigateToMembershipCardScreen()
            HouseBoardViewModel.VIEW_ALL_BENEFITS_ACTION -> navigateToBenefitsScreen()
            HouseBoardViewModel.BOOK_CINEMA_TICKETS_ACTION -> navigateToCinemaTicketsScreen()
        }
    }

    private fun navigateToCinemaTicketsScreen() {
        WebViewBottomSheetFragment.withKickoutType(SohoWebHelper.KickoutType.ELECTRIC_CINEMA)
            .show(requireActivity().supportFragmentManager, WebViewBottomSheetFragment.TAG)
    }

    private fun navigateToBenefitsScreen() {
        val uri = DeeplinkBuilder.buildUri(NavigationScreen.DISCOVER_PERKS)
        requireActivity().startActivitySafely(Intent(Intent.ACTION_VIEW, uri))
    }

    private fun onHoursDisplayItemButtonClick() {
        navigateToMembershipCardScreen()
    }

    private fun onNavigationRowItemClick(item: NavigationRowItem) {
        when (item.text) {
            getString(R.string.house_rules_cta) -> {
                loadURLinWebView(
                    SohoWebHelper.KickoutType.HOUSE_RULES,
                    R.string.house_rules_webview_header,
                    viewModel.localVenue.value?.slug
                )
            }
        }
    }

    private fun expandList(expand: Boolean) {
        viewModel.logExpandNotifications()
        viewModel.dismissNotificationBadge()
        binding.houseBoardRecyclerView.scrollToPosition(0)
        binding.houseBoardRecyclerView.expanded = expand
    }

    private fun collapseNotifications() {
        viewModel.logCollapseNotifications()
        expandList(false)
    }

    private fun openNotificationMenu() {
        val dialog = NotificationOptionsDialogFragment()
        dialog.setTargetFragment(
            this,
            NotificationOptionsDialogFragment.REQUEST_NOTIFICATION_OPTIONS
        )
        dialog.show(parentFragmentManager, NotificationOptionsDialogFragment.TAG)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (NotificationOptionsDialogFragment.REQUEST_NOTIFICATION_OPTIONS == requestCode
            && Activity.RESULT_OK == resultCode
        ) {
            when (data?.getIntExtra(NotificationOptionsDialogFragment.BUNDLE_KEY, -1)) {
                NotificationOptionsDialogFragment.RESULT_GO_TO_SETTIGNS -> {
                    val intent = Intent(requireContext(), SettingsActivity::class.java)
                    startActivity(intent)
                }
                NotificationOptionsDialogFragment.RESULT_CLEAR_ALL -> {
                    viewModel.dismissAllNotifications()
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun onNotificationClick(item: NotificationItem) {
        if (adapter.notificationItemCount > 1 && adapter.expanded.not()) {
            expandList(true)
            return
        }

        viewModel.onNotificationClick(item)
        return
    }

    private fun onInviteGuestClick() {
        viewModel.logClickInviteGuest()
        startActivity(NewGuestListActivity.getIntent(requireActivity()))
    }

    /**
     * UpcomingBookingsListener
     */
    override fun onRoomBookingClick(roomBooking: RoomBooking) {
        viewModel.logRoomBookingClick(roomBooking)
        WebViewBottomSheetFragment.withKickoutType(
            type = SohoWebHelper.KickoutType.ROOM_BOOKING,
            id = roomBooking.id
        ).show(requireActivity().supportFragmentManager, WebViewBottomSheetFragment.TAG)
    }

    override fun onEventBookingClick(item: EventBookingAdapterItem) {
        viewModel.logEventBookingClick(item.eventBooking)
        startActivity(
            EventDetailsActivity.getIntent(
                activity,
                item.eventBooking.event?.id,
                item.eventBooking.event?.images?.large
            )
        )
    }

    override fun onTableBookingClick(tableBooking: TableBookingAdapterItem) {
        viewModel.onTableBookingClick(tableBooking)
    }

    private fun openTableBooking(bookedTable: BookedTable) {
        startActivity(BookedTableDetailsActivity.newIntent(requireContext(), bookedTable.id))
    }

    override fun onSeeAllClick() {
        viewModel.logSeeAllUpcomingBookingsClick()
        startActivity(Intent(requireContext(), MyBookingsActivity::class.java))
    }

}
