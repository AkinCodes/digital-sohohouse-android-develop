package com.sohohouse.seven.authentication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.*
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.interactors.AccountInteractor
import com.sohohouse.seven.common.utils.TestCoroutineRule
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.models.SendVerificationLink
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class VerifyAccountViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    lateinit var accountInteractor: AccountInteractor

    @Mock
    lateinit var analyticsManager: AnalyticsManager

    val accountId = "testAccountID"

    val viewModel by lazy {
        VerifyAccountViewModel(
            analyticsManager,
            accountInteractor,
            Dispatchers.Unconfined
        )
    }

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun `when send verification link is triggered and succeeds, expected event is observed`() =
        runBlockingTest {
            //GIVEN
            Mockito.`when`(accountInteractor.sendVerificationLink(accountId))
                .thenReturn(value(SendVerificationLink()))

            val observer = com.sohohouse.seven.common.mock<Observer<Any>>()

            viewModel.verificationLinkSentEvent.observeForever(observer)

            val successObserver = com.sohohouse.seven.common.mock<Observer<Any>>()
            viewModel.showGenericErrorDialogEvent.observeForever(successObserver)

            val loadingObserver = com.sohohouse.seven.common.mock<Observer<LoadingState>>()
            viewModel.loadingState.observeForever(loadingObserver)

            //WHEN
            viewModel.onSendVerificationLinkClick(accountId)

            //THEN
            verify(loadingObserver, times(1)).onChanged(LoadingState.Loading)
            verify(observer, times(1)).onChanged(any())
            verify(loadingObserver, times(1)).onChanged(LoadingState.Idle)
            verify(successObserver, never()).onChanged(anyOrNull())
        }

    @Test
    fun `when send verification link is triggered and fails, expected event is observed`() =
        runBlockingTest {
            //GIVEN
            Mockito.`when`(accountInteractor.sendVerificationLink(accountId))
                .thenReturn(com.sohohouse.seven.network.base.model.error(ServerError.COMPLETE_MELTDOWN))

            val successObserver = com.sohohouse.seven.common.mock<Observer<Any>>()

            viewModel.verificationLinkSentEvent.observeForever(successObserver)

            val errorObserver = com.sohohouse.seven.common.mock<Observer<Any>>()
            viewModel.showGenericErrorDialogEvent.observeForever(errorObserver)

            val loadingObserver = com.sohohouse.seven.common.mock<Observer<LoadingState>>()
            viewModel.loadingState.observeForever(loadingObserver)

            //WHEN
            viewModel.onSendVerificationLinkClick(accountId)

            //THEN
            verify(loadingObserver, times(1)).onChanged(LoadingState.Loading)
            verify(successObserver, never()).onChanged(any())
            verify(errorObserver, times(1)).onChanged(anyOrNull())
            verify(loadingObserver, times(1)).onChanged(LoadingState.Idle)
        }
}

