package com.sohohouse.seven.debug

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.App
import com.sohohouse.seven.R
import com.sohohouse.seven.apponboarding.terms.OnboardingTermsActivity
import com.sohohouse.seven.base.BaseActivity
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.user.AnalyticsConsent
import com.sohohouse.seven.databinding.ActivityDebugBinding
import com.sohohouse.seven.intro.IntroActivity
import com.sohohouse.seven.memberonboarding.induction.booking.InductionBookingActivity

class DebugActivity : BaseActivity() {

    companion object {
        const val PRODUCTION_STRING_KEY = "production"
        const val STAGING_STRING_KEY = "staging"
    }

    override fun getContentLayout() = R.layout.activity_debug

    private val binding by viewBinding(ActivityDebugBinding::bind)

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        binding.setupViews(this)
    }

    @SuppressLint("SetTextI18n")
    private fun ActivityDebugBinding.setupViews(activity: DebugActivity) {
        debugTermsOnboarding.clicks {
            val userManager = App.appComponent.userManager
            userManager.analyticsConsent = AnalyticsConsent.NONE
            userManager.didConsentTermsConditions = false
            userManager.isAppOnboardingComplete = false
            val intent = Intent(
                activity,
                OnboardingTermsActivity::class.java
            )
            startActivity(intent)
        }

        debugTermsInduction.clicks {
            val userManager = App.appComponent.userManager
            userManager.isInducted = false
            val intent = Intent(
                activity,
                InductionBookingActivity::class.java
            )
            startActivity(intent)
        }

        with(debugSwitchEndpoints) {
            text = getAppModeText()
            clicks {
                App.buildConfigManager.isStaging = !App.buildConfigManager.isStaging
                text = getAppModeText()
                val moreUtils = App.appComponent.logoutUtil
                moreUtils.logout()
            }
        }

        debugForceCrash.clicks {
            throw RuntimeException("Force crash!")
        }

        welcomeScreen.clicks {
            startActivity(Intent(activity, IntroActivity::class.java))
        }
    }

    private fun getAppModeText() =
        "Change to ${if (App.buildConfigManager.isStaging) PRODUCTION_STRING_KEY else STAGING_STRING_KEY}"

}