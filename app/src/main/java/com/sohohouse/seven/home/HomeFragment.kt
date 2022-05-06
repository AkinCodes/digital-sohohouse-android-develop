package com.sohohouse.seven.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Keep
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.PinToTopOnItemPrepended
import com.sohohouse.seven.base.error.DisplayableError
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.book.BookTab
import com.sohohouse.seven.book.adapter.model.EventCarousel
import com.sohohouse.seven.book.adapter.model.EventCarouselHeader
import com.sohohouse.seven.book.adapter.model.EventCarouselItem
import com.sohohouse.seven.book.digital.DigitalEventsActivity
import com.sohohouse.seven.book.eventdetails.EventDetailsActivity
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.Scrollable
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.apihelpers.SohoWebHelper
import com.sohohouse.seven.common.design.carousel.CarouselHeaderRenderer
import com.sohohouse.seven.common.design.carousel.CarouselRenderer
import com.sohohouse.seven.common.extensions.applyColorScheme
import com.sohohouse.seven.common.extensions.setChildFragmentResultListener
import com.sohohouse.seven.common.extensions.withResultListener
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.common.views.showSafe
import com.sohohouse.seven.common.views.snackbar.Snackbar
import com.sohohouse.seven.common.views.snackbar.SnackbarState
import com.sohohouse.seven.common.views.toolbar.Banner
import com.sohohouse.seven.common.views.toolbar.BannerRenderer
import com.sohohouse.seven.common.views.webview.WebViewBottomSheetFragment
import com.sohohouse.seven.connect.match.OptInCompleteBottomSheet
import com.sohohouse.seven.connect.match.RecommendationListActivity
import com.sohohouse.seven.connect.match.RecommendationsOptInBottomSheet
import com.sohohouse.seven.connect.match.RecommendationsOptInBottomSheet.Companion.REQUEST_KAY_OPTED_IN
import com.sohohouse.seven.connect.trafficlights.controlpanel.TrafficLightControlPanelRenderer
import com.sohohouse.seven.connect.trafficlights.leave.LeaveVenueBottomSheet
import com.sohohouse.seven.connect.trafficlights.members.MembersInTheVenueActivity
import com.sohohouse.seven.databinding.FragmentHomeBinding
import com.sohohouse.seven.guests.NewGuestListActivity
import com.sohohouse.seven.home.adapter.HomeAdapter
import com.sohohouse.seven.home.adapter.renderers.*
import com.sohohouse.seven.home.adapter.viewholders.BannerShortcut
import com.sohohouse.seven.home.completeyourprofile.SetUpAppPromptItem
import com.sohohouse.seven.home.completeyourprofile.SetUpAppPromptItem.Prompt.COMPLETE_PROFILE
import com.sohohouse.seven.home.completeyourprofile.SetUpAppPromptItem.Prompt.CUSTOMISE_NOTIFICATIONS
import com.sohohouse.seven.home.houseboard.SwipeCallback
import com.sohohouse.seven.home.suggested_people.SuggestedPeopleRenderer
import com.sohohouse.seven.houseboard.post.HouseBoardPostActivity
import com.sohohouse.seven.housenotes.detail.sitecore.HouseNoteDetailsActivity
import com.sohohouse.seven.housepay.HousepayActivity
import com.sohohouse.seven.housepay.checkdetail.closed.CheckReceiptFragment
import com.sohohouse.seven.housepay.checkdetail.open.OpenCheckFragment
import com.sohohouse.seven.main.MainActivity
import com.sohohouse.seven.main.MainNavigationController
import com.sohohouse.seven.more.SettingsActivity
import com.sohohouse.seven.network.core.models.Event
import com.sohohouse.seven.perks.details.PerksDetailActivity
import com.sohohouse.seven.profile.edit.EditProfileActivity
import com.sohohouse.seven.profile.view.ProfileViewerFragment

