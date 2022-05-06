package com.sohohouse.seven.housepay.checkdetail.open.pay

import com.sohohouse.seven.network.core.models.housepay.Check
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test

class ValidateCheckImplTest {
    @Test
    fun `check which is not open but has remaining cents returns error`() {
        //GIVEN
        val check = mockk<Check>().also {
            every { it.status } returns Check.STATUS_CLOSED
            every { it.remainingCents } returns 5000
        }

        val cut = ValidateCheckImpl()

        //WHEN
        val result = cut.invoke(check)

        //THEN
        assertTrue(result is PayCheckError.Unknown)
    }

    @Test
    fun `check which is not open returns error`() {
        //GIVEN
        val check = mockk<Check>().also {
            every { it.status } returns Check.STATUS_CLOSED
            every { it.remainingCents } returns 0
        }

        val cut = ValidateCheckImpl()

        //WHEN
        val result = cut.invoke(check)

        //THEN
        assertTrue(result is PayCheckError.CheckClosed)
    }

    @Test
    fun `check which is open but has no remaining cents to pay returns error`() {
        //GIVEN
        val check = mockk<Check>().also {
            every { it.status } returns Check.STATUS_OPEN
            every { it.remainingCents } returns 0
        }

        val cut = ValidateCheckImpl()

        //WHEN
        val result = cut.invoke(check)

        //THEN
        assertTrue(result is PayCheckError.NoAmount)

    }
}