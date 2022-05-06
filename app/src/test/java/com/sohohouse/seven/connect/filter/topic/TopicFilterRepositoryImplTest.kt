package com.sohohouse.seven.connect.filter.topic

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.sohohouse.seven.common.extensions.one
import com.sohohouse.seven.common.relaxedMockk
import com.sohohouse.seven.common.user.MembershipType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.EmptyStringProvider
import com.sohohouse.seven.connect.filter.base.Filterable
import io.mockk.CapturingSlot
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TopicFilterRepositoryImplTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    lateinit var userManager: UserManager
    val stringProvider = EmptyStringProvider()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `repo emits u27 tag for u27 member`() {
        every { userManager.membershipType } returns MembershipType.U27.name
        val repo = TopicFilterRepositoryImpl(stringProvider, userManager)
        val observer = relaxedMockk<Observer<List<Filterable>>>()
        repo.items.observeForever(observer)
        val captor = CapturingSlot<List<Filterable>>()
        verify { observer.onChanged(capture(captor)) }
        assertTrue(captor.captured.one { it.id == Topic.U27.id })
    }

    @Test
    fun `repo does not emit u27 tag for non-u27 member`() {
        every { userManager.membershipType } returns MembershipType.REGULAR.name
        val repo = TopicFilterRepositoryImpl(stringProvider, userManager)
        val observer = relaxedMockk<Observer<List<Filterable>>>()
        repo.items.observeForever(observer)
        val captor = CapturingSlot<List<Filterable>>()
        verify { observer.onChanged(capture(captor)) }
        assertTrue(captor.captured.none { it.id == Topic.U27.id })
    }
}