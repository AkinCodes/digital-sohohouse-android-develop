package com.sohohouse.seven.housepay

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.BuildConfig
import com.sohohouse.seven.R
import com.sohohouse.seven.base.ErrorDialogFragment
import com.sohohouse.seven.base.InjectableActivity
import com.sohohouse.seven.base.mvvm.LoadingViewController
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.databinding.ActivityHousePayLandingBinding
import com.sohohouse.seven.home.repo.HousePayBannerDelegateImpl
import com.sohohouse.seven.housepay.home.HousePayHomeFragment
import com.sohohouse.seven.housepay.tools.HPOnboardStatus
import com.sohohouse.seven.payment.housepay.AddHousePayFragment
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class HousepayActivity : InjectableActivity(), LoadingViewController {

    @Inject
    lateinit var onboardStatus: HPOnboardStatus

    @Inject
    lateinit var housepayChecks: HousePayBannerDelegateImpl // implementation is used to check for open checks

    val binding by viewBinding(ActivityHousePayLandingBinding::bind)

    override val loadingView: LoadingView
        get() = binding.activityHousePayLoadingView

    override fun getContentLayout() = R.layout.activity_house_pay_landing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadingView.toggleSpinner(true)

        lifecycleScope.launch(handler) {
            val recentCheck = housepayChecks.getHousePayBanner() // banner contains details about check
            // If there is open check show open check fragment, else if not onboarded show onboarding fragment, else show AddHousePay
            val onBoarded = onboardStatus.isComplete()

            var fragment = when {
//                recentCheck?.id == HousePayBannerDelegateImpl.TYPE_OPEN_CHECK -> FragmentHousepayCheck()
                onBoarded == HPOnboardStatus.OnboardStatus.Yes -> AddHousePayFragment()
                onBoarded == HPOnboardStatus.OnboardStatus.No -> FragmentHousepayOnboarding()
                else -> ErrorDialogFragment()
            }

            fragment = HousePayHomeFragment()

            launch(Dispatchers.Main) {
                loadingView.toggleSpinner(false)
                setFragment(fragment)
            }
        }
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.house_pay_fragment_placeholder, fragment, "HousePay")
            .commitAllowingStateLoss()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (BuildConfig.DEBUG) {
            if (keyCode >= KeyEvent.KEYCODE_1 && keyCode <= KeyEvent.KEYCODE_5) {
                setFragment(
                    when (keyCode) {
                        KeyEvent.KEYCODE_1 -> AddHousePayFragment()
                        KeyEvent.KEYCODE_2 -> FragmentHousepayOnboarding()
                        KeyEvent.KEYCODE_3 -> FragmentHousepayCheck()
                        else -> ErrorDialogFragment()
                    }
                )
                return true
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    private val handler = CoroutineExceptionHandler { _, exception ->
        lifecycleScope.launch(Dispatchers.Main) {
            if (Thread.currentThread().name == "main") {
                loadingView.toggleSpinner(false)
            }
        }
        Timber.d("CoroutineExceptionHandler got $exception")
    }
}
