package com.sohohouse.seven.home.completeyourprofile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sohohouse.seven.common.prefs.PrefsManager
import com.sohohouse.seven.profile.ProfileTestHelper
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

class SetUpAppPromptItemFactoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    lateinit var prefsManager: PrefsManager

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `unedited profile and un-customised notifications should return correct items`() {
        every { prefsManager.notificationsCustomised } returns false
        val profile = ProfileTestHelper.emptyProfile()
        val factory = SetUpAppPromptItemFactory(profile, prefsManager)

        val result = factory.createItems()

        assertEquals(2, result.size)
        assertEquals(result[0].prompt, SetUpAppPromptItem.Prompt.COMPLETE_PROFILE)
        assertEquals(result[1].prompt, SetUpAppPromptItem.Prompt.CUSTOMISE_NOTIFICATIONS)
    }

    @Test
    fun `unedited profile and customised notifications return correct items`() {
        every { prefsManager.notificationsCustomised } returns true
        val profile = ProfileTestHelper.emptyProfile()
        val factory = SetUpAppPromptItemFactory(profile, prefsManager)

        val result = factory.createItems()

        assertEquals(1, result.size)
        assertEquals(result[0].prompt, SetUpAppPromptItem.Prompt.COMPLETE_PROFILE)
    }

    @Test
    fun `edited profile and un-customised notifications return correct items`() {
        every { prefsManager.notificationsCustomised } returns false
        val profile = ProfileTestHelper.fullProfile()
        val factory = SetUpAppPromptItemFactory(profile, prefsManager)

        val result = factory.createItems()

        assertEquals(1, result.size)
        assertEquals(result[0].prompt, SetUpAppPromptItem.Prompt.CUSTOMISE_NOTIFICATIONS)
    }

    @Test
    fun `edited profile and customised notifications return correct items`() {
        every { prefsManager.notificationsCustomised } returns true
        val profile = ProfileTestHelper.fullProfile()
        val factory = SetUpAppPromptItemFactory(profile, prefsManager)

        val result = factory.createItems()

        assertEquals(0, result.size)
    }

}