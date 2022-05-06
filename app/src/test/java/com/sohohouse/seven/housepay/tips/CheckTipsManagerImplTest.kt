package com.sohohouse.seven.housepay.tips

import com.sohohouse.seven.network.core.models.housepay.Check
import com.sohohouse.seven.network.core.models.housepay.Location
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class CheckTipsManagerImplTest {

    @MockK
    lateinit var check: Check

    @MockK
    lateinit var location: Location

    private lateinit var cut: CheckTipsManagerImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `tip cents value is calculated correctly for percentage and rounded up to nearest cent`() {
        every { check.locationCode } returns "SD"
        every { check.subtotal } returns 200
        every { check.amountPaidByOthers } returns 50

        every { location.variableTips } returns true
        every { location.variableTipsValues } returns listOf("5", "7.5", "12")
        every { location.variableTipsDefault } returns "5"

        every { check.location } returns location

        cut = CheckTipsManagerImpl()
        cut.useCheck(check)

        cut.tip = Tip.Percentage(0.125f)

        Assert.assertEquals(
            19,
            cut.tipValueCents
        )
    }

    @Test
    fun `tip cents value is calculated correctly for percentage and rounded down to nearest cent`() {
        every { check.locationCode } returns "SD"
        every { check.subtotal } returns 250
        every { check.amountPaidByOthers } returns 100

        every { location.variableTips } returns true
        every { location.variableTipsValues } returns listOf("5", "7.5", "12")
        every { location.variableTipsDefault } returns "5"

        every { check.location } returns location

        cut = CheckTipsManagerImpl()
        cut.useCheck(check)

        cut.tip = Tip.Percentage(0.075f)

        Assert.assertEquals(
            11,
            cut.tipValueCents
        )
    }

    @Test
    fun `tip cents value is calculated correctly for percentage no rounding`() {
        every { check.locationCode } returns "SD"
        every { check.subtotal } returns 1900
        every { check.amountPaidByOthers } returns 400

        every { location.variableTips } returns true
        every { location.variableTipsValues } returns listOf("5", "7.5", "12")
        every { location.variableTipsDefault } returns "5"

        every { check.location } returns location

        cut = CheckTipsManagerImpl()
        cut.useCheck(check)

        cut.tip = Tip.Percentage(0.05f)

        Assert.assertEquals(
            75,
            cut.tipValueCents
        )
    }

    @Test
    fun `tip cents value is calculated correctly for percentage and rounded to nearest dollar in SHK`() {
        every { check.locationCode } returns "SHK"
        every { check.subtotal } returns 1800
        every { check.amountPaidByOthers } returns 300

        every { location.variableTips } returns true
        every { location.variableTipsValues } returns listOf("5", "7.5", "12")
        every { location.variableTipsDefault } returns "5"

        every { check.location } returns location

        cut = CheckTipsManagerImpl()
        cut.useCheck(check)

        cut.tip = Tip.Percentage(0.05f)

        Assert.assertEquals(
            100,
            cut.tipValueCents
        )
    }

    @Test
    fun `tip cents value is calculated correctly for default percentage`() {
        every { check.locationCode } returns "SD"
        every { check.subtotal } returns 1700
        every { check.amountPaidByOthers } returns 200

        every { location.variableTips } returns true
        every { location.variableTipsValues } returns listOf("5", "7.5", "12")
        every { location.variableTipsDefault } returns "5"

        every { check.location } returns location

        cut = CheckTipsManagerImpl()
        cut.useCheck(check)

        Assert.assertEquals(
            75,
            cut.tipValueCents
        )
    }

    @Test
    fun `custom tip cents value does not exceed max custom tip for check`() {
        every { check.locationCode } returns "SD"
        every { check.subtotal } returns 1500
        every { check.amountPaidByOthers } returns 200

        every { location.variableTips } returns true
        every { location.variableTipsValues } returns listOf("5", "7.5", "12")
        every { location.variableTipsDefault } returns "5"

        every { check.location } returns location

        cut = CheckTipsManagerImpl()
        cut.useCheck(check)

        cut.tip = Tip.CustomAmount(2000)

        Assert.assertEquals(
            1300,
            cut.tipValueCents
        )
    }

}