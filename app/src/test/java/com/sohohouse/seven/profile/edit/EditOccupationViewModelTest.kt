package com.sohohouse.seven.profile.edit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.verify
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.mock
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.models.Occupation
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
class EditOccupationViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var analyticsManager: AnalyticsManager

    init {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun `when text changed, repo is queried for suggestions and they are emitted`() {
        val repo = mock<OccupationsRepository>()

        val filter = "Pro"

        val occupations = listOf(Occupation("Product Manager"), Occupation("Project Manager"))

        Mockito.`when`(repo.getOccupations(filter))
            .thenReturn(Single.just(value(occupations)))

        val scheduler = TestScheduler()
        val viewModel = EditOccupationViewModel(repo, scheduler, analyticsManager)

        val observer = mock<Observer<List<AutoCompleteSuggestion>>>()

        viewModel.autoCompleteSuggestions.observeForever(observer)

        viewModel.onTextChange(filter)

        scheduler.advanceTimeBy(
            AutoCompleteViewModel.DEFAULT_INPUT_TO_QUERY_DEBOUNCE_MS,
            TimeUnit.MILLISECONDS
        )

        verify(repo).getOccupations(filter)
        verify(observer).onChanged(Mockito.anyList())
    }


}