package com.sohohouse.seven.common.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.toDate
import com.sohohouse.seven.network.core.models.*
import org.assertj.core.api.Assertions
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalDateTime
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class DateUtilsTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `Same day, same hour, venue is open between the range - include start`() {
        // Given on Monday venue is open between 5 and 5:01 and it's 5
        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat("yyyy MM d HH:mm", Locale.UK).parse("2018 11 19 05:00")

        // THEN the venue is open
        assert(DateUtils.isVenueOpen(createOperatingHours(), TimeZone.getDefault().id, calendar))
    }

    @Test
    fun `Same day, same hour, venue is open between the range - include end`() {
        // Given on Monday venue is open between 5 and 5:01 and it's 5:01
        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat("yyyy MM d HH:mm", Locale.UK).parse("2018 11 19 05:01")

        // THEN the venue is open
        assert(DateUtils.isVenueOpen(createOperatingHours(), TimeZone.getDefault().id, calendar))
    }

    @Test
    fun `Same day, same hour, venue is not open outside range`() {
        // Given on Monday venue is open between 5 and 5:01 and it's 5:02
        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat("yyyy MM d HH:mm", Locale.UK).parse("2018 11 19 05:02")

        // THEN the venue is closed
        assert(!DateUtils.isVenueOpen(createOperatingHours(), TimeZone.getDefault().id, calendar))
    }

    @Test
    fun `Different open and close day, venue is open between`() {
        // Given on Monday venue is open between 16:01 and Tuesday 05:00 and it's Monday 17:00
        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat("yyyy MM d HH:mm", Locale.UK).parse("2018 11 19 17:00")

        // THEN the venue is open
        assert(DateUtils.isVenueOpen(createOperatingHours(), TimeZone.getDefault().id, calendar))
    }

    @Test
    fun `Different open and close day, venue is closed outside range - start`() {
        // Given on Monday venue is open between 16:01 and Tuesday 05:00 and it's Monday 16:00
        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat("yyyy MM d HH:mm", Locale.UK).parse("2018 11 19 16:00")

        // THEN the venue is open
        assert(!DateUtils.isVenueOpen(createOperatingHours(), TimeZone.getDefault().id, calendar))
    }

    @Test
    fun `Different open and close day, venue is closed outside range - end`() {
        // Given on Monday venue is open between 16:01 and Tuesday 05:00 and it's Tuesday 05:01
        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat("yyyy MM d HH:mm", Locale.UK).parse("2018 11 20 05:01")

        // THEN the venue is open
        assert(!DateUtils.isVenueOpen(createOperatingHours(), TimeZone.getDefault().id, calendar))
    }

    @Test
    fun `Venue open between Sunday opening and closing time even if it closes on Monday`() {
        // Given on Sunday venue is open between 20:00 and closes Monday 4:00 and it's Monday 3:00
        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat("yyyy MM d HH:mm", Locale.UK).parse("2018 11 19 03:00")

        // THEN the venue is open
        assert(DateUtils.isVenueOpen(createOperatingHours(), TimeZone.getDefault().id, calendar))
    }

    @Test
    fun `Same opening and closing hour results results in an empty string from opening hours`() {
        val dummyHoursStringProvider = object : StringProvider {
            override fun getString(resId: Int, vararg params: String): String {
                return "hours"
            }

            override fun getString(resId: Int?): String {
                return "hours"
            }

            override fun getStringArray(resId: Int): Array<String> {
                return emptyArray()
            }
        }
        // Given
        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat("yyyy MM d HH:mm", Locale.UK).parse("2018 11 19 03:00")

        // THEN 
        Assert.assertEquals(
            "", DateUtils.formatTodayOperatingHours(
                dummyHoursStringProvider,
                createEmptyOperatingHours(), TimeZone.getDefault().id, calendar
            )
        )
    }

    private fun createOperatingHours(): OperatingHours {
        val openPeriod = Period(VenueTime(1, "0500"), VenueTime(1, "0501"))
        val openPeriod1 = Period(VenueTime(1, "1601"), VenueTime(2, "0500"))
        val openPeriod2 = Period(VenueTime(2, "0601"), VenueTime(2, "0700"))
        val openPeriod3 = Period(VenueTime(3, "0000"), VenueTime(3, "2359"))
        val openPeriod4 = Period(VenueTime(4, "0000"), VenueTime(5, "2359"))
        val openPeriod5 = Period(VenueTime(5, "0000"), VenueTime(6, "2359"))
        val openPeriod6 = Period(VenueTime(0, "2000"), VenueTime(1, "0400"))
        return OperatingHours(
            periods = listOf(
                openPeriod,
                openPeriod1,
                openPeriod2,
                openPeriod3,
                openPeriod4,
                openPeriod5,
                openPeriod6
            )
        )
    }

    private fun createEmptyOperatingHours(): OperatingHours {
        val openPeriod = Period(VenueTime(1, "0000"), VenueTime(1, "0000"))
        val openPeriod1 = Period(VenueTime(2, "0000"), VenueTime(2, "0000"))
        val openPeriod2 = Period(VenueTime(3, "0000"), VenueTime(2, "0000"))
        val openPeriod3 = Period(VenueTime(4, "0000"), VenueTime(3, "0000"))
        val openPeriod4 = Period(VenueTime(5, "0000"), VenueTime(5, "0000"))
        val openPeriod5 = Period(VenueTime(6, "0000"), VenueTime(6, "0000"))
        val openPeriod6 = Period(VenueTime(0, "0000"), VenueTime(0, "0000"))
        return OperatingHours(
            periods = listOf(
                openPeriod,
                openPeriod1,
                openPeriod2,
                openPeriod3,
                openPeriod4,
                openPeriod5,
                openPeriod6
            )
        )
    }


    @Test
    fun `welcome evening message when 6PM - 11 59PM`() {
        Assertions.assertThat(DateUtils.getWelcomeHeaderRes(18))
            .isEqualTo(R.string.home_evening_header)
        Assertions.assertThat(DateUtils.getWelcomeHeaderRes(20))
            .isEqualTo(R.string.home_evening_header)
        Assertions.assertThat(DateUtils.getWelcomeHeaderRes(22))
            .isEqualTo(R.string.home_evening_header)
        Assertions.assertThat(DateUtils.getWelcomeHeaderRes(23))
            .isEqualTo(R.string.home_evening_header)
    }


    @Test
    fun `welcome morning message when 5AM - 10 59AM`() {
        Assertions.assertThat(DateUtils.getWelcomeHeaderRes(5))
            .isEqualTo(R.string.home_morning_header)
        Assertions.assertThat(DateUtils.getWelcomeHeaderRes(10))
            .isEqualTo(R.string.home_morning_header)
    }

    @Test
    fun `welcome afternoon message when 12PM - 5 59PM`() {
        Assertions.assertThat(DateUtils.getWelcomeHeaderRes(12))
            .isEqualTo(R.string.home_afternoon_header)
        Assertions.assertThat(DateUtils.getWelcomeHeaderRes(17))
            .isEqualTo(R.string.home_afternoon_header)
    }

    @Test
    fun `test time elapsed minutes ago`() {
        val to = Date()
        val from = LocalDateTime.now().minusMinutes(3).minusSeconds(1).toDate()

        val (timeUnit, elapsed) = DateUtils.getTimeElapsed(from, to)!!

        Assert.assertEquals(TimeUnit.MINUTES, timeUnit)
        Assert.assertEquals(3, elapsed)
    }

    @Test
    fun `test time elapsed hours ago`() {
        val to = Date()
        val from = LocalDateTime.now().minusHours(4).minusSeconds(1).toDate()

        val (timeUnit, elapsed) = DateUtils.getTimeElapsed(from, to)!!

        Assert.assertEquals(TimeUnit.HOURS, timeUnit)
        Assert.assertEquals(4, elapsed)
    }

    @Test
    fun `test time elapsed days ago`() {
        val to = Date()
        val from = LocalDateTime.now().minusDays(5).minusSeconds(1).toDate()

        val (timeUnit, elapsed) = DateUtils.getTimeElapsed(from, to)!!

        Assert.assertEquals(TimeUnit.DAYS, timeUnit)
        Assert.assertEquals(5, elapsed)
    }


}