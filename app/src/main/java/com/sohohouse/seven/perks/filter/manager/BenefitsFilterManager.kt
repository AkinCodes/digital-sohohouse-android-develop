package com.sohohouse.seven.perks.filter.manager

import com.sohohouse.seven.common.prefs.PrefsManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BenefitsFilterManager @Inject constructor(private val prefsManager: PrefsManager) {

    var citiesFiltered: List<String>
        get() {
            return prefsManager.perksFilterCities
        }
        set(value) {
            prefsManager.perksFilterCities = value
        }

}