@Keep
class HomeFragment : BaseMVVMFragment<HomeViewModel>(),
    Loadable.View,
    Scrollable {

    val binding by viewBinding(FragmentHomeBinding::bind)

    companion object {
        const val REQ_CODE_EDIT_PROFILE = 719
        const val REQ_CODE_UPDATE_AVAILABILITY = 720
    }

    override val viewModel: HomeViewModel by lazy {
        ViewModelProvider(requireActivity(), viewModelFactory)[viewModelClass]
    }

    override val viewModelClass: Class<HomeViewModel> = HomeViewModel::class.java

    private val adapter: HomeAdapter = HomeAdapter().apply {
        registerRenderers(
            RoundelShortcutsRenderer(::shortcutClick),
            HappeningNowRenderer(::onHappeningNowEventClicked),
            SuggestedPeopleRenderer(
                recyclerItemCallback = ::showUserProfile,
                seeAllCallback = ::seeAllSuggestedPeople,
                optInCallback = ::showOptInBottomSheet
            ),
            HouseNotesHeaderRenderer(::onHouseNotesSeeAllClick),
            HouseNoteContentRenderer(::onHouseNoteClicked),
            TrafficLightControlPanelRenderer(
                lazy { lifecycleScope },
                onLeaveClick = ::onLeaveClick,
                startMemberInVenue = ::startMemberInVenue
            ),
            OurHousesRenderer(::onSeeAllClick, ::onHouseImageClick),
            SetupAppPromptCarouselRenderer(::onSetUpAppPromptItemClick),
            HouseNotesCarouselRenderer(::onHouseNoteClicked),
            DiscoverPerksRenderer(::onSeeAllPerkButtonClicked, ::onPerkClicked),
            CarouselHeaderRenderer(EventCarouselHeader::class.java, ::onSeeAllOnDemandEvents),
            CarouselRenderer(EventCarousel::class.java, ::onItemClicked),
            BannerRenderer(::onBannerClick)
        )
    }

    private val navigationController: MainNavigationController?
        get() = (requireActivity() as? MainNavigationController)

    override val contentLayoutId: Int
        get() = R.layout.fragment_home

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupViewModel()
        listenCheckFragmentDismiss()
        listenLeaveVenueFragmentDismiss()

        if (savedInstanceState != null) {
            navigationController?.setLoadingState(LoadingState.Idle)
        }

        viewModel.setScreenName(name= AnalyticsManager.Screens.Home.name)
    }

    private fun setupViews() {
        binding.recyclerView.adapter = adapter
        adapter.registerAdapterDataObserver(PinToTopOnItemPrepended(binding.recyclerView))
        binding.swipeRefreshLayout.apply {
            applyColorScheme()
            setOnRefreshListener {
                viewModel.refreshHomeItems()
            }
        }
        createItemTouchHelper().attachToRecyclerView(binding.recyclerView)
    }

    private fun createItemTouchHelper(): ItemTouchHelper {
        return ItemTouchHelper(
            SwipeCallback(
                context = requireContext(),
                helper = adapter,
                leftSwipe = onBannerSwipe,
                rightSwipe = onBannerSwipe,
                leftSwipeText = getString(R.string.dismiss_button_label),
                rightSwipeText = getString(R.string.dismiss_button_label)
            )
        )
    }

    private val onBannerSwipe: (Banner, Int) -> Unit = { banner, _ ->
        viewModel.onBannerDismiss(banner)
    }

    private fun setupViewModel() {
        viewModel.items.observe(viewLifecycleOwner) { onDataReady(it) }
        viewModel.profileUrl.observe(viewLifecycleOwner) { updatePhoto(it) }
        viewModel.openProfile.observe(viewLifecycleOwner) { openProfile(it) }
        viewModel.error.observe(viewLifecycleOwner) {
            showErrorSnackbar(DisplayableError("", it))
        }
        viewModel.listLoading.observe(viewLifecycleOwner) {
            binding.swipeRefreshLayout.isRefreshing = it == LoadingState.Loading
            navigationController?.setSwipeRefreshLoadingState(it)
        }
        observeLoadingState(viewLifecycleOwner) { navigationController?.setLoadingState(it) }
        viewModel.events.observe(viewLifecycleOwner) {
            when (it) {
                is HomeViewModel.Event.LaunchIntent -> startActivity(it.intent)
                is HomeViewModel.Event.ShowErrorSnackbar -> showErrorSnackbar(it.error)
                is HomeViewModel.Event.ViewOpenCheck -> viewOpenCheck(it.checkId)
                is HomeViewModel.Event.ViewCheckReceipt -> viewCheckReceipt(it.checkId)
            }
        }
        viewModel.loadingState.observe(viewLifecycleOwner) {
            binding.swipeRefreshLayout.isRefreshing = it == LoadingState.Loading
            navigationController?.setLoadingState(it)
        }
    }

    private fun viewCheckReceipt(checkId: String) {
        CheckReceiptFragment.newInstance(checkId).show(
            childFragmentManager,
            CheckReceiptFragment.TAG
        )
    }

    private fun viewOpenCheck(id: String) {
        OpenCheckFragment.newInstance(id).show(
            childFragmentManager,
            OpenCheckFragment.TAG
        )
    }

    private fun listenCheckFragmentDismiss() {
        setChildFragmentResultListener(
            OpenCheckFragment.REQ_KEY_CHECK_DISMISSED
        ) { _, _ ->
            viewModel.refreshHousePayBanner()
        }
    }

    private fun listenLeaveVenueFragmentDismiss() {
        setChildFragmentResultListener(
            LeaveVenueBottomSheet.RESULT_TAG,
        ) { requestKey, _ ->
            if (requestKey == LeaveVenueBottomSheet.RESULT_TAG) viewModel.getHomeItems()
        }
    }

    private fun onBannerClick(banner: Banner) {
        viewModel.onBannerClick(banner)
    }

    private fun showErrorSnackbar(error: DisplayableError) {
        this.view?.let {
            Snackbar.Builder(it, useAsRoot = true)
                .setTitle(error.message)
                .setState(SnackbarState.NEGATIVE)
                .build()
                ?.show()
        }
    }

    private fun shortcutClick(shortcut: BannerShortcut) {
        when (shortcut) {
            BannerShortcut.HOUSE_PAY -> context?.let {
                startActivity(Intent(activity, HousepayActivity::class.java))
            }
            BannerShortcut.GUEST_INVITE -> context?.let {
                startActivity(NewGuestListActivity.getIntent(it))
            }
            BannerShortcut.HOUSE_VISIT -> (activity as? MainActivity)?.selectExploreTab(BookTab.HOUSE_VISIT)
            BannerShortcut.BEDROOMS -> (activity as? MainActivity)?.selectExploreTab(BookTab.BEDROOMS)
            BannerShortcut.BOOK_EVENT -> (activity as? MainActivity)?.selectExploreTab(BookTab.EVENTS)
            BannerShortcut.BOOK_GYM -> (activity as? MainActivity)?.selectExploreTab(BookTab.GYM)
            BannerShortcut.BOOK_SCREENING -> (activity as? MainActivity)?.selectExploreTab(viewModel.screeningTab)
            BannerShortcut.BENEFITS -> viewModel.onPerksClicked()
            BannerShortcut.OUR_SPACES -> showExploreSpaceWebpage()
            BannerShortcut.RESTAURANT -> showTableBooking()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.fixOptInState()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshHousePayBanner()
        navigationController?.indicateCurrentTab(tag)
        viewModel.checkForOptInStateChanges()
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }

    override fun startActivity(intent: Intent?) {
        when (intent?.component?.className) {
            HouseBoardPostActivity::class.java.name -> {
                startActivityForResult(intent, HouseBoardPostActivity.HOUSE_BOARD_POST_REQUEST_CODE)
            }
            else -> {
                super.startActivity(intent)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                EventDetailsActivity.REQUEST_CODE_EVENT_DETAILS -> {
                    if (data?.hasExtra(BundleKeys.EVENT) == true) {
                        val serializable = data.getSerializableExtra(BundleKeys.EVENT)
                        serializable?.let {
                            adapter.updateEventItem(it as Event)
                        }
                        return
                    }
                }
                REQ_CODE_EDIT_PROFILE,
                REQ_CODE_UPDATE_AVAILABILITY -> {
                    viewModel.getHomeItems()
                }
            }
        }
    }

    override fun scrollToPosition(position: Int) {
        binding.recyclerView.smoothScrollToPosition(position)
    }

    private fun onLeaveClick() {
        LeaveVenueBottomSheet().show(
            childFragmentManager,
            LeaveVenueBottomSheet::class.java.simpleName
        )
    }

    private fun showTableBooking() {
        viewModel.onTableBooking()
        (activity as? MainNavigationController)?.selectExploreTab(BookTab.BOOK_A_TABLE)
    }

    private fun showExploreSpaceWebpage() {
        WebViewBottomSheetFragment.withKickoutType(type = SohoWebHelper.KickoutType.STUDIO_SPACES)
            .show(requireActivity().supportFragmentManager, WebViewBottomSheetFragment.TAG)
    }

    private fun onDataReady(dataItems: List<DiffItem>) {
        adapter.submitItems(dataItems)
//        adapter.unregisterAdapterDataObserver(pinToTopAdapterObserver)
    }

    private fun showOptInBottomSheet(view: View) {
        RecommendationsOptInBottomSheet.newInstance()
            .withResultListener(REQUEST_KAY_OPTED_IN) { _, _ ->
                viewModel.refreshHomeItems()
            }
            .showSafe(parentFragmentManager, RecommendationsOptInBottomSheet.TAG)
    }

    private fun showOptInCompletedBottomSheet() {
        OptInCompleteBottomSheet.newInstance()
            .showSafe(parentFragmentManager, OptInCompleteBottomSheet.TAG)
    }

    private fun startMemberInVenue() {
        startActivityForResult(
            Intent(context, MembersInTheVenueActivity::class.java),
            REQ_CODE_UPDATE_AVAILABILITY
        )
    }

    private fun updatePhoto(url: String) {
        navigationController?.updateProfileImage(url)
    }

    private fun showToast(stringRes: Int) {
        val toastLayout = layoutInflater.inflate(
            R.layout.toast_success,
            requireActivity().findViewById(R.id.content)
        )
        toastLayout.findViewById<TextView>(R.id.toast_text).text = getString(stringRes)

        makeCustomToast(toastLayout)
    }

    private fun makeCustomToast(toastLayout: View) {
        Toast(requireContext()).apply {
            view = toastLayout
            duration = Toast.LENGTH_LONG
            setGravity(Gravity.FILL_HORIZONTAL or Gravity.BOTTOM, 0, 0)
        }.show()
    }

    private fun onHouseNotesSeeAllClick() {
        viewModel.onHouseNotesClicked()
    }

    private fun onHouseNoteClicked(id: String, isCityGuide: Boolean, position: Int) {
        viewModel.onHouseNoteClicked(id)
        startActivity(HouseNoteDetailsActivity.startIntent(requireContext(), id))
    }

    private fun onHappeningNowEventClicked(event: Event, imageView: ImageView) {
        val intent = EventDetailsActivity.getIntent(context, event.id, event.images?.large)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            requireActivity(),
            imageView, getString(R.string.events_event_image_description)
        )
        startActivityForResult(
            intent,
            EventDetailsActivity.REQUEST_CODE_EVENT_DETAILS,
            options.toBundle()
        )
    }

    private fun onItemClicked(item: EventCarouselItem, imageView: ImageView, position: Int) {
        val intent = EventDetailsActivity.getIntent(context, item.id, item.imageUrl)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            requireActivity(),
            imageView, getString(R.string.events_event_image_description)
        )
        startActivityForResult(
            intent,
            EventDetailsActivity.REQUEST_CODE_EVENT_DETAILS,
            options.toBundle()
        )
    }

    private fun onSetUpAppPromptItemClick(item: SetUpAppPromptItem) {
        viewModel.onCompleteYourProfileItemClick()
        when (item.prompt) {
            COMPLETE_PROFILE -> startActivityForResult(
                Intent(
                    requireContext(),
                    EditProfileActivity::class.java
                ), REQ_CODE_EDIT_PROFILE
            )
            CUSTOMISE_NOTIFICATIONS -> startActivity(
                Intent(
                    requireContext(),
                    SettingsActivity::class.java
                )
            )
        }
    }

    private fun onSeeAllClick() {
        viewModel.onSeeAllHousesClick()
    }

    private fun onHouseImageClick() {
        viewModel.onHousesImageClick()
    }

    private fun onPerkClicked(id: String, title: String?, promoCode: String?) {
        PerksDetailActivity.start(requireContext(), id)
        viewModel.trackEventPerksItem(id, title, promoCode)
    }

    private fun onSeeAllPerkButtonClicked() {
        viewModel.onSeeAllPerkButtonClicked()
    }

    private fun onSeeAllOnDemandEvents() {
        startActivity(DigitalEventsActivity.intent(requireContext()))
    }

    private fun showUserProfile(userId: String) {
        viewModel.openProfileById(userId)
    }

    private fun openProfile(item: ProfileItem) {
        ProfileViewerFragment.withProfile(item)
            .showSafe(parentFragmentManager, ProfileViewerFragment.TAG)
    }

    private fun seeAllSuggestedPeople(view: View) {
        startActivity(RecommendationListActivity.getIntent(requireContext()))
    }

    private fun optInToSuggestions(view: View) {
        RecommendationsOptInBottomSheet.newInstance()
            .showSafe(parentFragmentManager, RecommendationsOptInBottomSheet.TAG)
    }

}
