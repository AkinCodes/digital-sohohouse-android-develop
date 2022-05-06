package com.sohohouse.seven.connect.trafficlights.members

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.common.extensions.getAttributeColor
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.common.views.showSafe
import com.sohohouse.seven.connect.VenueMember
import com.sohohouse.seven.connect.trafficlights.update.UpdateAvailabilityStatusBottomSheet
import com.sohohouse.seven.databinding.MembersInTheVenueActivityBinding
import com.sohohouse.seven.profile.view.ProfileViewerFragment
import com.sohohouse.seven.profile.view.connect.ConnectRequestBottomSheet
import dagger.android.HasAndroidInjector
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MembersInTheVenueActivity : BaseMVVMActivity<MembersInTheVenueViewModel>(),
    HasAndroidInjector {

    override val viewModelClass: Class<MembersInTheVenueViewModel>
        get() = MembersInTheVenueViewModel::class.java

    val binding by viewBinding(MembersInTheVenueActivityBinding::bind)

    override fun getContentLayout(): Int = R.layout.members_in_the_venue_activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val adapter = MemberInTheVenueAdapter(::showProfileViewerFragment)

        with(binding) {
            back.setOnClickListener { onBackPressed() }
            setFragmentListeners()

            list.adapter = adapter
            list.layoutManager = GridLayoutManager(this@MembersInTheVenueActivity, 2)

            updateStatus.setOnClickListener { showUpdateBottomSheet() }
            image.setOnClickListener { showUpdateBottomSheet() }
            initViewModel(adapter)
        }
    }

    private fun showProfileViewerFragment(it: VenueMember) {
        ProfileViewerFragment
            .withProfile(ProfileItem(it))
            .showSafe(supportFragmentManager, ProfileViewerFragment.TAG)
    }

    private fun setFragmentListeners() {
        supportFragmentManager.setFragmentResultListener(
            UpdateAvailabilityStatusBottomSheet.ON_UPDATE_STATUS,
            this
        ) { result, _ ->
            if (result == UpdateAvailabilityStatusBottomSheet.ON_UPDATE_STATUS) {
                viewModel.refresh()
            }
        }

        supportFragmentManager.setFragmentResultListener(
            UpdateAvailabilityStatusBottomSheet.ON_LEAVE_LOCATION,
            this
        ) { result: String, _: Bundle ->
            if (result == UpdateAvailabilityStatusBottomSheet.ON_LEAVE_LOCATION) {
                setResult(RESULT_OK)
                finish()
            }
        }

        supportFragmentManager.setFragmentResultListener(
            MEMBER_CONNECTION_CHANGED,
            this
        ) { s: String, bundle: Bundle ->
            if (s == MEMBER_CONNECTION_CHANGED) {
                viewModel.clearCache()
                viewModel.refresh()
                // if member was blocked update home fragment list
                if (bundle.getBoolean(WAS_MEMBER_BLOCKED))
                    setResult(RESULT_OK)
            }
        }
    }

    private fun MembersInTheVenueActivityBinding.initViewModel(adapter: MemberInTheVenueAdapter) {
        viewModel.profileImageUrl.onEach(image::setImageUrl).launchIn(lifecycleScope)
        viewModel.title.onEach(statusTitle::setText).launchIn(lifecycleScope)
        viewModel.showConnectionRequestBottomSheet.observe(this@MembersInTheVenueActivity) {
            showConnectionRequestBottomSheet(it.profileItem)
            supportFragmentManager.setFragmentResultListener(
                ConnectRequestBottomSheet.REQUEST_SEND_CONNECTION,
                this@MembersInTheVenueActivity
            ) { result: String, _: Bundle ->
                if (result == ConnectRequestBottomSheet.REQUEST_SEND_CONNECTION) {
                    it.onConnect()
                }
            }
        }

        viewModel.venueMembers.onEach { pagedList ->
            adapter.submitList(pagedList)
        }.launchIn(lifecycleScope)

        viewModel.areOtherMembersInVenue.onEach {
            emptySubtitle.isVisible = !it
            emptyTitle.isVisible = !it
        }.launchIn(lifecycleScope)

        viewModel.statusColorAttr.onEach {
            imageStatus.backgroundTintList =
                ColorStateList.valueOf(imageStatus.getAttributeColor(it))
        }.launchIn(lifecycleScope)

        viewModel.connectionMembersCountAndHouseName.onEach { (count, houseName) ->
            description.text = if (count == 0)
                getString(R.string.no_connections_in_venue, houseName)
            else
                getString(R.string.n_connections_in_venue, count.toString(), houseName)
        }.launchIn(lifecycleScope)

        viewModel.showProfileViewer.observe(this@MembersInTheVenueActivity) {
            showProfileViewerFragment(it)
        }
    }

    private fun showConnectionRequestBottomSheet(profile: ProfileItem) {
        ConnectRequestBottomSheet.withProfile(profile)
            .showSafe(supportFragmentManager, ConnectRequestBottomSheet.TAG)
    }

    private fun showUpdateBottomSheet() {
        UpdateAvailabilityStatusBottomSheet().show(
            supportFragmentManager,
            UpdateAvailabilityStatusBottomSheet.TAG
        )
    }

    companion object {
        const val MEMBER_CONNECTION_CHANGED = "member_connection_changed"
        const val WAS_MEMBER_BLOCKED = "was_member_blocked"
    }

}