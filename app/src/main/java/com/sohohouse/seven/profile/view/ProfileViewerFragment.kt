package com.sohohouse.seven.profile.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Keep
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.sohohouse.seven.R
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.ErrorDialogFragment
import com.sohohouse.seven.base.mvvm.fragmentViewModel
import com.sohohouse.seven.common.BundleKeys.MESSAGE
import com.sohohouse.seven.common.BundleKeys.PROFILE
import com.sohohouse.seven.common.BundleKeys.SKIP_COLLAPSED
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.dagger.Injectable
import com.sohohouse.seven.common.design.adapter.RendererDiffAdapter
import com.sohohouse.seven.common.design.list.ListItemPaddingDecoration
import com.sohohouse.seven.common.extensions.setFragmentResult
import com.sohohouse.seven.common.extensions.setFragmentResultsListener
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.common.utils.UrlUtils
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.common.views.showSafe
import com.sohohouse.seven.connect.message.chat.ChatActivity
import com.sohohouse.seven.connect.mynetwork.connections.ConnectionsFragment.Companion.MY_CONNECTIONS_CHANGED
import com.sohohouse.seven.connect.mynetwork.requests.ConnectionRequestsFragment.Companion.CONNECTION_REQUEST_CHANGED
import com.sohohouse.seven.connect.trafficlights.members.MembersInTheVenueActivity.Companion.MEMBER_CONNECTION_CHANGED
import com.sohohouse.seven.connect.trafficlights.members.MembersInTheVenueActivity.Companion.WAS_MEMBER_BLOCKED
import com.sohohouse.seven.connect.trafficlights.update.UpdateAvailabilityStatusBottomSheet
import com.sohohouse.seven.databinding.FragmentProfileViewerBinding
import com.sohohouse.seven.more.AccountFragment.Companion.PROFILE_EDIT_REQUEST
import com.sohohouse.seven.profile.Blocked
import com.sohohouse.seven.profile.ResultListener
import com.sohohouse.seven.profile.SocialMediaItem
import com.sohohouse.seven.profile.edit.EditProfileActivity
import com.sohohouse.seven.profile.share.ShareProfileBottomSheet
import com.sohohouse.seven.profile.view.connect.ConnectRequestBottomSheet
import com.sohohouse.seven.profile.view.connect.ConnectRequestBottomSheet.Companion.REQUEST_SEND_CONNECTION
import com.sohohouse.seven.profile.view.model.*
import com.sohohouse.seven.profile.view.more.MoreOptionsBottomSheet
import com.sohohouse.seven.profile.view.renderer.ConnectionButtonsRenderer
import com.sohohouse.seven.profile.view.renderer.MessageRenderer
import com.sohohouse.seven.profile.view.renderer.ProfileHeaderRenderer
import com.sohohouse.seven.profile.view.renderer.SocialAccountsRenderer
import javax.inject.Inject

@Keep
class ProfileViewerFragment : BottomSheetDialogFragment(), Injectable {

    val binding by viewBinding(FragmentProfileViewerBinding::bind)

    @Inject
    lateinit var assistedFactory: ProfileViewerViewModel.Factory

    private val profile: ProfileItem by lazy {
        arguments?.getParcelable(PROFILE) ?: ProfileItem()
    }

    private val viewModel: ProfileViewerViewModel by fragmentViewModel {
        assistedFactory.create(
            profile = profile,
            message = arguments?.getString(MESSAGE)
        )
    }

    private val profileHeaderAdapter = RendererDiffAdapter<DiffItem>().apply {
        registerRenderers(
            ProfileHeaderRenderer(
                coroutineScope = lazy { lifecycleScope }
            ) {
                if (viewModel.isCheckedIn && viewModel.isMyProfile)
                    UpdateAvailabilityStatusBottomSheet().show(
                        requireActivity().supportFragmentManager,
                        UpdateAvailabilityStatusBottomSheet.TAG
                    )
            },
            SocialAccountsRenderer(::onSocialAccountClick),
            MessageRenderer(),
            ConnectionButtonsRenderer(::onClickButton)
        )
    }

