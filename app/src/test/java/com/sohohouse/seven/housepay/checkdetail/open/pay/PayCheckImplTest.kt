package com.sohohouse.seven.housepay.checkdetail.open.pay

import com.sohohouse.seven.common.relaxedMockk
import com.sohohouse.seven.housepay.CheckRepo
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.models.housepay.Check
import com.sohohouse.seven.network.core.models.housepay.PayCheckByCardInfo
import com.sohohouse.seven.payment.repo.CardRepo
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class PayCheckImplTest {
    @MockK
    lateinit var cardRepo: CardRepo

    @MockK
    lateinit var checkRepo: CheckRepo

    @MockK
    lateinit var validateCheck: ValidateCheck

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `when check is invalid, invoke returns the correct result`() {
        runBlockingTest {
            //GIVEN
            val check = relaxedMockk<Check>()
            every { validateCheck.invoke(check) } returns PayCheckError.NoAmount
            val cut = PayCheckImpl(validateCheck, cardRepo, checkRepo)
            coEvery { cardRepo.getPaymentMethods(forceRefresh = true) } returns ApiResponse.Success(
                listOf()
            )

            //WHEN
            val result = cut.invoke(
                PayCheckParams(
                    check = check,
                    payCheckPaymentInfo = relaxedMockk(),
                    amountCents = 0,
                    creditCents = 0,
                    tipCents = 0
                )
            )

            //THEN
            Assert.assertTrue(result is PayCheckResult.PayCheckFailure)
            Assert.assertTrue((result as PayCheckResult.PayCheckFailure).error is PayCheckError.NoAmount)
        }

    }

    @Test
    fun `when payment info is null, invoke returns the correct result`() {
        runBlockingTest {
            //GIVEN
            val check = relaxedMockk<Check>()
            every { validateCheck.invoke(check) } returns null
            val cut = PayCheckImpl(validateCheck, cardRepo, checkRepo)
            coEvery { cardRepo.getPaymentMethods(forceRefresh = true) } returns ApiResponse.Success(
                listOf()
            )

            //WHEN
            val result = cut.invoke(
                PayCheckParams(
                    check = check,
                    payCheckPaymentInfo = null,
                    amountCents = 500,
                    creditCents = 0,
                    tipCents = 0
                )
            )

            //THEN
            Assert.assertTrue(result is PayCheckResult.PayCheckFailure)
            Assert.assertTrue(
                (result as PayCheckResult.PayCheckFailure).error is PayCheckError.NoPaymentMethod
            )
        }

    }

    @Test
    fun `when card sync fails, invoke returns the correct result`() {
        runBlockingTest {
            //GIVEN
            val check = relaxedMockk<Check>()
            every { validateCheck.invoke(check) } returns null
            val cut = PayCheckImpl(validateCheck, cardRepo, checkRepo)
            coEvery { cardRepo.getPaymentMethods(forceRefresh = true) } returns ApiResponse.Error()

            //WHEN
            val result = cut.invoke(
                PayCheckParams(
                    check = check,
                    payCheckPaymentInfo = relaxedMockk(),
                    amountCents = 5000,
                    creditCents = 0,
                    tipCents = 0
                )
            )

            //THEN
            Assert.assertTrue(result is PayCheckResult.PayCheckFailure)
            Assert.assertTrue(
                (result as PayCheckResult.PayCheckFailure).error is PayCheckError.CardSyncFailed
            )
        }
    }

    @Test
    fun `when check is null, invoke returns the correct result`() {
        runBlockingTest {
            //GIVEN
            val check = null
            val cut = PayCheckImpl(validateCheck, cardRepo, checkRepo)

            //WHEN
            val result = cut.invoke(
                PayCheckParams(
                    check = check,
                    payCheckPaymentInfo = relaxedMockk(),
                    amountCents = 5000,
                    creditCents = 0,
                    tipCents = 0
                )
            )

            //THEN
            Assert.assertTrue(result is PayCheckResult.PayCheckFailure)
            Assert.assertTrue(
                (result as PayCheckResult.PayCheckFailure).error is PayCheckError.NoCheck
            )
        }
    }

    @Test
    fun `when payment fails and redownload check succeeds, invoke returns the correct result`() {
        runBlockingTest {
            //GIVEN
            val checkId = "testCheckId"
            val check = relaxedMockk<Check>().also {
                every { it.id } returns checkId
            }
            val cardId = "testCardId"
            val payCheckPaymentInfo = PayCheckPaymentInfo.Card(cardId)

            val amountCents = 5000
            val creditCents = 0
            val tipCents = 0

            every { validateCheck.invoke(check) } returns null

            coEvery { cardRepo.getPaymentMethods(forceRefresh = true) } returns ApiResponse.Success(
                listOf()
            )

            coEvery {
                checkRepo.payCheckByCard(
                    checkId = checkId,
                    cardId = cardId,
                    cardAmountCents = amountCents,
                    tipCents = tipCents,
                    creditCents = creditCents
                )
            } returns ApiResponse.Error()

            coEvery {
                checkRepo.getCheck(checkId)
            } returns ApiResponse.Success(check)

            val cut = PayCheckImpl(validateCheck, cardRepo, checkRepo)

            //WHEN
            val result = cut.invoke(
                PayCheckParams(
                    check = check,
                    payCheckPaymentInfo = payCheckPaymentInfo,
                    amountCents = amountCents,
                    creditCents = creditCents,
                    tipCents = tipCents
                )
            )

            //THEN
            Assert.assertTrue(result is PayCheckResult.PayCheckFailure)
            Assert.assertEquals(
                check,
                (result as PayCheckResult.PayCheckFailure).check
            )

        }
    }

    @Test
    fun `when payment fails and redownload check fails, invoke returns the correct result`() {
        runBlockingTest {
            //GIVEN
            val checkId = "testCheckId"
            val check = relaxedMockk<Check>().also {
                every { it.id } returns checkId
            }
            val cardId = "testCardId"
            val payCheckPaymentInfo = PayCheckPaymentInfo.Card(cardId)

            val amountCents = 5000
            val creditCents = 0
            val tipCents = 0

            every { validateCheck.invoke(check) } returns null

            coEvery { cardRepo.getPaymentMethods(forceRefresh = true) } returns ApiResponse.Success(
                listOf()
            )

            coEvery {
                checkRepo.payCheckByCard(
                    checkId = checkId,
                    cardId = cardId,
                    cardAmountCents = amountCents,
                    tipCents = tipCents,
                    creditCents = creditCents
                )
            } returns ApiResponse.Error()

            coEvery {
                checkRepo.getCheck(checkId)
            } returns ApiResponse.Error()

            val cut = PayCheckImpl(validateCheck, cardRepo, checkRepo)

            //WHEN
            val result = cut.invoke(
                PayCheckParams(
                    check = check,
                    payCheckPaymentInfo = payCheckPaymentInfo,
                    amountCents = amountCents,
                    creditCents = creditCents,
                    tipCents = tipCents
                )
            )

            //THEN
            Assert.assertTrue(result is PayCheckResult.PayCheckFailure)
            Assert.assertNull(
                (result as PayCheckResult.PayCheckFailure).check
            )

        }
    }
}