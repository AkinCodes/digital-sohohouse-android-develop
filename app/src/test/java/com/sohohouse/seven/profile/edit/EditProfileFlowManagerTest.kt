package com.sohohouse.seven.profile.edit

import android.text.InputType
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.utils.EmptyStringProvider
import com.sohohouse.seven.network.core.models.Interest
import com.sohohouse.seven.profile.ProfileField
import com.sohohouse.seven.profile.ProfileTestHelper
import com.sohohouse.seven.profile.SocialMediaItem
import com.sohohouse.seven.profile.edit.interests.EditInterestsBottomSheet
import com.sohohouse.seven.profile.edit.socialmedia.EditSocialMediaBottomSheet
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class EditProfileFlowManagerTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var flowManager: EditProfileFlowManager

    @Mock
    lateinit var eventTracking: AnalyticsManager

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        flowManager = EditProfileFlowManager(eventTracking)
    }


    @Test
    fun `for editing ask me about, flow manager return text area bottom sheet`() {
        val askMeAboutField = ProfileField.AskMeAbout("Test Value")
        val result = flowManager.createEditorBottomSheet(askMeAboutField, EmptyStringProvider())
        assertTrue(result is TextAreaBottomSheet.Companion.Factory)
        val typed = result as TextAreaBottomSheet.Companion.Factory
        assertEquals(askMeAboutField.getEditDisplayValue(EmptyStringProvider()), typed.currentValue)
        assertEquals(askMeAboutField.placeholder, typed.hint)
        assertEquals(askMeAboutField.maxChars, typed.maxChars)
        assertEquals(askMeAboutField.getLabel(EmptyStringProvider()), typed.title)
        assertEquals(EditProfileFlowManager.REQ_CODE_EDIT_ASK_ME_ABOUT, typed.requestCode)
    }

    @Test
    fun `for editing connected accounts, flow manager returns correct bottom sheet`() {
        val items = ProfileTestHelper.createSocialMediaItems()
        val field = ProfileField.SocialMedia(items, optIn = false)

        val result = flowManager.createEditorBottomSheet(field, EmptyStringProvider())
        assertTrue(result is EditSocialMediaBottomSheet.Companion.Factory)

        val typed = result as EditSocialMediaBottomSheet.Companion.Factory

        val socialItems = field.data.associateBy { it.type }
        val factoryData = typed.field.data.associateBy { it.type }
        assertEquals(
            socialItems.get(SocialMediaItem.Type.INSTAGRAM)?.handle,
            factoryData.get(SocialMediaItem.Type.INSTAGRAM)?.handle
        )
        assertEquals(
            socialItems.get(SocialMediaItem.Type.TWITTER)?.handle,
            factoryData.get(SocialMediaItem.Type.TWITTER)?.handle
        )
        assertEquals(
            socialItems.get(SocialMediaItem.Type.YOUTUBE)?.handle,
            factoryData.get(SocialMediaItem.Type.YOUTUBE)?.url
        )
        assertEquals(
            socialItems.get(SocialMediaItem.Type.WEBSITE)?.handle,
            factoryData.get(SocialMediaItem.Type.WEBSITE)?.url
        )
        assertEquals(
            socialItems.get(SocialMediaItem.Type.LINKEDIN)?.handle,
            factoryData.get(SocialMediaItem.Type.LINKEDIN)?.url
        )
        assertEquals(
            socialItems.get(SocialMediaItem.Type.SPOTIFY)?.handle,
            factoryData.get(SocialMediaItem.Type.SPOTIFY)?.url
        )
    }

    @Test
    fun `for editing occupation, flow manager returns correct bottom sheet`() {
        val field = ProfileField.Occupation("My occupation")

        val result = flowManager.createEditorBottomSheet(field, EmptyStringProvider())
        assertTrue(result is EditOccupationBottomSheet.Companion.Factory)

        val typed = result as EditOccupationBottomSheet.Companion.Factory

        assertEquals(field.getEditDisplayValue(EmptyStringProvider()), typed.initialValue)
    }

    @Test
    fun `for editing city, flow manager returns correct bottom sheet`() {
        val field = ProfileField.City("My city")

        val result = flowManager.createEditorBottomSheet(field, EmptyStringProvider())
        assertTrue(result is EditCityBottomSheet.Companion.Factory)

        val typed = result as EditCityBottomSheet.Companion.Factory

        assertEquals(field.getEditDisplayValue(EmptyStringProvider()), typed.initialValue)
    }

    @Test
    fun `for editing phone, flow manager return text area bottom sheet`() {
        val phoneField = ProfileField.Phone("+447765811389")
        val result = flowManager.createEditorBottomSheet(phoneField, EmptyStringProvider())
        assertTrue(result is TextAreaBottomSheet.Companion.Factory)
        val typed = result as TextAreaBottomSheet.Companion.Factory
        assertEquals(phoneField.getEditDisplayValue(EmptyStringProvider()), typed.currentValue)
        assertEquals(phoneField.placeholder, typed.hint)
        assertEquals(phoneField.maxChars, typed.maxChars)
        assertEquals(phoneField.getLabel(EmptyStringProvider()), typed.title)
        assertEquals(1, typed.maxLines)
        assertEquals(InputType.TYPE_CLASS_PHONE, typed.inputType)
        assertEquals(EditProfileFlowManager.REQ_CODE_EDIT_PHONE, typed.requestCode)
    }

    @Test
    fun `for editing interests, flow manager returns correct bottom sheet`() {
        val field = ProfileField.Interests(listOf(Interest("Football"), Interest("Food")))

        val result = flowManager.createEditorBottomSheet(field, EmptyStringProvider())
        assertTrue(result is EditInterestsBottomSheet.Companion.Factory)

        val typed = result as EditInterestsBottomSheet.Companion.Factory

        assertEquals(field.data, typed.currentInterests)
    }

}