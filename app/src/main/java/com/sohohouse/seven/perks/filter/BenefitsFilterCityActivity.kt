package com.sohohouse.seven.perks.filter

import android.app.Activity
import com.sohohouse.seven.R
import com.sohohouse.seven.base.InjectableActivity

class BenefitsFilterCityActivity : InjectableActivity(), BenefitsFilterCityFragment.Listener {

    override fun getContentLayout(): Int {
        return R.layout.activity_benefits_filter_city
    }

    override fun onCityFilterConfirmed() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onCanceled() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}