package com.sohohouse.seven.common.views

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.MainThread
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import com.sohohouse.seven.common.utils.DateUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.*

class TimeSinceTextView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : AppCompatTextView(
    context, attributeSet, defStyleAttrs
) {

    companion object {
        private val UPDATE_INTERVAL_SECONDS = SECONDS.toMillis(30)
        private val UPDATE_INTERVAL_MINS = MINUTES.toMillis(1)
        private val UPDATE_INTERVAL_HOURS = HOURS.toMillis(1)
        private val UPDATE_INTERVAL_DAYS = DAYS.toMillis(1)
    }

    var markedTime: Long? = null
        set(value) {
            field = value
            displayTimeSince()
        }

    private var config: Config? = null

    private var timer: Timer? = null

    fun setUp(config: Config) {
        this.config = config
    }

    fun setTextOverrideTimer(text: CharSequence) {
        timer?.cancel()
        setText(text)
    }

    private fun runUpdateTimer(updateInterval: Long?) {
        updateInterval ?: return
        timer?.cancel()
        this.timer = Timer().apply {
            schedule(object : TimerTask() {
                override fun run() {
                    MainScope().launch {
                        displayTimeSince()
                    }
                }
            }, updateInterval)
        }
    }

    private fun getUpdateInterval(timeUnit: TimeUnit): Long? {
        return when (timeUnit) {
            SECONDS -> UPDATE_INTERVAL_SECONDS
            MINUTES -> UPDATE_INTERVAL_MINS
            HOURS -> UPDATE_INTERVAL_HOURS
            DAYS -> UPDATE_INTERVAL_DAYS
            else -> null
        }
    }

    @MainThread
    private fun displayTimeSince() {
        val markedTime = this.markedTime ?: return
        val config = this.config ?: return

        val elapsedTime = DateUtils.getTimeElapsed(markedTime, System.currentTimeMillis()) ?: return
        val (timeUnit, amount) = elapsedTime
        val amountInt = amount.toInt()

        val text: String

        if (timeUnit == SECONDS
            && amount == 0L
            && config.zeroSecondsSubstitute != null
        ) {
            text = resources.getString(config.zeroSecondsSubstitute)
        } else {
            val pluralsRes = when (timeUnit) {
                SECONDS -> config.secondsLabel
                MINUTES -> config.minutesLabel
                HOURS -> config.hoursLabel
                DAYS -> config.daysLabel
                else -> null
            }

            val quantityString = if (pluralsRes != null) {
                resources.getQuantityString(
                    pluralsRes,
                    amountInt,
                    amountInt
                )
            } else ""

            text = resources.getString(
                config.format,
                quantityString
            )
        }

        setText(text)

        runUpdateTimer(getUpdateInterval(timeUnit))
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        timer?.cancel()
    }

    data class Config(
        @StringRes val format: Int, // eg "Last updated %s ago"
        @PluralsRes val secondsLabel: Int,
        @PluralsRes val minutesLabel: Int,
        @PluralsRes val hoursLabel: Int,
        @PluralsRes val daysLabel: Int,
        @StringRes val zeroSecondsSubstitute: Int? = null // e.g. "Last updated a moment ago"
    )

}