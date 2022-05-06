package com.sohohouse.seven.base

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sohohouse.sendbird.SendBirdHelper
import com.sohohouse.sendbird.repo.SendBirdChatConnectionRepoImpl
import com.sohohouse.seven.common.utils.TestCoroutineRule
import com.sohohouse.seven.network.chat.model.MiniProfile
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.models.SendBirdToken
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SendBirdConnectTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @MockK(relaxed = true)
    private lateinit var connectionRepo: SendBirdChatConnectionRepoImpl

    @MockK(relaxed = true)
    lateinit var sohoApiService: SohoApiService

    @MockK(relaxed = true)
    lateinit var sbHelper: SendBirdHelper

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        connectionRepo = SendBirdChatConnectionRepoImpl(sohoApiService, sbHelper)
    }

    @Test
    fun `sendbird connect invokes only once`() = runBlockingTest {

        val profile = mockk<MiniProfile>(relaxed = true)
        val sendBirdToken = mockk<SendBirdToken>(relaxed = true)

        coEvery { sohoApiService.getSendBirdAccessToken() } returns ApiResponse.Success(
            sendBirdToken
        )

        connectionRepo.connect(profile)
        connectionRepo.connect(profile)
        connectionRepo.connect(profile)

        coVerify(exactly = 1) { sohoApiService.getSendBirdAccessToken() }
    }
}