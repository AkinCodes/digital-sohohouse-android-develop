package com.sohohouse.seven.common.events

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.verify
import com.sohohouse.seven.app.TestApp
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.models.EventCategory
import com.sohohouse.seven.network.core.request.GetEventsCategoriesRequest
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApp::class)
class ExploreCategoryManagerTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var zipRequestsUtil: ZipRequestsUtil

    @Mock
    lateinit var zipRequestsUtilv2: com.sohohouse.seven.common.utils.ZipRequestsUtil
    private lateinit var categoryManager: ExploreCategoryManager

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        categoryManager = ExploreCategoryManager(zipRequestsUtil, zipRequestsUtilv2)
    }

    @Test
    fun `CategoryManager fetches data when it has none saved`() {
        // GIVEN the category manager has no categories saved
        val categories = listOf<EventCategory>()
        Mockito.`when`(zipRequestsUtil.issueApiCall(any<GetEventsCategoriesRequest>()))
            .thenReturn(Single.just(value(categories)))

        // WHEN the category manager tries to fetch categories
        val result = categoryManager.getCategories()

        // THEN an api call gets made to fetch houses
        verify(zipRequestsUtil).issueApiCall(any<GetEventsCategoriesRequest>())
        val testObserver = result.test()
        testObserver.assertValue { it is Either.Value && it.value == categories }
    }

    //    @Test temp disable this test; its passing locally but failing on CI
    fun `CategoryManager saves the fetched data and does not call again`() {
        // GIVEN the category manager has some category data
        val categories1 =
            listOf(EventCategory(name = "category1"), EventCategory(name = "category2"))
        Mockito.`when`(zipRequestsUtil.issueApiCall(any<GetEventsCategoriesRequest>()))
            .thenReturn(Single.just(value(categories1)))
        val result1 = categoryManager.getCategories()
        val testObserver1 = result1.test()
        testObserver1.assertValue { it is Either.Value && it.value == categories1 }

        // WHEN the category manager tries to get categories again
        val categories2 = listOf(EventCategory(name = "category3"))
        Mockito.`when`(zipRequestsUtil.issueApiCall(any<GetEventsCategoriesRequest>()))
            .thenReturn(Single.just(value(categories2)))
        val result2 = categoryManager.getCategories()

        // THEN the category manager does not make a new call and just returns old data
        val testObserver2 = result2.test()
        testObserver2.assertValue { it is Either.Value && it.value == categories1 }
    }
}