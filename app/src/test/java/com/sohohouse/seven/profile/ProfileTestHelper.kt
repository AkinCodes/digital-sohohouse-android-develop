package com.sohohouse.seven.profile

import com.sohohouse.seven.common.mock
import com.sohohouse.seven.network.core.models.Account
import com.sohohouse.seven.network.core.models.Address
import com.sohohouse.seven.network.core.models.Interest
import com.sohohouse.seven.network.core.models.Profile
import moe.banana.jsonapi2.HasMany
import moe.banana.jsonapi2.ObjectDocument
import org.mockito.Mockito
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import com.sohohouse.seven.common.extensions.getFormattedDate
import java.util.*

object ProfileTestHelper {

    const val TEST_ID = "12345"

    const val TEST_PROFILE_ID = "TEST_PROFILE_ID"

    fun fullMockProfile(): Profile {
        return createMockProfile(
            id = TEST_ID,
            firstName = "Peter",
            lastName = "M",
            imageUrl = "test.com/img",
            instagramHandle = "pnmurphy",
            spotifyHandle = "pmurph",
            youtubeHandle = "ythandle",
            linkedInHandle = "linkedinha",
            linkedInUrl = "lurl",
            spotifyUrl = "surl",
            youtubeUrl = "yurl",
            twitterUrl = "turl",
            instagramUrl = "iurl",
            website = "pm.com",
            occupation = "Dev",
            city = "Dub",
            industry = "Tech",
            whatIcanOffer = "Foo",
            interests = listOf(Interest("Football"), Interest("Food")),
            bio = "Answer to bioQuestion",
            bioQuestion = getMockQuestions().first()
        )
    }

    fun fullProfile(): Profile {
        val document = ObjectDocument<Profile>()
        val profile = Profile(
            _firstName = "Peter",
            _lastName = "M",
            _imageUrl = "test.com/img",
            instagramHandle = "pnmurphy",
            spotifyHandle = "pmurph",
            youtubeHandle = "ythandle",
            linkedInHandle = "linkedinha",
            linkedInUrl = "lurl",
            spotifyUrl = "surl",
            youtubeUrl = "yurl",
            twitterUrl = "turl",
            instagramUrl = "iurl",
            website = "pm.com",
            occupation = "Dev",
            city = "Dub",
            industry = "Tech",
            askMeAbout = "Foo",
            interestsResource = HasMany(Interest("Football"), Interest("Food")),
            bio = "Short bio"
        )
        document.set(profile)
        profile.document = document
        return profile
    }

    fun emptyProfile(): Profile {
        val document = ObjectDocument<Profile>()
        val profile = Profile(
            _firstName = "Peter",
            _lastName = "M",
            _imageUrl = null,
            instagramHandle = null,
            spotifyHandle = null,
            youtubeHandle = null,
            linkedInHandle = null,
            linkedInUrl = null,
            spotifyUrl = null,
            youtubeUrl = null,
            twitterUrl = null,
            instagramUrl = null,
            website = null,
            occupation = null,
            city = null,
            industry = null,
            askMeAbout = null,
            interestsResource = null,
            bio = null
        ).apply { id = TEST_PROFILE_ID }
        document.set(profile)
        profile.document = document
        return profile
    }

    fun createSocialMediaItems(): List<SocialMediaItem> {
        return listOf(
            SocialMediaItem(SocialMediaItem.Type.INSTAGRAM, "www.ig.com/ighandle", "ighandle"),
            SocialMediaItem(SocialMediaItem.Type.INSTAGRAM, "www.twitter.com/twhandle", "twhandle"),
            SocialMediaItem(SocialMediaItem.Type.INSTAGRAM, "www.yt.com/ythandle", "ythandle"),
            SocialMediaItem(SocialMediaItem.Type.INSTAGRAM, "www.spotify.com/sphandle", "sphandle"),
            SocialMediaItem(
                SocialMediaItem.Type.INSTAGRAM,
                "www.linkedin.com/lihandle",
                "lihandle"
            ),
            SocialMediaItem(SocialMediaItem.Type.INSTAGRAM, "www.website.com")
        )
    }

