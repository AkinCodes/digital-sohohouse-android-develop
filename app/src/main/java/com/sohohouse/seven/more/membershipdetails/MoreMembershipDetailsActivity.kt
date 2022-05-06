package com.sohohouse.seven.more.membershipdetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.format.DateUtils
import androidx.lifecycle.lifecycleScope
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.utils.MembershipUtils
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.ReloadableErrorStateView
import com.sohohouse.seven.common.views.webview.WebViewBottomSheetFragment
import com.sohohouse.seven.databinding.ActivityMoreMembershipDetailsBinding
import com.sohohouse.seven.network.core.models.Account

class MoreMembershipDetailsActivity : BaseMVVMActivity<MoreMembershipDetailsViewModel>(),
    Loadable.View, Errorable.View {

    private lateinit var binding: ActivityMoreMembershipDetailsBinding

    companion object {
        private const val MEMBERSHIP_DETAILS_MODE = "MembershipDetailsMode"

        fun getIntent(context: Context, mode: Mode): Intent {
            val intent = Intent(context, MoreMembershipDetailsActivity::class.java)
            intent.putExtra(MEMBERSHIP_DETAILS_MODE, mode as Parcelable)
            return intent
        }
    }

    override val viewModelClass: Class<MoreMembershipDetailsViewModel>
        get() = MoreMembershipDetailsViewModel::class.java

    override val loadingView: LoadingView
        get() = binding.activityMoreMembershipDetailsLoadingView

    override val errorStateView: ReloadableErrorStateView
        get() = binding.errorState

    init {
        lifecycleScope.launchWhenStarted {
            getMode().let { mode ->
                viewModel.trackWhenStarted(mode)
                setDisplayMode(mode ?: Mode.DETAILS)
            }
            viewModel.fetchAccount()
        }
    }

    override fun getContentLayout(): Int = R.layout.activity_more_membership_details

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMoreMembershipDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        viewModel.userMembership.observe(this, { onDataReady(it) })
        viewModel.url.observe(this, { loadUrl(it) })
        observeErrorState(this) { viewModel.fetchAccount() }
        observeLoadingState(this)
    }

    private fun onDataReady(userMembership: Account) {
        with(binding) {
            foundingMemberLabel.setVisible(userMembership.isFounder)

            membershipTypeSince.text = userMembership.joinedOn?.let { date ->
                val membershipSince = DateUtils.formatDateTime(
                    this@MoreMembershipDetailsActivity,
                    date.time,
                    DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_ABBREV_MONTH or DateUtils.FORMAT_SHOW_YEAR
                )
                getString(R.string.member_since_label, membershipSince)
            }

            localHouse.text = userMembership.venueName

            membershipCard.setMembership(
                subscriptionType = userMembership.subscriptionType,
                membershipDisplayName = userMembership.membershipDisplayName,
                memberName = getString(R.string.more_membership_name_label)
                    .replaceBraces(
                        userMembership.firstName ?: "",
                        userMembership.lastName ?: ""
                    ),
                membershipId = MembershipUtils.formatMembershipNumber(userMembership.id),
                shortCode = userMembership.shortCode,
                profileImageUrl = userMembership.profile?.imageUrl,
                loyaltyId = userMembership.loyaltyId,
                isStaff = userMembership.isStaff
            )
        }
    }

    private fun loadUrl(url: String) {
        if (url.isEmpty()) return

        WebViewBottomSheetFragment.withUrl(url)
            .show(supportFragmentManager, WebViewBottomSheetFragment.TAG)
    }

    private fun getMode(): Mode? {
        return intent.getSerializableExtra(MEMBERSHIP_DETAILS_MODE) as Mode?
    }

    private fun setDisplayMode(mode: Mode) {
        when (mode) {
            Mode.DETAILS -> {
                binding.dismissButton.setGone()
            }
            Mode.CARD_ONLY -> {
                with(binding) {
                    appBar.setGone()
                    dismissButton.apply {
                        setVisible()
                        clicks { finish() }
                    }
                }
            }
        }
    }

}
