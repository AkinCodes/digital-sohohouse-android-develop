package com.sohohouse.seven.profile.view.connect

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.utils.TestCoroutineRule
import com.sohohouse.seven.connect.mynetwork.ConnectionRepository
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.models.MutualConnectionRequests
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ConnectRequestViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @MockK(relaxed = true)
    private lateinit var repo: ConnectionRepository

    @MockK(relaxed = true)
    lateinit var analyticsManager: AnalyticsManager

    private lateinit var viewModel: ConnectRequestViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        every { analyticsManager.logEventAction(any(), any()) } returns Unit
        viewModel = ConnectRequestViewModel(
            "member_id",
            repo,
            analyticsManager,
            testCoroutineRule.testCoroutineDispatcher
        )
    }


    @Test
    fun `view model emits success when connect request succeeds`() = runBlockingTest {
        coEvery { repo.postConnectionRequest(any()) } returns Either.Value(MutualConnectionRequests())

        val observer = mockk<Observer<Any>>(relaxed = true)
        viewModel.requestSent.observeForever(observer)

        val errorObserver = mockk<Observer<String>>(relaxed = true)
        viewModel.error.observeForever(errorObserver)

        viewModel.sendRequest()

        verify(exactly = 1) { observer.onChanged(any()) }
        verify(exactly = 0) { errorObserver.onChanged(any()) }
    }

    @Test
    fun `view model emits error when connect request fails`() = runBlockingTest {
        coEvery { repo.postConnectionRequest(any()) } returns Either.Error(ServerError.BAD_REQUEST)

        val observer = mockk<Observer<Any>>(relaxed = true)
        viewModel.requestSent.observeForever(observer)

        val errorObserver = mockk<Observer<String>>(relaxed = true)
        viewModel.error.observeForever(errorObserver)

        viewModel.sendRequest()

        verify(exactly = 0) { observer.onChanged(any()) }
        verify(exactly = 1) { errorObserver.onChanged(any()) }
    }

    @Test
    fun `log analytics event when cancelling message`() = runBlockingTest {

        viewModel.onCancelComposeMessage()
        CapturingSlot<AnalyticsManager.Action>().let { slot ->
            verify { analyticsManager.logEventAction(capture(slot), any()) }
            assert(slot.captured == AnalyticsManager.Action.ProfileConnectMessageCancel)
        }
    }

    @Test
    fun `log analytics event when writing message`() = runBlockingTest {
        viewModel.onClickWriteMessage()
        CapturingSlot<AnalyticsManager.Action>().let { slot ->
            verify { analyticsManager.logEventAction(capture(slot), any()) }
            assert(slot.captured == AnalyticsManager.Action.ProfileConnectWriteMessage)
        }
    }
}