    fun profileJustBiometrics() = createMockProfile(
        firstName = "peter",
        lastName = "Murphy",
        bioQuestion = "Question"
    )

    fun createMockProfile(
        id: String? = "",
        firstName: String? = "",
        lastName: String? = "",
        imageUrl: String? = "",
        occupation: String? = "",
        industry: String? = "",
        whatIcanOffer: String? = "",
        interests: List<Interest>? = listOf(),
        instagramHandle: String? = "",
        twitterHandle: String? = "",
        linkedInHandle: String? = "",
        spotifyHandle: String? = "",
        youtubeHandle: String? = "",
        instagramUrl: String? = "",
        twitterUrl: String? = "",
        linkedInUrl: String? = "",
        spotifyUrl: String? = "",
        youtubeUrl: String? = "",
        website: String? = "",
        bio: String? = "",
        city: String? = "",
        bioQuestion: String
    ): Profile {

        val profile = mock<Profile>()

        Mockito.`when`(profile.firstName).thenReturn(firstName)
        Mockito.`when`(profile.lastName).thenReturn(lastName)
        Mockito.`when`(profile.imageUrl).thenReturn(imageUrl)
        Mockito.`when`(profile.occupation).thenReturn(occupation)
        Mockito.`when`(profile.industry).thenReturn(industry)
        Mockito.`when`(profile.askMeAbout).thenReturn(whatIcanOffer)
        Mockito.`when`(profile.interests).thenReturn(interests)
        Mockito.`when`(profile.spotifyHandle).thenReturn(spotifyHandle)
        Mockito.`when`(profile.youtubeHandle).thenReturn(youtubeHandle)
        Mockito.`when`(profile.twitterHandle).thenReturn(twitterHandle)
        Mockito.`when`(profile.linkedInHandle).thenReturn(linkedInHandle)
        Mockito.`when`(profile.website).thenReturn(website)
        Mockito.`when`(profile.instagramHandle).thenReturn(instagramHandle)
        Mockito.`when`(profile.youtubeUrl).thenReturn(youtubeUrl)
        Mockito.`when`(profile.twitterUrl).thenReturn(twitterUrl)
        Mockito.`when`(profile.linkedInUrl).thenReturn(linkedInUrl)
        Mockito.`when`(profile.spotifyUrl).thenReturn(spotifyUrl)
        Mockito.`when`(profile.instagramUrl).thenReturn(instagramUrl)
        Mockito.`when`(profile.bio).thenReturn(bio)
        Mockito.`when`(profile.city).thenReturn(city)
        Mockito.`when`(profile.id).thenReturn(id)
        Mockito.`when`(profile.bioQuestion).thenReturn(bioQuestion)

        return profile
    }

    fun fullMockProfileAccount(): Account {
        return createMockAccount(
            mockDate(),
            Address(listOf("1 London St", "London"), postalCode = "E15KL", country = "UK"),
            "077658113333"
        )
    }

    fun createMockAccount(dob: Date, address: Address, phone: String): Account {

        val account = mockk<Account>()
        every { account.dateOfBirth } returns dob
        every { account.address } returns address
        every { account.phoneNumber } returns phone
        every { account.profile } returns fullProfile()

        return account
    }

    private fun mockDate(): Date {
        val date = mockk<Date>()

        mockkStatic("com.sohohouse.seven.common.extensions.DateKt")
        every { date.getFormattedDate() } returns ("15 Jan 1993")
        return date
    }

    fun getMockIndustryOptions(): List<String> {
        return listOf("Arts", "Tech", "Marketing")
    }

    fun getMockQuestions(): List<String> {
        return listOf("Favourite Soho House ...", "Favourite drink ...", "What  keeps me busy ...")
    }
}