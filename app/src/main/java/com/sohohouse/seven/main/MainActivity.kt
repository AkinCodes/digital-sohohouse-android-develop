package com.sohohouse.seven.main

import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.StateListDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sohohouse.seven.R
import com.sohohouse.seven.base.error.ErrorDialogHelper
import com.sohohouse.seven.base.filter.FilterType
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.book.BookFragment
import com.sohohouse.seven.book.BookFragment.Companion.EXPLORE_NOTIFICATION_EVENT_ID
import com.sohohouse.seven.book.BookTab
import com.sohohouse.seven.book.filter.BookFilterActivity
import com.sohohouse.seven.book.filter.BookFilterManager
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.Scrollable
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.analytics.NavigationParams
import com.sohohouse.seven.common.behaviors.BottomSheetBehavior
import com.sohohouse.seven.common.deeplink.DeeplinkBuilder
import com.sohohouse.seven.common.deeplink.DeeplinkBuilder.APPS_SCHEME
import com.sohohouse.seven.common.deeplink.DeeplinkBuilder.APP_AUTHORITY
import com.sohohouse.seven.common.deeplink.DeeplinkBuilder.AUTHORITY_PROFILE
import com.sohohouse.seven.common.deeplink.DeeplinkBuilder.HTTPS_SCHEME
import com.sohohouse.seven.common.deeplink.DeeplinkBuilder.PATH_MY_PROFILE
import com.sohohouse.seven.common.deeplink.DeeplinkBuilder.PATH_PROFILE
import com.sohohouse.seven.common.drawable.TextDrawable
import com.sohohouse.seven.common.extensions.startActivitySafely
import com.sohohouse.seven.common.navigation.IntentUtils
import com.sohohouse.seven.common.navigation.NavigationScreen
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.Backable
import com.sohohouse.seven.common.utils.imageloader.ImageLoader
import com.sohohouse.seven.common.utils.collectLatest
import com.sohohouse.seven.common.views.EventType
import com.sohohouse.seven.common.views.bottomnavigation.BottomNavigationView
import com.sohohouse.seven.common.views.bottomnavigation.NavigationItem
import com.sohohouse.seven.common.views.inappnotification.InAppNotificationAdapterItem
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.common.views.showSafe
import com.sohohouse.seven.connect.trafficlights.firstvisit.TrafficLightFirstVisitBottomSheet
import com.sohohouse.seven.databinding.ActivityMainBinding
import com.sohohouse.seven.fcm.SohoFCMService.Companion.KEY_EVENT_TYPE
import com.sohohouse.seven.fcm.SohoFCMService.Companion.REQUEST_PUSH_MSG
import com.sohohouse.seven.fcm.SohoFCMService.Companion.TYPE_PENDING_REGISTRATION
import com.sohohouse.seven.more.bookings.UpcomingBookingsFragment
import com.sohohouse.seven.profile.view.ProfileViewerFragment
import com.sohohouse.seven.shake.MembershipCardShakeListener
import dagger.android.HasAndroidInjector
import eightbitlab.com.blurview.RenderScriptBlur
import javax.inject.Inject

