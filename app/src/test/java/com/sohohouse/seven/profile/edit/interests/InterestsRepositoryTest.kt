package com.sohohouse.seven.profile.edit.interests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.verify
import com.sohohouse.seven.common.mock
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.CoreRequestFactory
import com.sohohouse.seven.network.core.models.Interest
import com.sohohouse.seven.network.core.request.GetInterestsRequest
import io.reactivex.Single
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

@RunWith(JUnit4::class)
class InterestsRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `when repo is queried for interests, should fetch and return results from API`() {
        val coreReqFactory = mock<CoreRequestFactory>()

        val interests = listOf(Interest("Football"), Interest("Food"))
        Mockito.`when`(coreReqFactory.create(any<GetInterestsRequest>()))
            .thenReturn(Single.just(value(interests)))

        val repo = InterestsRepository(coreReqFactory)

        val result = repo.getInterests("Foo")

        verify(coreReqFactory).create(any<GetInterestsRequest>())
        val testObserver = result.test()
        testObserver.assertValue { it is Either.Value && it.value == interests }
    }

}