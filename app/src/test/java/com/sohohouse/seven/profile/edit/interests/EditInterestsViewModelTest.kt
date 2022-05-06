package com.sohohouse.seven.profile.edit.interests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.relaxedMockk
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.models.Interest
import com.sohohouse.seven.profile.edit.interests.EditInterestsViewModel.InterestItem
import io.mockk.CapturingSlot
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class EditInterestsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @MockK
    lateinit var analyticsManager: AnalyticsManager

    @MockK
    lateinit var repo: InterestsRepository

    init {
        MockKAnnotations.init(this)
    }

    @Test
    fun `when viewModel is inited with selected interests, it emits all interests with those interests preselected`() {
        val viewModel = EditInterestsViewModel(analyticsManager, Dispatchers.Unconfined, repo)

        every { repo.getAllInterests() } returns value(allInterests)
        viewModel.init(myInterests)

        val itemsObserver = relaxedMockk<Observer<List<DiffItem>>>()
        val itemsSlot = CapturingSlot<List<DiffItem>>()

        viewModel.items.observeForever(itemsObserver)

        verify { itemsObserver.onChanged(capture(itemsSlot)) }
        assertEquals(10, itemsSlot.captured.size)
        assertEquals(
            myInterests.size,
            itemsSlot.captured.filterIsInstance(InterestItem::class.java).count { it.isSelected })
    }

    companion object {
        const val INTEREST_ID_FOOTBALL = "FOOTBALL"
        const val INTEREST_ID_TENNIS = "TENNIS"
        const val INTEREST_ID_COMEDY = "COMEDY"
        const val INTEREST_ID_GAMING = "GAMING"
        const val INTEREST_ID_HIKING = "HIKING"
        const val INTEREST_ID_SWIMMING = "SWIMMING"

        const val INTEREST_CATEGORY_SPORT = "SPORT"
        const val INTEREST_CATEGORY_ARTS = "ARTS"
        const val INTEREST_CATEGORY_GAMES = "GAMES"
        const val INTEREST_CATEGORY_OUTDOORS = "OUTDOORS"
    }

    private fun buildInterest(id: String, category: String) =
        Interest(id, category).apply { this.id = id }

    private val allInterests = listOf(
        buildInterest(INTEREST_ID_FOOTBALL, INTEREST_CATEGORY_SPORT),
        buildInterest(INTEREST_ID_GAMING, INTEREST_CATEGORY_GAMES),
        buildInterest(INTEREST_ID_SWIMMING, INTEREST_CATEGORY_SPORT),
        buildInterest(INTEREST_ID_HIKING, INTEREST_CATEGORY_OUTDOORS),
        buildInterest(INTEREST_ID_COMEDY, INTEREST_CATEGORY_ARTS),
        buildInterest(INTEREST_ID_TENNIS, INTEREST_CATEGORY_SPORT)
    )

    private val myInterests = listOf(
        buildInterest(INTEREST_ID_FOOTBALL, INTEREST_CATEGORY_SPORT),
        buildInterest(INTEREST_ID_TENNIS, INTEREST_CATEGORY_SPORT),
        buildInterest(INTEREST_ID_COMEDY, INTEREST_CATEGORY_ARTS)
    )

}