class MainActivity : BaseMVVMActivity<MainViewModel>(),
    HasAndroidInjector,
    MembershipCardShakeListener,
    MainNavigationController,
    UpcomingBookingsFragment.Listener {

    companion object {
        const val NOTIFICATION_ERROR = "notification_unknown_screen"

        const val SELECTED_TAB = "selectedTab"

        fun start(context: Context, tabId: Int) {
            val starter = Intent(context, MainActivity::class.java)
            starter.putExtra(SELECTED_TAB, tabId)
            context.startActivity(starter)
        }

        fun startClean(context: Context, tabId: Int) {
            val starter = getCleanIntent(context, tabId)
            context.startActivity(starter)
        }

        fun getCleanIntent(context: Context, tabId: Int = R.id.menu_home): Intent {
            val starter = Intent(context, MainActivity::class.java)
            starter.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            starter.putExtra(SELECTED_TAB, tabId)
            return starter
        }

        fun getIntent(context: Context, tabId: Int = R.id.menu_home): Intent {
            return Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(SELECTED_TAB, tabId)
            }
        }
    }

    @Inject
    lateinit var bookFilterManager: BookFilterManager

    @Inject
    lateinit var userManager: UserManager

    @Inject
    lateinit var imageLoader: ImageLoader

    override val viewModelClass: Class<MainViewModel> = MainViewModel::class.java

    val binding by viewBinding(ActivityMainBinding::bind)

    private val profileTarget: CustomTarget<Bitmap> by lazy {
        object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                binding.bottomNavigationView.setIcon(
                    R.id.menu_account,
                    BitmapDrawable(resources, resource)
                )
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                try {
                    binding.bottomNavigationView.setIcon(R.id.menu_account, placeholder)
                }catch (e: Exception){
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                binding.bottomNavigationView.setIcon(
                    R.id.menu_account,
                    ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_account)
                )
            }
        }
    }

    private lateinit var behavior: BottomSheetBehavior<*>

    private val colorNavSelected: Int by lazy {
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.colorNavigationItemSelected, typedValue, true)
        typedValue.data
    }

    private val colorNavUnselected: Int by lazy {
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.colorNavigationItemUnselected, typedValue, true)
        typedValue.data
    }

    private var overlayColor: Int = Color.BLACK

    private var animator: LoadingStateAnimator? = null

    private val mainFlowManager: MainFlowManager by lazy { MainFlowManager(supportFragmentManager) }

    init {
        lifecycleScope.launchWhenCreated {
            overlayColor =
                TypedValue().apply { theme.resolveAttribute(R.attr.colorLayer0, this, true) }.data
        }
        lifecycleScope.launchWhenResumed {
            viewModel.flush()
            // TODO improve me: fetch profile with is passed in first and return so that can open other profiles
            binding.coordinatorLayout.post { handleDeepLink(intent.data) }
        }
    }

    override fun getContentLayout(): Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainFlowManager.restoreSavedState(savedInstanceState)

        setupViews(savedInstanceState)
        setupViewModel()

        if (intent.getBooleanExtra(NOTIFICATION_ERROR, false)) {
            showInvalidDialog()
        }
        viewModel.setScreenName(name= AnalyticsManager.Screens.Home.name)
    }

    private fun showNewMessageIndication(show: Boolean) {
        binding.unreadMessageIndication.isVisible = show
    }

    override fun setBrandingTheme() {
        setTheme(themeManager.darkTheme)
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                when (it.getStringExtra(KEY_EVENT_TYPE)) {
                    TYPE_PENDING_REGISTRATION -> viewModel.registerTokenOnSendBird()
                    else -> {
                    }
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        viewModel.registerDateChangeReceiver(this)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadcastReceiver, IntentFilter(REQUEST_PUSH_MSG))
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetch()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mainFlowManager.saveState(outState)
    }

    override fun onStop() {
        super.onStop()
        viewModel.unregisterDateChangeReceiver(this)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }

    override fun onBackPressed() {
        if (behavior.isCollapsed()) {
            toggleHouseBoard()
            return
        }

        supportFragmentManager.findFragmentByTag(MainFlowManager.FRAG_BOOK)?.let {
            if ((it as? Backable)?.onBackPressed() == true) {
                return
            }
        }

        if (supportFragmentManager.findFragmentByTag(MainFlowManager.FRAG_HOME) == null) {
            loadFragment(MainFlowManager.FRAG_HOME)
            return
        }


        super.onBackPressed()
    }

    private val deeplinkPathSegments = listOf(
        DeeplinkBuilder.PATH_HOME,
        DeeplinkBuilder.PATH_EVENTS,
        DeeplinkBuilder.PATH_PLANNER,
        DeeplinkBuilder.PATH_DISCOVER,
        DeeplinkBuilder.PATH_HOME_ATTENDANCE_STATUS_UPDATE
    )

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val path = intent?.data?.pathSegments?.firstOrNull()
            ?.takeIf { deeplinkPathSegments.contains(it) } ?: return

        if (behavior.isCollapsed()) toggleHouseBoard()

        binding.bottomNavigationView.let {
            when (path) {
                DeeplinkBuilder.PATH_HOME -> it.selectedItemId = R.id.menu_home
                DeeplinkBuilder.PATH_EVENTS -> it.selectedItemId = R.id.menu_book
                DeeplinkBuilder.PATH_PLANNER -> it.selectedItemId = R.id.menu_discover
                DeeplinkBuilder.PATH_DISCOVER,
                DeeplinkBuilder.PATH_MEMBER_BENEFITS -> it.selectedItemId = R.id.menu_discover
                DeeplinkBuilder.PATH_HOME_ATTENDANCE_STATUS_UPDATE -> {
                    showAttendanceStatusUpdate()
                    it.selectedItemId = R.id.menu_home
                }
                else -> return
            }
        }
    }

    // MainNavigationController
    override fun selectExploreTab(bookTab: BookTab) {
        supportFragmentManager.setFragmentResult(
            BookFragment.SELECTED_BOOK_TAB_REQ_KEY,
            bundleOf(BookFragment.BOOK_TAB_TAG to bookTab)
        )
        binding.bottomNavigationView.selectedItemId = R.id.menu_book
    }

    override fun selectDiscoverTab() {
        binding.bottomNavigationView.selectedItemId = R.id.menu_discover
    }

    private fun setupViewModel() {
        viewModel.headerData.observe(this) { headerDataChanged(it) }
        viewModel.profileImage.observe(this) { updateProfileImage(it) }
        viewModel.notificationDialog.observe(this) { showNotificationAlertDialog() }
        viewModel.notificationBadge.observe(this) { show ->
            binding.homeHeader.showBadge(show)
        }
        viewModel.deeplink.observe(this) {
            when (it) {
                is Intent? -> {
                    if (it?.data?.authority != AUTHORITY_PROFILE) deepLinkSafely(it)
                }
                is InAppNotificationAdapterItem -> {
                    showInAppNotification(this, item = it, primaryClickListener = {
                        //TODO do not assume events screen
                        deepLinkSafely(IntentUtils.from(NavigationScreen.EVENTS))
                    })
                }
            }
        }
        viewModel.dayOfMonth.observe(this) { dayOfMonth ->
            binding.bottomNavigationView.setIcon(
                R.id.menu_book, LayerDrawable(
                    arrayOf(
                        ContextCompat.getDrawable(this, R.drawable.ic_book),
                        StateListDrawable().apply {
                            addState(
                                intArrayOf(android.R.attr.state_selected),
                                TextDrawable(
                                    context = this@MainActivity,
                                    text = dayOfMonth.toString(),
                                    color = colorNavSelected
                                )
                            )
                            addState(
                                intArrayOf(),
                                TextDrawable(
                                    context = this@MainActivity,
                                    text = dayOfMonth.toString(),
                                    color = colorNavUnselected
                                )
                            )
                        }.mutate()
                    )
                )
            )
        }
        viewModel.unReadMessages.observe(this) {
            showNewMessageIndication(it)
        }

        viewModel.sharedProfile.collectLatest(this) {
            it?.let { openProfileDialog(it) }
        }

        viewModel.sharedProfileState.collectLatest(this) {
            ErrorDialogHelper.showGenericErrorDialog(this@MainActivity, it)
        }
    }

    private fun showAttendanceStatusUpdate() {
        TrafficLightFirstVisitBottomSheet()
            .show(supportFragmentManager, TrafficLightFirstVisitBottomSheet.TAG)
    }

    private fun headerDataChanged(headerData: HeaderData) {
        displayHeader(headerData.title, headerData.subtitle, headerData.imageUrl, headerData.emoji)
    }

    private fun handleDeepLink(uri: Uri?) {
        uri?.let {
            if (it.scheme == APPS_SCHEME &&
                it.authority == APP_AUTHORITY &&
                it.pathSegments.firstOrNull() == PATH_PROFILE &&
                it.pathSegments.lastOrNull() == PATH_MY_PROFILE
            ) {
                openProfileDialog(viewModel.profileItem)
            } else if (it.scheme == HTTPS_SCHEME &&
                it.authority == AUTHORITY_PROFILE
            ) {
                viewModel.loadProfile(uri.toString())
            }
        }
    }

    private fun openProfileDialog(profile: ProfileItem) {
        ProfileViewerFragment.withProfile(
            profile = profile,
            skipCollapsed = true
        ).showSafe(supportFragmentManager, ProfileViewerFragment.TAG)
    }

    private fun deepLinkSafely(intent: Intent?) {
        if (intent == null) return

        // Try to open the activities within the app
        packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            .firstOrNull { info -> packageName == info.activityInfo.packageName }
            ?.let { info ->
                val newIntent = Intent(Intent.ACTION_MAIN).apply {
                    component = ComponentName(
                        info.activityInfo.applicationInfo.packageName,
                        info.activityInfo.name
                    )
                    data = intent.data
                    putExtras(intent.extras ?: return@apply)
                }
                startActivitySafely(newIntent)
                return
            }

        // if not found, let the system deal with it
        startActivitySafely(intent)
    }

    private fun setupViews(savedInstanceState: Bundle?) {
        bookFilterManager.resetAllFiltersToDefaultSelection()

        applyRoundedCornersToContent()
        setupBottomSheetBehavior()
        setupBlurView()
        setupBottomNavigationView(savedInstanceState)

        binding.homeHeader.getHeaderContentView().setOnClickListener {
            when (behavior.state) {
                BottomSheetBehavior.STATE_SETTLING,
                BottomSheetBehavior.STATE_DRAGGING -> return@setOnClickListener
            }
            toggleHouseBoard()
        }
    }

    private fun applyRoundedCornersToContent() {
        with(binding.fragmentPlaceholder) {
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    val radius =
                        resources.getDimensionPixelOffset(R.dimen.home_content_corner_radius)
                    outline.setRoundRect(0, 0, view.width, (view.height + radius), radius.toFloat())
                }
            }
            clipToOutline = true
        }
    }

    private fun setupBlurView() {
        with(binding) {
            blurView.setupWith(coordinatorLayout)
                .setFrameClearDrawable(coordinatorLayout.background)
                .setBlurAlgorithm(RenderScriptBlur(this@MainActivity))
                .setBlurRadius(10f)
                .setHasFixedTransformationMatrix(true)
        }
    }

    private fun setupBottomSheetBehavior() {
        behavior = BottomSheetBehavior.from(binding.contentView).apply {
            isHideable = false
            skipCollapsed = true
            isFitToContents = false
            state = BottomSheetBehavior.STATE_HALF_EXPANDED

            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HALF_EXPANDED,
                        BottomSheetBehavior.STATE_EXPANDED -> onBottomSheetExpanded()
                        BottomSheetBehavior.STATE_COLLAPSED -> onBottomSheetCollapsed()
                    }
                }
            })
        }
    }

    private fun toggleHouseBoard() {
        binding.homeHeader.toggleMenu()
        toggleBottomSheet()
        behavior.toggle()
    }

    private fun onBottomSheetExpanded() {
        behavior.isDraggable = true
        binding.homeHeader.showOpenButton()
    }

    private fun onBottomSheetCollapsed() {
        behavior.isDraggable = false
        viewModel.onHouseBoardViewed()
        binding.homeHeader.showCloseButton()
    }

    private fun toggleBottomSheet() {
        val translationY =
            if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED) 0f else binding.bottomNavigationContainer.height.toFloat()

        with(binding.bottomNavigationContainer.animate()) {
            translationY(translationY)
            duration = 250
            start()
        }
    }

    private fun setupBottomNavigationView(savedInstanceState: Bundle?) {
        with(binding.bottomNavigationView) {
            inflateMenu(viewModel.bottomNavMenu)
            setNavigationItemListener(object : BottomNavigationView.NavigationItemListener {
                override fun onNavigationItemReselected(item: NavigationItem, pos: Int) {
                    if (behavior.isHalfExpanded().not()) {
                        behavior.expandHalf()
                        return
                    }

                    supportFragmentManager.findFragmentById(R.id.fragment_placeholder)
                        ?.takeIf { it is Scrollable }
                        ?.let { (it as Scrollable).scrollToPosition(0) }
                }

                override fun onNavigationItemSelected(item: NavigationItem, pos: Int) {
                    if (binding.contentViewLoadingView.visibility != View.VISIBLE) {
                        animateLoadingState(true)
                    }

                    when (item.id) {
                        R.id.menu_home -> {
                            loadFragment(MainFlowManager.FRAG_HOME)
                            viewModel.trackTabSelected(NavigationParams.Tab.HOME)
                        }
                        R.id.menu_book -> {
                            loadFragment(MainFlowManager.FRAG_BOOK)
                            viewModel.trackTabSelected(NavigationParams.Tab.BOOK)
                        }
                        R.id.menu_discover -> {
                            loadFragment(MainFlowManager.FRAG_DISCOVER)
                            viewModel.trackTabSelected(NavigationParams.Tab.DISCOVER)
                        }
                        R.id.menu_account -> {
                            loadFragment(MainFlowManager.FRAG_ACCOUNT)
                            viewModel.trackTabSelected(NavigationParams.Tab.ACCOUNT)
                        }
                        R.id.menu_connect -> {
                            loadFragment(MainFlowManager.FRAG_CONNECT)
                            viewModel.trackTabSelected(NavigationParams.Tab.CONNECT)
                        }
                    }
                }
            })

            // set the current position with intent or savedInstanceState
            val tabID = if (intent.hasExtra(SELECTED_TAB)) {
                intent.getIntExtra(SELECTED_TAB, R.id.menu_home).also {
                    intent.removeExtra(SELECTED_TAB)
                }
            } else {
                savedInstanceState?.getInt(SELECTED_TAB) ?: R.id.menu_home
            }
            selectedItemId = tabID
        }

    }

    private fun loadFragment(tag: String) {
        val fragment = mainFlowManager.transitionTo(tag).apply {
            arguments = Bundle().apply {
                intent.getStringExtra(EXPLORE_NOTIFICATION_EVENT_ID)
                    .takeIf { it.isNullOrEmpty().not() }
                    ?.let { eventId ->
                        if (MainFlowManager.FRAG_BOOK == tag) {
                            putString(BundleKeys.ID, eventId)
                        } else {
                            intent.removeExtra(EXPLORE_NOTIFICATION_EVENT_ID)
                        }
                    }
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_placeholder, fragment, tag)
            .commitAllowingStateLoss()
        behavior.expandHalf()
    }

    private fun displayHeader(title: String, subtitle: String, imageUrl: String?, emoji: String?) {
        with(binding.homeHeader) {
            setTitle(title)
            setSubtitle(subtitle)
            setImage(imageUrl, fallback = R.drawable.ic_soho_house_small_white)
            setEmoji(emoji)
            visibility = View.VISIBLE
        }
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
                start(this, R.id.menu_home)
            })

        showInAppNotification(this, item, ::onDialogPrimaryButtonClicked)
    }

    override fun getFilterScreenNavigationIntent(
        context: Context?,
        filterType: FilterType,
        eventType: EventType
    ): Intent {
        viewModel.trackFilterEvent(eventType)
        return BookFilterActivity.newIntent(context, filterType, eventType)
    }

    private fun showNotificationAlertDialog() {
        val item = InAppNotificationAdapterItem(
            imageDrawableId = R.drawable.ic_soho_house,
            status = getString(R.string.introducing_notifications_header),
            textBody = getString(R.string.introducing_notifications_supporting),
            primaryButtonString = getString(R.string.introducing_notifications_cta),
            secondaryButtonString = getString(R.string.introducing_notifications_skip_cta),
            isTextBodyVisible = true,
            isSecondaryButtonVisible = !NotificationManagerCompat.from(this)
                .areNotificationsEnabled()
        )
        showInAppNotification(this, item, ::onDialogPrimaryButtonClicked)
    }

    private fun onDialogPrimaryButtonClicked() {
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled())
            launchNotificationSettingsActivity()
    }

    private fun launchNotificationSettingsActivity() {
        val intent = Intent().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            } else {
                action = "android.settings.APP_NOTIFICATION_SETTINGS"
                putExtra("app_package", packageName)
                putExtra("app_uid", applicationInfo.uid)
            }
        }
        startActivity(intent)
    }

    override fun updateProfileImage(imageUrl: String) {
        imageLoader.load(imageUrl, true).apply { placeholder = R.drawable.ic_account }
            .into(profileTarget)
    }

    override fun indicateCurrentTab(tag: String?) {
        val id = when (tag) {
            MainFlowManager.FRAG_HOME -> R.id.menu_home
            MainFlowManager.FRAG_BOOK -> R.id.menu_book
            MainFlowManager.FRAG_DISCOVER -> R.id.menu_discover
            MainFlowManager.FRAG_ACCOUNT -> R.id.menu_account
            MainFlowManager.FRAG_CONNECT -> R.id.menu_connect
            else -> 0
        }
        if (id != 0) binding.bottomNavigationView.indicateCurrentItem(id)
    }

    /**
     * UpcomingBookingsFragment.Listener
     */
    override fun onExploreButtonClick() {
        selectExploreTab(BookTab.EVENTS)
    }

    override fun setLoadingState(state: LoadingState) {
        updateLoadingState(state)

        if (userManager.subscriptionType != SubscriptionType.FRIENDS) return
        binding.bottomNavigationView.updateLoadingState(state)
    }

    override fun setSwipeRefreshLoadingState(state: LoadingState) {
        updateLoadingState(state)
    }

    private fun updateLoadingState(state: LoadingState) {
        if (state == LoadingState.Idle) animateLoadingState(false)
        binding.progressBar.visibility =
            if (LoadingState.Loading == state) View.VISIBLE else View.INVISIBLE
    }

    private fun animateLoadingState(loading: Boolean) {
        if (animator?.loading == loading) return

        animator?.cancel()
        animator =
            LoadingStateAnimator(
                this,
                binding.contentViewLoadingView,
                binding.blurView,
                loading,
                overlayColor
            )
        animator?.start(object : LoadingStateAnimator.AnimatorCallback {
            override fun onAnimationCompleted() {
                animator = null
            }

            override fun onAnimationUpdate(color: Int) {
                overlayColor = color
            }
        })
    }
}