package com.sohohouse.seven.common.prefs

import android.content.res.AssetManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sohohouse.seven.network.core.CoreServiceModule
import com.sohohouse.seven.network.core.models.*
import com.squareup.moshi.Moshi
import io.mockk.*
import io.mockk.InternalPlatformDsl.toStr
import io.mockk.impl.annotations.MockK
import moe.banana.jsonapi2.ArrayDocument
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.ResourceIdentifier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

class VenueCacheTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    lateinit var fileCreator: FileCreator

    @MockK
    lateinit var assetManager: AssetManager

    @MockK
    lateinit var cacheDir: File
    lateinit var moshi: Moshi

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        moshi = setUpMoshi()
    }

    private fun setUpMoshi(): Moshi {
        return CoreServiceModule().providesMoshi()
    }

    @Test
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun `reading venues from cache deserializes data properly`() {
        val fileName = "venues"
        val assetsFileName = "venues.json"

        val testVenuesFileName = "test_venues.json"
        every { assetManager.open(assetsFileName) } returns
                javaClass.classLoader.getResourceAsStream(testVenuesFileName)
        every { fileCreator.file(cacheDir, fileName) } returns
                File(javaClass.classLoader.getResource(testVenuesFileName).path)

        val cut = VenueCache.Impl(
            fileName,
            assetsFileName,
            cacheDir,
            moshi,
            fileCreator,
            assetManager
        )

        val result = cut.read()
        assertTrue("Deserialized list is not empty", result.isNotEmpty())
        assertEquals(316, result.size)
        assertTrue(
            "Deserialized venues have document data",
            result.all { it.document != null }
        )

        val actual = result.first { it.id == "SD" }
        assertEquals(testVenue, actual)
        assertEquals(
            testHouse,
            actual.house.get(actual.document)
        )
    }

    @Test
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun `writing venues to cache serializes data properly`() {
        val fileName = "venues"
        val assetsFileName = "venues.json"

        val expectedWriteOutput = javaClass.classLoader
            .getResourceAsStream("venue_cache_test_data.json")
            .readBytes()
            .decodeToString()

        val testVenuesFileName = "test_venues.json"
        every { assetManager.open(assetsFileName) } returns
                javaClass.classLoader.getResourceAsStream(testVenuesFileName)
        val mockFile = mockk<File>(relaxed = true)
        mockkStatic("kotlin.io.FilesKt__FileReadWriteKt")
        every { mockFile.readText(any()) } returns ""
        every { fileCreator.file(cacheDir, fileName) } returns mockFile
        every { mockFile.writeBytes(any()) } returns Unit

        val venueCache = VenueCache.Impl(
            fileName,
            assetsFileName,
            cacheDir,
            moshi,
            fileCreator,
            assetManager
        )

        val venueObjects = testInput

        venueCache.write(venueObjects)
        verify { mockFile.writeText(expectedWriteOutput) }
    }

    private val testInput: List<Venue> get() = listOf(testVenue)

    //this object is mapped directly from the SD venue in test_venues.json
    private val testVenue: Venue
        get() {
            val venue = Venue(
                _name = "Shoreditch House",
                _description = "Set in a converted warehouse, Shoreditch House has a rooftop pool with views across the City and East End. Inside the club there are 26 bedrooms, a lounge, two bars, a gym, Cowshed spa and two restaurants.",
                _venueType = "HOUSE",
                venueAddress = VenueAddress(
                    lines = listOf(
                        "Ebor Street",
                        "Shoreditch"
                    ),
                    postalCode = "E1 6AW",
                    country = "United Kingdom"
                ),
                _region = "UK",
                _country = "United Kingdom",
                _city = "London",
                _phoneNumber = "+44 (0)20 7739 5040",
                _timeZone = "Europe/London",
                location = Location(
                    latitude = 51.523742f,
                    longitude = -0.075915f
                ),
                _venueColors = VenueColors(
                    house = "#9c1a38",
                    dark = "#9c1a38",
                    light = "#f6edee"
                ),
                _isTopLevel = true,
                _isActive = false,
                operatingHours = OperatingHours(
                    opensAt = "2021-11-09T08:00:00.000+00:00",
                    closesAt = "2021-11-09T23:00:00.000+00:00",
                    periods = listOf(
                        Period(
                            venueOpen = VenueTime(
                                _day = 1,
                                time = "0800"
                            ),
                            venueClose = VenueTime(
                                _day = 1,
                                time = "2300"
                            )
                        ),
                        Period(
                            venueOpen = VenueTime(
                                _day = 2,
                                time = "0800"
                            ),
                            venueClose = VenueTime(
                                _day = 2,
                                time = "2300"
                            )
                        ),
                        Period(
                            venueOpen = VenueTime(
                                _day = 3,
                                time = "0800"
                            ),
                            venueClose = VenueTime(
                                _day = 3,
                                time = "2300"
                            )
                        ),
                        Period(
                            venueOpen = VenueTime(
                                _day = 5,
                                time = "0800"
                            ),
                            venueClose = VenueTime(
                                _day = 5,
                                time = "2300"
                            )
                        ),
                        Period(
                            venueOpen = VenueTime(
                                _day = 4,
                                time = "0800"
                            ),
                            venueClose = VenueTime(
                                _day = 4,
                                time = "2300"
                            )
                        ),
                        Period(
                            venueOpen = VenueTime(
                                _day = 6,
                                time = "1000"
                            ),
                            venueClose = VenueTime(
                                _day = 6,
                                time = "2300"
                            )
                        ),
                        Period(
                            venueOpen = VenueTime(
                                _day = 0,
                                time = "1000"
                            ),
                            venueClose = VenueTime(
                                _day = 0,
                                time = "2300"
                            )
                        )
                    )
                ),
                VenueIcons(
                    "https://res.cloudinary.com/soho-house/image/upload/venues-staging/icons/png/light/sd.png",
                    "https://res.cloudinary.com/soho-house/image/upload/venues-staging/icons/svg/light/sd.svg",
                    "https://res.cloudinary.com/soho-house/image/upload/venues-staging/icons/png/dark/sd.png",
                    "https://res.cloudinary.com/soho-house/image/upload/venues-staging/icons/svg/dark/sd.svg"
                ),
                _slug = "shoreditch-house",
                house = HasOne(
                    "houses",
                    "SD"
                ),
                _restaurant = HasOne(
                ),
                hotel = HasOne(
                    "hotels",
                    "SD"
                ),
                _parent = HasOne(),
                activeParentVenue = HasOne(),
                scaRequired = false,
                maxGuests = 3

            )
            return venue.apply {
                id = "SD"
                type = "venues"
                document = ArrayDocument<ResourceIdentifier>().apply {
                    add(venue)
                    addInclude(testHouse)
                    addInclude(testHotel)
                }
            }
        }

    private val testHotel: Hotel
        get() {
            return Hotel(
                roomBookingUrl = "https://be.synxis.com/?hotel=27773&chain=9014&promo=SHMEM",
                _totalsInclusiveOfTax = true,
                friendsAndFamily = true,
                venue = HasOne(
                    "venues",
                    "SD"
                )
            ).apply {
                id = "SD"
            }
        }

    private val testHouse: House
        get() {
            return House().apply {
                id = "SD"
                houseImageSet = ImageSet().apply {
                    mediumPng =
                        "https://res.cloudinary.com/soho-house/image/upload/t_houses_house_image_medium/v1/sitecore-uat/551420101AB24FEE8EACC2044EB9FA32.jpg"
                    largePng =
                        "https://res.cloudinary.com/soho-house/image/upload/t_houses_house_image_large/v1/sitecore-uat/551420101AB24FEE8EACC2044EB9FA32.jpg"
                    xlargePng =
                        "https://res.cloudinary.com/soho-house/image/upload/t_houses_house_image_xlarge/v2/sitecore-uat/551420101AB24FEE8EACC2044EB9FA32.jpg"
                }
                eatAndDrinkImageSet = EADImageSet().apply {
                    fillCrop =
                        "https://res.cloudinary.com/soho-house/image/upload/t_1:1_fill-crop/v1/sitecore-uat/551420101AB24FEE8EACC2044EB9FA32.jpg"
                    altText = "Drone shot of pool with city skyline in the background"
                }
            }

        }
}