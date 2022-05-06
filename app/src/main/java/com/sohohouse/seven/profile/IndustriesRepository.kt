package com.sohohouse.seven.profile

import android.content.res.Resources
import com.sohohouse.seven.FeatureFlags
import com.sohohouse.seven.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IndustriesRepository @Inject constructor(private val resources: Resources) {
    fun getIndustries(): List<String> {
        val arrayRes =
            if (FeatureFlags.PREPOPULATE_PROFILE) R.array.industries_with_non_values else R.array.industries
        return resources.getStringArray(arrayRes).toList()
    }
}