package com.sohohouse.seven.guests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.secondValue
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.sohohouse.seven.BuildConfigManager
import com.sohohouse.seven.FeatureFlags
import com.sohohouse.seven.R
import com.sohohouse.seven.app.TestApp
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.captor
import com.sohohouse.seven.common.extensions.cast
import com.sohohouse.seven.common.extensions.getDayAndMonthFormattedDate
import com.sohohouse.seven.common.house.HouseManager
import com.sohohouse.seven.common.mock
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.guests.GuestsTestHelper.mockGuestList
import com.sohohouse.seven.guests.GuestsTestHelper.mockInvite
import com.sohohouse.seven.network.base.model.empty
import com.sohohouse.seven.network.base.model.value
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(application = TestApp::class)
class GuestListDetailsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var analyticsManager: AnalyticsManager

    @Mock
    lateinit var guestListRepository: GuestListRepository

    @Mock
    lateinit var houseManager: HouseManager

    @Mock
    lateinit var buildConfigManager: BuildConfigManager

    @Mock
    lateinit var userManager: UserManager

    @MockK
    lateinit var featureFlags: FeatureFlags
    lateinit var guestListHelper: GuestListHelper

    private val testGuestListId = "testId"

    private val stringProvider = object : StringProvider {
        override fun getString(resId: Int, vararg params: String): String {
            return "Share message ${params[0]}"
        }

        override fun getString(resId: Int?): String {
            return ""
        }

        override fun getStringArray(resId: Int): Array<String> {
            return emptyArray()
        }
    }

    val viewModel by lazy {
        GuestListDetailsViewModel(
            analyticsManager,
            guestListRepository,
            buildConfigManager,
            stringProvider,
            userManager,
            guestListHelper,
            Dispatchers.Unconfined
        )
    }

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        MockKAnnotations.init(this)
        mockkStatic("com.sohohouse.seven.common.extensions.DateKt")
        every { any<Date>().getDayAndMonthFormattedDate() } returns "1 January"
        every { featureFlags.guestRegistration } returns true
        guestListHelper = GuestListHelper(featureFlags, stringProvider, buildConfigManager)
    }

    @Test
    fun `on init in new guestlist mode, viewmodel fetches and emits guestlist items`() =
        runBlockingTest {
            `when`(guestListRepository.getGuestList(testGuestListId)).thenReturn(value(mockGuestList()))

            val observer = mock<Observer<List<GuestListDetailsAdapterItem>>>().also {
                viewModel.items.observeForever(it)
            }

            viewModel.init(testGuestListId, GuestListDetailsMode.MODE_NEW_GUEST_LIST)

            val captor = captor<List<GuestListDetailsAdapterItem>>()
            verify(observer, times(1)).onChanged(captor.capture())

            val items = captor.value

            assertEquals(4, items.size)
            assertEquals(GuestListDetailsAdapterItem.FormHeaderItem::class, items[0]::class)
            assertEquals(GuestListDetailsAdapterItem.GuestListFormItem::class, items[1]::class)
            assertEquals(GuestListDetailsAdapterItem.GuestsHeaderItem::class, items[2]::class)
            assertEquals(
                R.string.header_your_guests,
                (items[2] as GuestListDetailsAdapterItem.GuestsHeaderItem).text
            )

            assertEquals(GuestListDetailsAdapterItem.GuestsSubheaderItem::class, items[3]::class)
            assertEquals(
                R.string.title_guest_new_invite_message,
                (items[3] as GuestListDetailsAdapterItem.GuestsSubheaderItem).text
            )
        }

    @Test
    fun `on init in existing guestlist mode, viewmodel fetches and emits guestlist items`() =
        runBlockingTest {
            `when`(guestListRepository.getGuestList(testGuestListId)).thenReturn(
                value(
                    mockGuestList(
                        invites = listOf(mockInvite(guestListId = testGuestListId))
                    )
                )
            )

            val observer = mock<Observer<List<GuestListDetailsAdapterItem>>>().also {
                viewModel.items.observeForever(it)
            }

            viewModel.init(testGuestListId, GuestListDetailsMode.MODE_EXISTING_GUEST_LIST)

            val captor = captor<List<GuestListDetailsAdapterItem>>()
            verify(observer, times(1)).onChanged(captor.capture())

            val items = captor.value

            assertEquals(5, items.size)
            assertEquals(GuestListDetailsAdapterItem.FormHeaderItem::class, items[0]::class)
            assertEquals(GuestListDetailsAdapterItem.GuestListFormItem::class, items[1]::class)
            assertEquals(GuestListDetailsAdapterItem.GuestsHeaderItem::class, items[2]::class)
            assertEquals(
                R.string.header_your_guests,
                (items[2] as GuestListDetailsAdapterItem.GuestsHeaderItem).text
            )

            assertEquals(GuestListDetailsAdapterItem.GuestsSubheaderItem::class, items[3]::class)
            assertEquals(
                R.string.title_guest_new_invite_message,
                (items[3] as GuestListDetailsAdapterItem.GuestsSubheaderItem).text
            )

            assertEquals(GuestListDetailsAdapterItem.GuestItem::class, items[4]::class)
        }

    @Test
    fun `on init in existing guestlist mode with max 3 guests and 2 filled, viewmodel fetches and emits guestlist items`() =
        runBlockingTest {
            `when`(guestListRepository.getGuestList(testGuestListId)).thenReturn(
                value(
                    mockGuestList(
                        maxGuests = 3,
                        invites = listOf(
                            mockInvite(id = "1", guestListId = testGuestListId),
                            mockInvite(id = "2", guestListId = testGuestListId)
                        )
                    )
                )
            )

            val observer = mock<Observer<List<GuestListDetailsAdapterItem>>>().also {
                viewModel.items.observeForever(it)
            }

            viewModel.init(testGuestListId, GuestListDetailsMode.MODE_EXISTING_GUEST_LIST)

            val captor = captor<List<GuestListDetailsAdapterItem>>()
            verify(observer, times(1)).onChanged(captor.capture())

            val items = captor.value

            assertEquals(6, items.size)
            assertEquals(GuestListDetailsAdapterItem.FormHeaderItem::class, items[0]::class)
            assertEquals(GuestListDetailsAdapterItem.GuestListFormItem::class, items[1]::class)
            assertEquals(GuestListDetailsAdapterItem.GuestsHeaderItem::class, items[2]::class)
            assertEquals(
                R.string.header_your_guests,
                (items[2] as GuestListDetailsAdapterItem.GuestsHeaderItem).text
            )

            assertEquals(GuestListDetailsAdapterItem.GuestsSubheaderItem::class, items[3]::class)
            assertEquals(
                R.string.title_guest_new_invite_message,
                (items[3] as GuestListDetailsAdapterItem.GuestsSubheaderItem).text
            )

            assertEquals(GuestListDetailsAdapterItem.GuestItem::class, items[4]::class)
            assertEquals(GuestListDetailsAdapterItem.GuestItem::class, items[5]::class)
        }

    @Test
    fun `on init in existing guestlist mode with max 3 guests and 3 filled, viewmodel fetches and emits guestlist items`() =
        runBlockingTest {
            `when`(guestListRepository.getGuestList(testGuestListId)).thenReturn(
                value(
                    mockGuestList(
                        maxGuests = 3,
                        invites = listOf(
                            mockInvite(id = "1", guestListId = testGuestListId),
                            mockInvite(id = "2", guestListId = testGuestListId),
                            mockInvite(id = "3", guestListId = testGuestListId)
                        )
                    )
                )
            )

            val observer = mock<Observer<List<GuestListDetailsAdapterItem>>>().also {
                viewModel.items.observeForever(it)
            }

            viewModel.init(testGuestListId, GuestListDetailsMode.MODE_EXISTING_GUEST_LIST)

            val captor = captor<List<GuestListDetailsAdapterItem>>()
            verify(observer, times(1)).onChanged(captor.capture())

            val items = captor.value

            assertEquals(7, items.size)
            assertEquals(GuestListDetailsAdapterItem.FormHeaderItem::class, items[0]::class)
            assertEquals(GuestListDetailsAdapterItem.GuestListFormItem::class, items[1]::class)
            assertEquals(GuestListDetailsAdapterItem.GuestsHeaderItem::class, items[2]::class)
            assertEquals(
                R.string.header_your_guests,
                (items[2] as GuestListDetailsAdapterItem.GuestsHeaderItem).text
            )

            assertEquals(GuestListDetailsAdapterItem.GuestsSubheaderItem::class, items[3]::class)
            assertEquals(
                R.string.title_guest_new_invite_message,
                (items[3] as GuestListDetailsAdapterItem.GuestsSubheaderItem).text
            )

            assertEquals(GuestListDetailsAdapterItem.GuestItem::class, items[4]::class)
            assertEquals(GuestListDetailsAdapterItem.GuestItem::class, items[5]::class)
            assertEquals(GuestListDetailsAdapterItem.GuestItem::class, items[6]::class)

        }

    @Test
    fun `when user adds guest, viewmodel emits correct items`() = runBlockingTest {
        val guestName = "Jen"
        `when`(guestListRepository.getGuestList(testGuestListId)).thenReturn(value(mockGuestList()))
        `when`(
            guestListRepository.addGuestToGuestList(
                testGuestListId,
                guestName
            )
        ).thenReturn(value(mockInvite(name = guestName, guestListId = testGuestListId)))

        val observer = mock<Observer<List<GuestListDetailsAdapterItem>>>().also {
            viewModel.items.observeForever(it)
        }

        viewModel.init(testGuestListId, GuestListDetailsMode.MODE_EXISTING_GUEST_LIST)

        viewModel.onNewGuestNameConfirmed(guestName)

        val captor = captor<List<GuestListDetailsAdapterItem>>()
        verify(observer, times(2)).onChanged(captor.capture())

        val items = captor.secondValue

        assertEquals(5, items.size)
        assertEquals(GuestListDetailsAdapterItem.FormHeaderItem::class, items[0]::class)
        assertEquals(GuestListDetailsAdapterItem.GuestListFormItem::class, items[1]::class)
        assertEquals(GuestListDetailsAdapterItem.GuestsHeaderItem::class, items[2]::class)
        assertEquals(
            R.string.header_your_guests,
            (items[2] as GuestListDetailsAdapterItem.GuestsHeaderItem).text
        )

        assertEquals(GuestListDetailsAdapterItem.GuestsSubheaderItem::class, items[3]::class)
        assertEquals(
            R.string.title_guest_new_invite_message,
            (items[3] as GuestListDetailsAdapterItem.GuestsSubheaderItem).text
        )

        assertEquals(GuestListDetailsAdapterItem.GuestItem::class, items[4]::class)

        assertEquals(guestName, items[4].cast<GuestListDetailsAdapterItem.GuestItem>().guestName)
    }

    @Test
    fun `when user edits guest name guest, viewmodel emits correct items`() = runBlockingTest {
        val originalGuestName = "Jen"
        val editedGuestName = "Sarah"
        val inviteId = "1"
        `when`(guestListRepository.getGuestList(testGuestListId)).thenReturn(
            value(
                mockGuestList(
                    id = testGuestListId,
                    invites = mutableListOf(
                        mockInvite(
                            id = inviteId,
                            guestListId = testGuestListId,
                            name = originalGuestName
                        )
                    )
                )
            )
        )
        `when`(guestListRepository.editGuestName(inviteId, editedGuestName)).thenReturn(
            value(
                mockInvite(id = inviteId, guestListId = testGuestListId, name = editedGuestName)
            )
        )

        val observer = mock<Observer<List<GuestListDetailsAdapterItem>>>().also {
            viewModel.items.observeForever(it)
        }

        viewModel.init(testGuestListId, GuestListDetailsMode.MODE_EXISTING_GUEST_LIST)

        viewModel.onExistingGuestNameChanged(inviteId, editedGuestName)

        val captor = captor<List<GuestListDetailsAdapterItem>>()
        verify(observer, times(2)).onChanged(captor.capture())

        val items = captor.secondValue

        assertEquals(5, items.size)
        assertEquals(GuestListDetailsAdapterItem.FormHeaderItem::class, items[0]::class)
        assertEquals(GuestListDetailsAdapterItem.GuestListFormItem::class, items[1]::class)
        assertEquals(GuestListDetailsAdapterItem.GuestsHeaderItem::class, items[2]::class)
        assertEquals(
            R.string.header_your_guests,
            (items[2] as GuestListDetailsAdapterItem.GuestsHeaderItem).text
        )

        assertEquals(GuestListDetailsAdapterItem.GuestsSubheaderItem::class, items[3]::class)
        assertEquals(
            R.string.title_guest_new_invite_message,
            (items[3] as GuestListDetailsAdapterItem.GuestsSubheaderItem).text
        )

        assertEquals(GuestListDetailsAdapterItem.GuestItem::class, items[4]::class)

        assertEquals(
            editedGuestName,
            items[4].cast<GuestListDetailsAdapterItem.GuestItem>().guestName
        )
    }

    @Test
    fun `on delete guest list action, viewmodel calls repo to delete guest list and on success emits navigation exit event`() =
        runBlockingTest {
            `when`(guestListRepository.getGuestList(testGuestListId)).thenReturn(
                value(
                    mockGuestList(
                        id = testGuestListId
                    )
                )
            )
            `when`(guestListRepository.deleteGuestList(testGuestListId)).thenReturn(empty())

            viewModel.init(testGuestListId, GuestListDetailsMode.MODE_EXISTING_GUEST_LIST)

            val deleteObserver = mock<Observer<Any>>().also {
                viewModel.navigationExitEvent.observeForever(it)
            }

            viewModel.deleteGuestList()

            verify(guestListRepository).deleteGuestList(testGuestListId)
            verify(deleteObserver, times(1)).onChanged(ArgumentMatchers.any())
        }


}