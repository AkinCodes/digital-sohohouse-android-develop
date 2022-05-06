package com.sohohouse.seven.welcome

import android.content.Context
import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.verify
import com.sohohouse.seven.app.TestApp
import com.sohohouse.seven.authentication.AuthenticationActivity
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.mock
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApp::class)
class WelcomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var analyticsManager: AnalyticsManager

    @Mock
    private lateinit var context: Context

    private val viewModel: WelcomeViewModel by lazy {
        WelcomeViewModel(analyticsManager)
    }

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun `open sign in screen when clicking Sign up button`() {
        val observer = mock<Observer<Intent>>()
        viewModel.navigation.observeForever(observer)
        viewModel.onClickSignIn(context)

        val eventCapture = argumentCaptor<AnalyticsManager.Action>()
        verify(analyticsManager).logEventAction(eq(eventCapture.capture()))
        assert(eventCapture.firstValue == AnalyticsManager.Action.SignIn)

        val intentCapture = ArgumentCaptor.forClass(Intent::class.java)
        verify(observer).onChanged(intentCapture.capture())
        assert(intentCapture.value?.component?.className == AuthenticationActivity::class.java.name)
    }

}