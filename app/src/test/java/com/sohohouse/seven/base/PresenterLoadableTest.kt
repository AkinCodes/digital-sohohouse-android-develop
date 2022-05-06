package com.sohohouse.seven.base

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.verify
import com.sohohouse.seven.app.TestApp
import com.sohohouse.seven.base.load.LoadViewController
import com.sohohouse.seven.base.load.PresenterLoadable
import com.sohohouse.seven.common.views.LoadingView
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, application = TestApp::class)
class PresenterLoadableTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var presenterLoadable: DummyPresenter

    @Mock
    lateinit var loadViewController: LoadViewController

    @Mock
    lateinit var loadingView: LoadingView

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        presenterLoadable = DummyPresenter()
        presenterLoadable.attach(loadViewController)
        `when`(loadViewController.loadingView).thenReturn(loadingView)
    }

    @After
    fun tearDown() {
        Mockito.reset(loadViewController)
    }

    @Test
    fun `when call is executed show load`() {
        // GIVEN user wants to issue an expensive call
        val single = Single.just(Any())

        // WHEN presenter executes call
        presenterLoadable.executeExpensiveCall(single)

        // VERIFY loading is shown
        verify(loadViewController).showLoadingState()

    }

    @Test
    fun `when call completes successfully, hide loading`() {
        // GIVEN user wants to issue an expensive call, which completes successfully
        val single = Single.just(Any())

        // WHEN presenter executes call
        presenterLoadable.executeExpensiveCall(single)

        // VERIFY loading is hidden
        verify(loadViewController).hideLoadingState()
    }

    @Test
    fun `when call errors out, hide loading`() {
        // GIVEN user wants to issue an expensive call, which terminates on error
        val single = Single.error<IllegalStateException>(IllegalStateException())

        // WHEN presenter executes call
        presenterLoadable.executeExpensiveCall(single)

        // VERIFY loading is hidden
        verify(loadViewController).hideLoadingState()
    }

    class DummyPresenter
        : BasePresenter<LoadViewController>(), PresenterLoadable<LoadViewController> {

        fun <T> executeExpensiveCall(single: Single<T>) {
            single.compose(loadTransformer()).subscribe({

            }, {

            })

        }
    }
}

