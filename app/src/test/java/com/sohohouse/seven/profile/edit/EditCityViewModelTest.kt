package com.sohohouse.seven.profile.edit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.verify
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.mock
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.places.City
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.concurrent.TimeUnit

@RunWith(JUnit4::class)
class EditCityViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var analyticsManager: AnalyticsManager

    init {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun `when text changed, repo is queried for suggestions and they are emitted`() {
        val repo = mock<CitiesRepository>()

        val filter = "Lo"

        val cities = listOf(City("London"), City("Louisiana"))

        Mockito.`when`(repo.getCities(any(), eq(filter)))
            .thenReturn(Single.just(value(cities)))

        val scheduler = TestScheduler()
        val viewModel = EditCityViewModel(analyticsManager, repo, scheduler)

        val observer = mock<Observer<List<AutoCompleteSuggestion>>>()

        viewModel.autoCompleteSuggestions.observeForever(observer)

        viewModel.onTextChange(filter)

        scheduler.advanceTimeBy(
            AutoCompleteViewModel.DEFAULT_INPUT_TO_QUERY_DEBOUNCE_MS,
            TimeUnit.MILLISECONDS
        )

        verify(repo).getCities(any(), eq(filter))
        verify(observer).onChanged(Mockito.anyList())
    }


}