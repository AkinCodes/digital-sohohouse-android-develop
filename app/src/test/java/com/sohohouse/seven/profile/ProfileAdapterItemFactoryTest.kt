package com.sohohouse.seven.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sohohouse.seven.common.extensions.one
import com.sohohouse.seven.common.utils.EmptyStringProvider
import com.sohohouse.seven.network.core.models.Account
import com.sohohouse.seven.network.core.models.Profile
import com.sohohouse.seven.profile.ProfileTestHelper.fullMockProfile
import com.sohohouse.seven.profile.ProfileTestHelper.profileJustBiometrics
import com.sohohouse.seven.profile.edit.EditProfileAdapterItem
import com.sohohouse.seven.profile.view.ViewProfileAdapterItem
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

@RunWith(JUnit4::class)
class ProfileAdapterItemFactoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private fun getMockIndustryOptions(): List<String> {
        return ProfileTestHelper.getMockIndustryOptions()
    }

    private fun getMockAccount(): Account {
        return ProfileTestHelper.fullMockProfileAccount()
    }

    @Test
    fun `factory should include all fields when present and be sorted correctly`() {
        val profile = fullMockProfile()

        val result = ProfileAdapterItemFactory.createViewProfileItems(
            profile = profile,
            stringProvider = EmptyStringProvider()
        )

        assertEquals(4, result.size)

        assertTrue(result.one { it is ViewProfileAdapterItem.Field && it.field is ProfileField.Industry })
        assertTrue(result.one { it is ViewProfileAdapterItem.Field && it.field is ProfileField.Question })
        assertTrue(result.one { it is ViewProfileAdapterItem.Field && it.field is ProfileField.AskMeAbout })
        assertTrue(result.one { it is ViewProfileAdapterItem.Field && it.field is ProfileField.Interests })

        assertEquals(
            result.filterIsInstance<ViewProfileAdapterItem.Field>()
                .sortedBy { it.field.viewSortOrder },
            result.filterIsInstance<ViewProfileAdapterItem.Field>()
        )
    }

    @Test
    fun `factory should omit fields when not present`() {
        val profile = profileJustBiometrics()

        val result = ProfileAdapterItemFactory.createViewProfileItems(
            profile = profile,
            stringProvider = EmptyStringProvider()
        )

        assertFalse(result.any { it is ViewProfileAdapterItem.Field && it.field is ProfileField.Industry })
        assertFalse(result.any { it is ViewProfileAdapterItem.Field && it.field is ProfileField.Question })
        assertFalse(result.any { it is ViewProfileAdapterItem.Field && it.field is ProfileField.AskMeAbout })
        assertFalse(result.any { it is ViewProfileAdapterItem.Field && it.field is ProfileField.Interests })
        assertFalse(result.any { it is ViewProfileAdapterItem.Field && it.field is ProfileField.SocialMedia })
    }

    @Test
    fun `factory should include connected accounts item if just one url present`() {
        val profile = Profile(
            _firstName = "Peter",
            _lastName = "M",
            _imageUrl = "test.com/img",
            instagramUrl = "iurl",
            socialsOptIn = true
        )

        val result = ProfileAdapterItemFactory.createConnectedAccount(profile = profile)

        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `factory should always include all edit items and in correct order`() {
        ProfileAdapterItemFactory
            .createEditProfileItems(
                fullMockProfile(), getMockAccount(), getMockIndustryOptions(),
                ProfileTestHelper.getMockQuestions().toMutableList(), EmptyStringProvider()
            ).let {
                testResultEditItems(it)
            }

        ProfileAdapterItemFactory
            .createEditProfileItems(
                profileJustBiometrics(), getMockAccount(), getMockIndustryOptions(),
                ProfileTestHelper.getMockQuestions().toMutableList(), EmptyStringProvider()
            ).let {
                testResultEditItems(it)
            }
    }

    @Test
    fun `factory should not emit social media items for my profile with saved socials but socials opt in set to false`() {
        fullMockProfile().apply { Mockito.`when`(socialsOptIn).thenReturn(false) }.let { profile ->
            ProfileAdapterItemFactory.createViewProfileItems(
                profile = profile,
                EmptyStringProvider()
            ).let { items ->
                assertTrue(items.none { it is ViewProfileAdapterItem.ConnectedAccounts })
            }
        }
    }

    @Test
    fun `factory should emit social media items for my profile with saved socials and socials opt in set to true`() {
        val profile = fullMockProfile()
        Mockito.`when`(profile.socialsOptIn).thenReturn(true)
        assertTrue(ProfileAdapterItemFactory.createConnectedAccount(profile = profile).isNotEmpty())
    }

    private fun testResultEditItems(result: List<EditProfileAdapterItem>) {
        assertEquals(18, result.size)

        var i = 0
        assertTrue(result[i++] is EditProfileAdapterItem.Header)

        assertTrue(result[i++].let { it is EditProfileAdapterItem.Field<*> && it.field is ProfileField.Occupation })
        assertTrue(result[i++].let { it is EditProfileAdapterItem.Field<*> && it.field is ProfileField.Industry })
        assertTrue(result[i++].let { it is EditProfileAdapterItem.Field<*> && it.field is ProfileField.City })
        assertTrue(result[i++].let { it is EditProfileAdapterItem.Field<*> && it.field is ProfileField.Pronouns })
        assertTrue(result[i++].let { it is EditProfileAdapterItem.Field<*> && it.field is ProfileField.AskMeAbout })
        assertTrue(result[i++].let { it is EditProfileAdapterItem.Field<*> && it.field is ProfileField.Interests })
        assertTrue(result[i++].let { it is EditProfileAdapterItem.Field<*> && it.field is ProfileField.SocialMedia })

        assertTrue(result[i++].let { it is EditProfileAdapterItem.SectionHeader })
        assertTrue(result[i++].let { it is EditProfileAdapterItem.Question })
        assertTrue(result[i++].let { it is EditProfileAdapterItem.Question })
        assertTrue(result[i++].let { it is EditProfileAdapterItem.Question })

        assertTrue(result[i++] is EditProfileAdapterItem.PrivateInfoHeader)
        assertTrue(result[i++].let { it is EditProfileAdapterItem.Field<*> && it.field is ProfileField.Name })
        assertTrue(result[i++].let { it is EditProfileAdapterItem.Field<*> && it.field is ProfileField.Dob })
        assertTrue(result[i++].let { it is EditProfileAdapterItem.Field<*> && it.field is ProfileField.Address })
        assertTrue(result[i++].let { it is EditProfileAdapterItem.Field<*> && it.field is ProfileField.Phone })
        assertTrue(result[i] is EditProfileAdapterItem.LegalDisclaimer)
    }
}