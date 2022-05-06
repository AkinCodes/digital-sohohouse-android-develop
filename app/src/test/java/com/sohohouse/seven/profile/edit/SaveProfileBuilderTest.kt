package com.sohohouse.seven.profile.edit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sohohouse.seven.network.core.models.Interest
import com.sohohouse.seven.profile.ProfileField
import com.sohohouse.seven.profile.ProfileTestHelper
import com.sohohouse.seven.profile.QuestionAndAnswer
import com.sohohouse.seven.profile.SocialMediaItem
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SaveProfileBuilderTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `result accurately reflects changes`() {
        val existingProfile = ProfileTestHelper.fullProfile()

        val newPhone = "07765811389"

        val newOccupation = "Engineer"
        val newIndustry = IndustryOption("Construction")
        val newAskMeAbout = "Anything"
        val newBio = QuestionAndAnswer("Question", "Answer")
        val newCity = "Krakow"
        val newInterests = listOf(Interest("Volleyball"), Interest("Tennis"))
        val newIgUrl = "https://instagram.com/spbt"
        val newIgHandle = "spbt"
        val fields = listOf(
            ProfileField.Phone(newPhone),
            ProfileField.Occupation(newOccupation),
            ProfileField.Industry(newIndustry),
            ProfileField.AskMeAbout(newAskMeAbout),
            ProfileField.Question(newBio),
            ProfileField.City(newCity),
            ProfileField.Interests(newInterests),
            ProfileField.SocialMedia(
                listOf(
                    SocialMediaItem(
                        SocialMediaItem.Type.INSTAGRAM,
                        newIgUrl,
                        newIgHandle
                    )
                ), optIn = false
            )
        )
        val updateObject = SaveProfileBuilder.buildProfileForSave(existingProfile, fields, true)
        val newProfile = updateObject.profile
        val accountUpdateObject = updateObject.accountUpdate

        assertEquals(newOccupation, newProfile.occupation)
        assertEquals(newIndustry.value, newProfile.industry)
        assertEquals(newAskMeAbout, newProfile.askMeAbout)
        assertEquals(newBio.answer, newProfile.bio)
        assertEquals(newBio.question, newProfile.bioQuestion)
        assertEquals(newCity, newProfile.city)
        assertEquals(newIgUrl, newProfile.instagramUrl)
        assertEquals(newIgHandle, newProfile.instagramHandle)
        assertEquals(newIgHandle, newProfile.instagramHandle)

        assertEquals(newPhone, accountUpdateObject!!.phoneNumber)
    }

    @Test
    fun `result includes account update when an account field was changed`() {
        val existingProfile = ProfileTestHelper.fullProfile()
        val newPhone = "07765811389"
        val fields = listOf(
            ProfileField.Phone(newPhone)
        )
        val updateObject = SaveProfileBuilder.buildProfileForSave(existingProfile, fields, true)

        assertTrue(updateObject.includesAccountUpdate)
    }

    @Test
    fun `result does not include account update when no account field was changed`() {
        val existingProfile = ProfileTestHelper.fullProfile()
        val fields = listOf(ProfileField.Question(QuestionAndAnswer("Question", "Answer")))
        val updateObject = SaveProfileBuilder.buildProfileForSave(existingProfile, fields, true)

        assertFalse(updateObject.includesAccountUpdate)
    }

    @Test
    fun `builder does not overwrite existing fields if they are not provided`() {
        val existingProfile = ProfileTestHelper.fullProfile()

        val newPhone = "07765811389"

        val newOccupation = "Engineer"
        val newIndustry = IndustryOption("Construction")
        val newCity = "Berlin"
        val fields = listOf(
            ProfileField.Phone(newPhone),
            ProfileField.Occupation(newOccupation),
            ProfileField.Industry(newIndustry),
            ProfileField.City(newCity)
        )

        val updateObject = SaveProfileBuilder.buildProfileForSave(existingProfile, fields, true)
        val newProfile = updateObject.profile
        val accountUpdateObject = updateObject.accountUpdate

        assertEquals(newOccupation, newProfile.occupation)
        assertEquals(newIndustry.value, newProfile.industry)
        assertEquals(newCity, newProfile.city)

        //Ensure these fields which were not provided were not overwritten
        assertEquals(existingProfile.askMeAbout, newProfile.askMeAbout)
        assertEquals(existingProfile.interestsResource, newProfile.interestsResource)
        assertEquals(existingProfile.instagramUrl, newProfile.instagramUrl)
        assertEquals(existingProfile.instagramHandle, newProfile.instagramHandle)
        assertEquals(existingProfile.spotifyUrl, newProfile.spotifyUrl)
        assertEquals(existingProfile.spotifyHandle, newProfile.spotifyHandle)
        assertEquals(existingProfile.youtubeUrl, newProfile.youtubeUrl)
        assertEquals(existingProfile.youtubeHandle, newProfile.youtubeHandle)
        assertEquals(existingProfile.linkedInHandle, newProfile.linkedInHandle)
        assertEquals(existingProfile.linkedInUrl, newProfile.linkedInUrl)
        assertEquals(existingProfile.website, newProfile.website)

        assertEquals(newPhone, accountUpdateObject!!.phoneNumber)
    }
}