    private lateinit var startForResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultsListener(
            requestKeys = arrayOf(CONNECTION_STATUS_CHANGED + profile.id, REQUEST_SEND_CONNECTION),
            listener = { _, _ ->
                viewModel.getProfile()
                onConnectionStatusChanged()
            }
        )
    }

    private fun onConnectionStatusChanged() {
        setFragmentResult(MY_CONNECTIONS_CHANGED)
        setFragmentResult(CONNECTION_REQUEST_CHANGED)

        parentFragmentManager.setFragmentResult(
            MEMBER_CONNECTION_CHANGED, bundleOf(
                WAS_MEMBER_BLOCKED to (viewModel.profile.status is Blocked)
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_viewer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupViewModel()
    }

    private fun setupViews() {
        binding.recyclerView.adapter = profileHeaderAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.itemAnimator = null

        val spacing = resources.getDimensionPixelSize(R.dimen.dp_16)
        binding.recyclerView.addItemDecoration(
            ListItemPaddingDecoration(
                verticalSpacing = spacing,
                horizontalSpacing = spacing
            )
        )

        setupTabLayout()
    }

    private fun setupViewModel() {
        viewModel.items.observe(viewLifecycleOwner) {
            profileHeaderAdapter.submitItems(it)
            binding.recyclerView.post { setupBottomSheet(dialog as? BottomSheetDialog) }
        }
        viewModel.connectionChanged.observe(viewLifecycleOwner) { onConnectionStatusChanged() }
        viewModel.error.observe(viewLifecycleOwner) { showErrorDialog() }
        viewModel.navigateToChatScreen.observe(viewLifecycleOwner) {
            startActivity(
                if (it.isNew) {
                    ChatActivity.newIntentViaURL(
                        context = requireContext(),
                        channelUrl = it.channelUrl,
                        memberId = viewModel.profileId
                    )
                } else {
                    ChatActivity.newIntentViaID(
                        context = requireContext(),
                        channelId = it.channelId,
                        memberId = viewModel.profileId
                    )
                }
            )
        }
        setupTabLayout()
    }

    private fun showErrorDialog() {
        ErrorDialogFragment().showSafe(childFragmentManager, ErrorDialogFragment.TAG)
    }

    private fun setupTabLayout() {

        val adapter = ProfileViewerAdapter(this, profile.id, viewModel.isFriendsSubscriptionType)

        startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    viewModel.getProfile()
                    adapter.fragments.forEach { (it as? ResultListener)?.onResult() }
                    setFragmentResult(PROFILE_EDIT_REQUEST)
                }
            }

        with(binding) {
            viewPager.adapter = adapter
            viewPager.offscreenPageLimit = 1
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.setText(adapter.getTitle(position))
            }.attach()
            tabLayout.setVisible(adapter.itemCount > 1)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.logViewed()
    }

    private fun setupBottomSheet(bottomSheetDialog: BottomSheetDialog?) {
        val bottomSheet: View = bottomSheetDialog?.findViewById(R.id.design_bottom_sheet) ?: return
        bottomSheet.layoutParams =
            bottomSheet.layoutParams.apply { height = WindowManager.LayoutParams.MATCH_PARENT }
        BottomSheetBehavior.from<View?>(bottomSheet).apply {
            this.peekHeight = binding.recyclerView.measuredHeight

            if (arguments?.getBoolean(SKIP_COLLAPSED, false) == false) return

            this.skipCollapsed = true
            this.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun onSocialAccountClick(item: SocialMediaItem) {
        viewModel.logSocialMediaClick(item)
        UrlUtils.sanitiseUrl(item.url)?.let {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it)))
        }
    }

    private fun onClickButton(button: Button) {
        when (button) {
            EditButton -> editProfile()
            ConnectButton -> showConnectionRequestBottomSheet()
            AcceptButton -> viewModel.acceptRequest()
            IgnoreButton -> viewModel.ignoreRequest()
            OptionsMenu -> showMoreOptions()
            UnblockButton -> viewModel.unblockMember()
            MessageButton -> viewModel.createChannel()
            ShareProfileButton -> shareProfile()
            else -> {
            }
        }
    }

    private fun editProfile() {
        startForResult.launch(Intent(requireContext(), EditProfileActivity::class.java))
    }

    private fun showConnectionRequestBottomSheet() {
        ConnectRequestBottomSheet.withProfile(viewModel.profile)
            .showSafe(parentFragmentManager, ConnectRequestBottomSheet.TAG)
    }

    private fun shareProfile() {
        ShareProfileBottomSheet().showSafe(parentFragmentManager, ShareProfileBottomSheet.TAG)
        viewModel.logAnalyticsAction(AnalyticsManager.Action.ProfileShareProfile)
    }

    private fun showMoreOptions() {
        MoreOptionsBottomSheet.with(viewModel.profile)
            .showSafe(parentFragmentManager, MoreOptionsBottomSheet.TAG)
    }

    companion object {
        const val TAG = "view_profile_dialog"

        const val CONNECTION_STATUS_CHANGED = "connection_status_changed?id="

        fun withProfile(
            profile: ProfileItem,
            skipCollapsed: Boolean = false,
            message: String? = null
        ): ProfileViewerFragment {
            return ProfileViewerFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(PROFILE, profile)
                    putBoolean(SKIP_COLLAPSED, skipCollapsed)
                    putString(MESSAGE, message)
                }
            }
        }
    }
}