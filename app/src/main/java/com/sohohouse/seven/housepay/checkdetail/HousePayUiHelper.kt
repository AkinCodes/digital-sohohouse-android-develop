package com.sohohouse.seven.housepay.checkdetail

import com.sohohouse.seven.R
import com.sohohouse.seven.common.views.TimeSinceTextView

object HousePayUiHelper {
    fun createLastUpdatedConfig(): TimeSinceTextView.Config {
        return TimeSinceTextView.Config(
            format = R.string.last_updated_x_ago,
            secondsLabel = R.plurals.seconds,
            minutesLabel = R.plurals.minutes,
            hoursLabel = R.plurals.hours,
            daysLabel = R.plurals.days,
            zeroSecondsSubstitute = R.string.last_updated_a_moment_ago
        )
    }
}