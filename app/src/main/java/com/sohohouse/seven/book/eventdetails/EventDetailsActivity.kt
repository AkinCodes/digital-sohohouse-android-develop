package com.sohohouse.seven.book.eventdetails

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.isVisible
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.sohohouse.seven.App
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseViewControllerActivity
import com.sohohouse.seven.base.error.ErrorHelper
import com.sohohouse.seven.book.eventdetails.bookingsuccess.BookingSuccessActivity
import com.sohohouse.seven.book.eventdetails.bookingsuccess.BookingSuccessItem
import com.sohohouse.seven.book.eventdetails.payment.PaymentConfirmationActivity
import com.sohohouse.seven.book.eventdetails.renderer.*
import com.sohohouse.seven.branding.ThemeManager
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.behaviors.PullToRefreshBehavior
import com.sohohouse.seven.common.dagger.Injectable
import com.sohohouse.seven.common.design.adapter.RendererAdapter
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.navigation.IntentUtils
import com.sohohouse.seven.common.utils.imageloader.ImageLoader
import com.sohohouse.seven.common.views.*
import com.sohohouse.seven.common.views.eventdetaillist.*
import com.sohohouse.seven.databinding.ActivityEventDetailsBinding
import com.sohohouse.seven.main.MainActivity
import com.sohohouse.seven.membership.ActiveMembershipInfoActivity
import com.sohohouse.seven.shake.MembershipCardShakeListener
import javax.inject.Inject


