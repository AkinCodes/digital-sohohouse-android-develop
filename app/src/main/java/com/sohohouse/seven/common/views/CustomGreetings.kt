package com.sohohouse.seven.common.views

import androidx.annotation.StringRes
import com.sohohouse.seven.R
import java.util.*

enum class CustomGreetings(
    val defaultGreetings: Greetings,
    val localeGreetings: Map<Locale, Greetings>
) {
    MYK(
        Greetings(
            R.string.good_morning_greek_label,
            R.string.good_afternoon_greek_label,
            R.string.good_evening_greek_label
        ), mapOf()
    ),
    AMST(
        Greetings(
            R.string.good_morning_dutch_label,
            R.string.good_afternoon_dutch_label,
            R.string.good_evening_dutch_label
        ), mapOf()
    ),
    LBHB(
        Greetings(
            R.string.good_morning_spanish_label,
            R.string.good_afternoon_spanish_label,
            R.string.good_evening_spanish_label
        ),
        mapOf(
            Pair(
                Locale("ca", "ES"),
                Greetings(
                    R.string.good_morning_catalan_label,
                    R.string.good_afternoon_catalan_label,
                    R.string.good_evening_catalan_label
                )
            )
        )
    ),
    BCL(
        Greetings(
            R.string.good_morning_spanish_label,
            R.string.good_afternoon_spanish_label,
            R.string.good_evening_spanish_label
        ),
        mapOf(
            Pair(
                Locale("ca", "ES"),
                Greetings(
                    R.string.good_morning_catalan_label,
                    R.string.good_afternoon_catalan_label,
                    R.string.good_evening_catalan_label
                )
            )
        )
    ),
    BER(
        Greetings(
            R.string.good_morning_german_label,
            R.string.good_afternoon_german_label,
            R.string.good_evening_german_label
        ), mapOf()
    ),
    ISTAN(
        Greetings(
            R.string.good_morning_turkish_label,
            R.string.good_afternoon_turkish_label,
            R.string.good_evening_turkish_label
        ), mapOf()
    ),
    SHK(
        Greetings(
            R.string.good_morning_mandarin_label,
            R.string.good_afternoon_mandarin_label,
            R.string.good_evening_mandarin_label
        ), mapOf()
    );

    companion object {
        fun getCustomGreeting(venueId: String?, locale: Locale, hourOfDay: Int): Period {
            val customGreeting = CustomGreetings.values().find { it.name == venueId }
            if (customGreeting != null) {
                return customGreeting.localeGreetings[locale]?.getGreetingFromHourOfDay(hourOfDay)
                    ?: customGreeting.defaultGreetings.getGreetingFromHourOfDay(hourOfDay)
            }
            return Greetings(
                R.string.good_morning_english_label,
                R.string.good_afternoon_english_label,
                R.string.good_evening_english_label
            ).getGreetingFromHourOfDay(hourOfDay)
        }
    }
}

data class Greetings(
    @StringRes val morning: Int,
    @StringRes val afternoon: Int,
    @StringRes val evening: Int
) {
    fun getGreetingFromHourOfDay(hourOfDay: Int): Period {
        return when (hourOfDay) {
            in 5 until 12 -> Period(morning, getGreetingEmoji(morning))
            in 12 until 18 -> Period(afternoon, getGreetingEmoji(afternoon))
            else -> Period(evening, getGreetingEmoji(evening))
        }
    }

    fun getGreetingEmoji(@StringRes period: Int): Int? {
        return when (period) {
            morning -> R.string.emoji_morning
            afternoon -> R.string.emoji_afternoon
            evening -> R.string.emoji_night
            else -> null
        }
    }
}

data class Period(val title: Int, val emoji: Int?)