package com.sohohouse.seven.membership

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.InjectableActivity
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.user.GymMembership
import com.sohohouse.seven.databinding.ActivityActiveMembershipInfoBinding
import com.sohohouse.seven.more.contact.MoreContactActivity
import javax.inject.Inject

class ActiveMembershipInfoActivity : InjectableActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: MembershipInfoViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[MembershipInfoViewModel::class.java]
    }

    override fun getContentLayout(): Int = R.layout.activity_active_membership_info
    private val binding by viewBinding(ActivityActiveMembershipInfoBinding::bind)

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        with(binding) {
            closeButton.setOnClickListener { onBackPressed() }

            subscribeActive.setOnClickListener {
                trackEvent(AnalyticsManager.Action.ActiveInfoSubscribe)
                openContactForm(GymMembership.ACTIVE)
            }

            subscribeActivePlus.setOnClickListener {
                trackEvent(AnalyticsManager.Action.ActiveInfoSubscribePlus)
                openContactForm(GymMembership.ACTIVE_PLUS)
            }
        }
    }

    private fun trackEvent(action: AnalyticsManager.Action) {
        val eventId = intent.getStringExtra(BUNDLE_KEY_EVENT_ID)
        val eventName = intent.getStringExtra(BUNDLE_KEY_EVENT_NAME)
        val eventType = intent.getStringExtra(BUNDLE_KEY_EVENT_TYPE)
        val params = AnalyticsManager.SubscribeActive.buildParams(eventId, eventName, eventType)
        viewModel.trackEvent(action, params)
    }

    private fun openContactForm(gymMembership: GymMembership) {
        startActivity(
            MoreContactActivity.getIntentForActiveMembershipSubscription(
                this,
                gymMembership
            )
        )
    }

    companion object {

        private const val BUNDLE_KEY_EVENT_ID = "event_id"
        private const val BUNDLE_KEY_EVENT_NAME = "event_name"
        private const val BUNDLE_KEY_EVENT_TYPE = "event_type"

        fun getIntent(
            context: Context,
            eventId: String?,
            eventName: String?,
            eventType: String?
        ): Intent {
            return Intent(context, ActiveMembershipInfoActivity::class.java).apply {
                putExtra(BUNDLE_KEY_EVENT_ID, eventId)
                putExtra(BUNDLE_KEY_EVENT_NAME, eventName)
                putExtra(BUNDLE_KEY_EVENT_TYPE, eventType)
            }
        }
    }
}