class EventDetailsActivity : BaseViewControllerActivity<EventDetailsPresenter>(),
    EventDetailsViewController,
    MembershipCardShakeListener,
    DialogInterface.OnDismissListener,
    Injectable {

    companion object {
        const val REQUEST_CODE_SYSTEM_NOTIFICATION = 106
        const val REQUEST_CODE_EVENT_DETAILS = 2976

        fun getIntent(
            context: Context?,
            eventId: String?,
            imagePath: String? = null,
            fromNotification: Boolean = false
        ): Intent {
            return Intent(context, EventDetailsActivity::class.java).apply {
                putExtra(BundleKeys.ID, eventId)
                putExtra(BundleKeys.IMAGE_URL, imagePath)
                if (fromNotification) {
                    putExtra(BundleKeys.FROM_NOTIFICATION_DIALOG, fromNotification)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }
        }
    }

    private val binding by viewBinding(ActivityEventDetailsBinding::bind)

    @Inject
    lateinit var themeManager: ThemeManager

    private var goBackToWhatsOn = false

    private val adapter = RendererAdapter<BaseEventDetailsAdapterItem>().apply {
        registerRenderers(
            AttributeWClickableTextRenderer(EventTicketsAdapterItem::class.java),
            AttributeWClickableTextRenderer(EventGuestAdapterItem::class.java),
            AttributeWClickableTextRenderer(EventTicketsAdapterItem::class.java),
            AttributeWClickableTextRenderer(EventDateAdapterItem::class.java),
            AttributeWClickableTextRenderer(HouseDetailsAdapterItem::class.java),
            AttributeWClickableTextRenderer(AddressAdapterItem::class.java),
            DescriptionAttributeRenderer(EventDepositPolicyAdapterItem::class.java),
            DescriptionAttributeRenderer(EventDescriptionAdapterItem::class.java),
            DescriptionAttributeRenderer(EventCancellationAdapterItem::class.java),
            EventExternalLinkRenderer(::onUrlClicked),
            EventOverviewRenderer(::leaveGuestWaitList),
            GuestRecyclerviewRenderer(),
            MembershipRenderer(),
            SubDescriptionAttributeRenderer()
        )
    }

    private var playerView: PlayerView? = null

    private val player by lazy {
        SimpleExoPlayer.Builder(this).build().apply {
            playWhenReady = false
            seekTo(0, 0)
            setPlaybackParameters(PlaybackParameters(1f))
            addListener(object : Player.EventListener {
                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_BUFFERING -> binding.progressBar.setVisible()
                        else -> binding.progressBar.setGone()
                    }
                }
            })
            prepare()
        }
    }


    override fun setBrandingTheme() {
        setTheme(themeManager.lightTheme)
    }

    override fun getContentLayout(): Int = R.layout.activity_event_details

    override fun createPresenter(): EventDetailsPresenter {
        return App.appComponent.eventDetailsPresenter.apply {
            eventId = getEventId() ?: let { finish(); return@apply }
            goBackToWhatsOn = intent.getBooleanExtra(BundleKeys.FROM_NOTIFICATION_DIALOG, false)
        }
    }

    private fun getEventId(): String? {
        return intent.getStringExtra(BundleKeys.ID) ?: intent.data?.getQueryParameter(BundleKeys.ID)
        ?: intent.data?.pathSegments?.last()
    }

    override fun setupScreenName(eventType: EventType) {
        when (eventType) {
            EventType.MEMBER_EVENT -> setScreenName(AnalyticsManager.Screens.EventDetails.name)
            EventType.CINEMA_EVENT -> setScreenName(AnalyticsManager.Screens.ScreeningDetails.name)
            EventType.FITNESS_EVENT -> setScreenName(AnalyticsManager.Screens.FitnessDetails.name)
            EventType.HOUSE_VISIT -> setScreenName(AnalyticsManager.Screens.HouseVisit.name)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportPostponeEnterTransition()
    }

    override fun launchAddToCalendarIntent(
        name: String,
        address: String,
        startsAt: Long,
        endsAt: Long
    ) {
        startActivity(Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, name)
            putExtra(CalendarContract.Events.EVENT_LOCATION, address)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startsAt)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endsAt)
        })
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        val largeImage = intent.getStringExtra(BundleKeys.IMAGE_URL)
        with(binding) {


            if (largeImage?.isNotEmpty() == true) {
                App.appComponent.imageLoader.load(largeImage, isFade = false)
                    .apply { placeholder = R.drawable.placeholder }
                    .into(eventImage, object : ImageLoader.Callback {
                        override fun onSuccess() {
                            supportStartPostponedEnterTransition()
                        }

                        override fun onError() {
                            supportStartPostponedEnterTransition()
                        }
                    })
            } else {
                eventImage.setImageResource(R.drawable.placeholder)
                supportStartPostponedEnterTransition()
            }

            backButton.setOnClickListener { onBackPressed() }

            swipeRefreshLayout.applyColorScheme()
            swipeRefreshLayout.setOnRefreshListener(::onRefresh)

            val layoutParams = recyclerView.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = layoutParams.behavior as PullToRefreshBehavior
            behavior.dependencyId = R.id.image_container
            swipeRefreshLayout.setOnChildScrollUpCallback { _, _ -> behavior.topOffset.toFloat() != recyclerView.translationY }
            recyclerView.adapter = adapter

            // temporary fix to refresh the information, full screen loading, will switch to inline later with prefilled data
//        presenter.setUp(intent.getSerializableExtra(EVENT_TYPE_KEY) as EventTypeHelper, event)
        }
        setupPlayerView()
        setupFullScreenButton()

        presenter.fetchData(true)
    }

    override fun onStart() {
        super.onStart()
        player.playWhenReady = playerView?.isVisible == true
    }

    override fun onStop() {
        player.playWhenReady = false
        super.onStop()
    }

    override fun onDestroy() {
        player.stop()
        player.release()
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (goBackToWhatsOn) MainActivity.startClean(this, R.id.menu_book) else onBackPressed()
            return true
        }
        return false
    }

    // region PullToRefreshViewController
    override fun getSwipeRefreshLayout(): SwipeRefreshLayout = binding.swipeRefreshLayout

    override val loadingView: LoadingView
        get() = binding.activityEventDetailsLoadingView
    //endregion

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PaymentConfirmationActivity.PAYMENT_CONFIRMATION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val ticketCount =
                data?.getIntExtra(PaymentConfirmationActivity.PAYMENT_CONFIRMATION_TICKET_COUNT, 0)
                    ?: 0
            val state = data?.getStringExtra(PaymentConfirmationActivity.PAYMENT_CONFIRMATION_STATE)
                .orEmpty()
            presenter.showSuccessView(ticketCount, state)
            presenter.fetchData()
        } else if (requestCode == REQUEST_CODE_SYSTEM_NOTIFICATION) {

            if (NotificationManagerCompat.from(context).areNotificationsEnabled())
                onSetReminderButtonClicked()
            else {
                showRemindMeView(RemindMeButtonStatus.SET_REMINDER)
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        val dialogFragment =
            supportFragmentManager.findFragmentByTag(VideoPlayerDialogFragment.TAG) ?: return

        playerView = (dialogFragment as VideoPlayerDialogFragment).playerView
        (playerView?.parent as ViewGroup).removeView(playerView)

        playerView?.layoutParams =
            ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 0).apply {
                topToTop = R.id.event_image
                bottomToBottom = R.id.event_image
            }

        binding.imageContainer.addView(playerView)
        setupFullScreenButton()
    }

    override fun setVideoUrl(url: String?) {
        url?.let { player.setMediaItem(MediaItem.fromUri(it)) }
        showEventBanner(url)
    }

    private fun onRefresh() {
        with(binding) {
            showAnimated(eventImage)
            hideAnimated(this@EventDetailsActivity.playerView, eventBanner, playIcon, joinEvent)
            player.stop()

            presenter.fetchData(true)
        }
    }

    private fun setupPlayerView() =
        with(binding) {
            this@EventDetailsActivity.playerView = playerView
            this@EventDetailsActivity.playerView?.player = player

            playIcon.setOnClickListener {
                hideAnimated(binding.playIcon, joinEvent, eventImage)
                showAnimated(playerView)
                this@EventDetailsActivity.playerView?.useController = true
                player.play()
            }
        }


    private fun setupFullScreenButton() {
        playerView?.findViewById<ImageButton>(R.id.exo_fullscreen_button)?.apply {
            this.setImageResource(R.drawable.ic_fullscreen)
            this.setOnClickListener {
                supportFragmentManager.findFragmentByTag(VideoPlayerDialogFragment.TAG)?.let {
                    it.dismiss()
                    return@setOnClickListener
                }
                binding.imageContainer.removeView(playerView)
                VideoPlayerDialogFragment().also { it.playerView = playerView }
                    .show(supportFragmentManager, VideoPlayerDialogFragment.TAG)
            }
        }
    }

    private fun showEventBanner(videoLink: String?) {
        when {
            presenter.isDigitalEvent().not() -> return
            presenter.isPastEvent() && videoLink.isNullOrEmpty() -> return
            (presenter.isEventLiveNow() || presenter.isPastEvent()) && videoLink?.isNotEmpty() == true -> showAnimated(
                binding.playIcon,
                binding.joinEvent
            )
            presenter.isStartingSoon() -> showAnimated(binding.eventBanner)
        }
    }

    private fun showAnimated(vararg views: View?) {
        views.forEach { it?.showAnimated() }
    }

    private fun hideAnimated(vararg views: View?) {
        views.forEach { it?.hideAnimated() }
    }

    //getIntent EventDetailsViewController
    override fun showEventDetails(data: List<BaseEventDetailsAdapterItem>) {
        adapter.submitItems(data)
    }

    private fun onUrlClicked(url: String) {
        startActivitySafely(IntentUtils.openUrlIntent(url))
    }

    override fun onAddressClicked(address: String) {
        startActivitySafely(IntentUtils.viewOnMapIntent(address))
    }

    override fun showBookingStepper(config: StepperPresenter.Config) {
        binding.stepper.setVisible()
        val listener = object : JoinEventListener {
            override fun joinEvent(tickets: Int) {
                if (config.resString == R.string.explore_events_event_add_to_booking_cta) {

                }
                when (config.resString) {
                    R.string.explore_cinema_event_buy_tickets_cta -> presenter.logJoinLottery()
                    R.string.explore_events_event_waiting_cta -> presenter.logJoinWaitingList()
                    R.string.explore_events_event_add_to_booking_cta -> presenter.logAddToBookings()
                }
                val numberOfTickets = if (config.invitingGuest) {
                    tickets
                } else {
                    tickets - 1
                }
                presenter.bookEvent(numberOfTickets)
            }

            override fun onMoreTicketsClick() {
                presenter.logMoreTicketsClick()
            }

            override fun onLessTicketsClick() {
                presenter.logLessTicketsClick()
            }
        }
        if (config.maxAvailableTickets > 1 || config.invitingGuest) {
            binding.stepper.setUp(0, config.maxAvailableTickets, config.resString, listener)
        } else {
            binding.stepper.setUp(config.resString, listener)
        }
    }

    override fun hideBookingStepper() {
        binding.stepper.setGone()
    }

    override fun showRemindMeView(status: RemindMeButtonStatus) {
        with(binding) {
            remindMeView.visibility = View.VISIBLE
            remindMeView.bind(status, this@EventDetailsActivity)
        }
    }

    override fun hideRemindMeView() {
        binding.remindMeView.visibility = View.GONE
    }

    override fun showBookingSuccess(bookingSuccessItem: BookingSuccessItem) {
        val intent = Intent(this, BookingSuccessActivity::class.java)
        intent.putExtra(BookingSuccessActivity.BOOKING_SUCCESS_ITEM, bookingSuccessItem)
        startActivity(intent)
    }

    override fun showConfirmationButton(onConfirmed: () -> Unit) =
        with(binding) {
            confirmBooking.isVisible = true
            stepper.isVisible = false
            confirmBooking.setOnClickListener {
                ConfirmBookingBottomSheet().show(
                    supportFragmentManager,
                    ConfirmBookingBottomSheet.TAG
                )
                supportFragmentManager.setFragmentResultListener(
                    ConfirmBookingBottomSheet.ON_CONFIRM,
                    this@EventDetailsActivity
                ) { s: String, _: Bundle ->
                    if (s == ConfirmBookingBottomSheet.ON_CONFIRM) {
                        onConfirmed()
                    }
                }
            }
        }

    override fun getPaymentMethod(
        eventId: String, eventName: String, eventType: String, priceCents: Int,
        currency: String?, ticketCount: Int, newTickets: Int, bookingId: String?
    ) {
        val intent = PaymentConfirmationActivity.getIntent(
            context = this,
            eventId = eventId,
            eventName = eventName,
            eventType = eventType,
            priceCents = priceCents,
            currency = currency,
            newTickets = newTickets,
            ticketCount = ticketCount,
            bookingId = bookingId
        )
        startActivityForResult(
            intent,
            PaymentConfirmationActivity.PAYMENT_CONFIRMATION_REQUEST_CODE
        )
    }

    override fun showDeleteDialogue(newGuestCount: Int) {
        CustomDialogFactory.createThemedAlertDialog(this,
            getString(R.string.explore_events_event_remove_guest_confirm_header),
            getString(R.string.explore_events_event_remove_guest_confirm_supporting),
            getString(R.string.explore_events_event_remove_guest_confirm_confirm_cta),
            getString(R.string.explore_events_event_remove_guest_confirm_cancel_cta),
            DialogInterface.OnClickListener { _, _ -> presenter.deleteGuest(newGuestCount) })
            .show()
    }

    override fun showBookingError() {
        CustomDialogFactory.createThemedAlertDialog(
            this,
            getString(R.string.general_error_header),
            getString(R.string.general_error_supporting),
            getString(R.string.general_error_ok_cta)
        ).show()
    }

    override fun showBookingErrorWithMessage(message: String) {
        CustomDialogFactory.createThemedAlertDialog(
            this,
            getString(R.string.general_error_header),
            message,
            getString(R.string.general_error_ok_cta)
        ).show()
    }

    //endregion

    //region RemindMeListener
    override fun onSetReminderButtonClicked() {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled())
            launchNotificationSettingsActivity()
        else {
            showRemindMeView(RemindMeButtonStatus.IS_LOADING)
            presenter.addReminder()
        }
    }

    override fun onDeleteReminderButtonClicked() {
        showRemindMeView(RemindMeButtonStatus.IS_LOADING)
        presenter.deleteReminder()
    }
    //endregion

    //region EventDetailAdapterListener
    private fun leaveGuestWaitList(isWaitList: Boolean, isLottery: Boolean, isTicketless: Boolean) {
        when {
            isLottery -> CustomDialogFactory.createThemedAlertDialog(this,
                getString(R.string.explore_events_event_leave_lottery_confirm_header),
                getString(R.string.explore_events_event_leave_waiting_confirm_supporting),
                getString(R.string.explore_events_event_leave_waiting_confirm_confirm_cta),
                getString(R.string.explore_events_event_leave_waiting_confirm_cancel_cta),
                DialogInterface.OnClickListener { _, _ ->
                    presenter.deleteBooking()
                })
                .show()
            isTicketless -> CustomDialogFactory.createThemedAlertDialog(this,
                getString(R.string.explore_events_event_cancel_ticketless_header),
                getString(R.string.explore_events_event_cancel_ticketless_supporting),
                getString(R.string.explore_events_event_cancel_ticketless_confirm_cta),
                getString(R.string.explore_events_event_cancel_ticketless_cancel_cta),
                DialogInterface.OnClickListener { _, _ ->
                    presenter.logCancelTicketless()
                    presenter.deleteBooking()
                })
                .show()
            isWaitList -> CustomDialogFactory.createThemedAlertDialog(this,
                getString(R.string.explore_events_event_leave_waiting_confirm_header),
                getString(R.string.explore_events_event_leave_waiting_confirm_supporting),
                getString(R.string.explore_events_event_leave_waiting_confirm_confirm_cta),
                getString(R.string.explore_events_event_leave_waiting_confirm_cancel_cta),
                DialogInterface.OnClickListener { _, _ ->
                    presenter.onUserClickCancelBooking()
                    presenter.deleteBooking()
                })
                .show()
            else -> CustomDialogFactory.createThemedAlertDialog(this,
                getString(R.string.explore_events_event_cancel_booking_confirm_header),
                getString(R.string.explore_events_event_cancel_booking_confirm_supporting),
                getString(R.string.explore_events_event_cancel_booking_confirm_confirm_cta),
                getString(R.string.explore_events_event_cancel_booking_confirm_cancel_cta),
                DialogInterface.OnClickListener { _, _ ->
                    presenter.onUserClickCancelBooking()
                    presenter.deleteBooking()
                })
                .show()
        }
    }

    override fun showActiveMembershipInfo(eventId: String, eventName: String, eventType: String) {
        startActivity(ActiveMembershipInfoActivity.getIntent(this, eventId, eventName, eventType))
    }
    //endregion

    override fun finish() {
        presenter.shouldRefreshOnResult {
            val intent = Intent()
            intent.putExtra(BundleKeys.EVENT, it)
            setResult(Activity.RESULT_OK, intent)
        }
        binding.recyclerView.adapter = null
        super.finish()
    }

    //region Error
    override fun getErrorStateView(): ReloadableErrorStateView = binding.errorState

    override fun showGenericErrorDialog(errorCodes: Array<out String>) {
        val messageRes = ErrorHelper.errorCodeMap.get(errorCodes.firstOrNull())
            ?: R.string.payment_methods_error_supporting
        CustomDialogFactory.createThemedAlertDialog(
            this,
            getString(R.string.payment_methods_error_header),
            getString(messageRes),
            getString(R.string.payment_methods_ok_cta)
        ).show()
    }
    //endregion

    private fun launchNotificationSettingsActivity() {
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context?.packageName)
        } else {
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("app_package", context?.packageName)
            intent.putExtra("app_uid", context?.applicationInfo?.uid)
        }
        startActivityForResult(intent, REQUEST_CODE_SYSTEM_NOTIFICATION)
    }

    override fun onBackPressed() {
        if (goBackToWhatsOn) {
            MainActivity.startClean(this, R.id.menu_book)
        } else {
            // force visibility back to ensure the return animation
            binding.eventImage.alpha = 1.0f
            binding.eventImage.setVisible()

            super.onBackPressed()
        }
    }
}
