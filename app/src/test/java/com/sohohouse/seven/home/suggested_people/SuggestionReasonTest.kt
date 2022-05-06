package com.sohohouse.seven.home.suggested_people

import com.sohohouse.seven.network.core.models.RecommendationDto
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test

class SuggestionReasonTest {

    @MockK(relaxed = true)
    lateinit var recommendationDto: RecommendationDto

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `unknown reasons return NONE`() {
        every { recommendationDto.reasons }.returns(listOf("something", "two"))
        val suggestedPeopleItem = SuggestedPeopleAdapterItem.NormalItem(recommendationDto)
        assert(suggestedPeopleItem.reason == SuggestionReason.NONE)

        every { recommendationDto.reasons }.returns(null)
        val suggestedPeopleItem1 = SuggestedPeopleAdapterItem.NormalItem(recommendationDto)
        assert(suggestedPeopleItem1.reason == SuggestionReason.NONE)

        every { recommendationDto.reasons }.returns(listOf("house_local"))
        val suggestedPeopleItem2 = SuggestedPeopleAdapterItem.NormalItem(recommendationDto)
        assert(suggestedPeopleItem2.reason == SuggestionReason.HOUSE_LOCAL)

    }
}