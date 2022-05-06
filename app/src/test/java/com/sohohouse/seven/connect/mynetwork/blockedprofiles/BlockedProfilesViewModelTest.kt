package com.sohohouse.seven.connect.mynetwork.blockedprofiles

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.sohohouse.seven.common.utils.TestCoroutineRule
import com.sohohouse.seven.connect.mynetwork.ConnectionRepository
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.empty
import com.sohohouse.seven.network.base.model.error
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.models.BlockedMemberList
import com.sohohouse.seven.network.core.models.Profile
import com.sohohouse.seven.profile.ProfileRepository
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.Timeout
import java.util.*

const val DummyExceptionStr = "Dummy exception for testing"
const val FAKE_PROFILE_ID_1 = "Fake_Profile_ID_1"
const val FAKE_PROFILE_ID_2 = "Fake_Profile_ID_2"

val fakeProfile1 = Profile(_firstName = "Obi-Wan", _lastName = "Kenobi", occupation = "Jedi")
val fakeProfile2 = Profile(_firstName = "Grievous", occupation = "General")

@SmallTest
@ExperimentalCoroutinesApi
class BlockedProfilesViewModelTest {

    private lateinit var fakeBlockedProfile1: BlockedProfile
    private lateinit var fakeBlockedProfile2: BlockedProfile

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val globalTimeout: Timeout = Timeout.seconds(20)

    @MockK
    lateinit var connectionRepo: ConnectionRepository

    @MockK
    lateinit var profileRepo: ProfileRepository

    lateinit var viewModel: BlockedProfilesViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = BlockedProfilesViewModel(
            connectionRepo,
            profileRepo,
            analyticsManager = mockk(relaxed = true),//we do not need analyticsManager, so we can mock locally.
            testCoroutineRule.testCoroutineDispatcher
        )

        generateFakeBlockedProfiles()
    }

    private fun generateFakeBlockedProfiles() {
        fakeBlockedProfile1 =
            generateFakeBlockedProfileFrom(
                fakeProfile1.apply { id = UUID.randomUUID().toString() },
                FAKE_PROFILE_ID_1
            )
        fakeBlockedProfile2 =
            generateFakeBlockedProfileFrom(
                fakeProfile2.apply { id = UUID.randomUUID().toString() },
                FAKE_PROFILE_ID_2
            )
    }

    private fun generateFakeBlockedProfileFrom(
        fakeProfile: Profile,
        fakeProfileId: String
    ): BlockedProfile {
        fakeProfile.let {
            return BlockedProfile(
                id = it.id,
                profileId = fakeProfileId,
                firstName = it.firstName,
                lastName = it.lastName,
                fullName = it.firstName + " " + it.lastName,
                occupation = it.occupation ?: "",
                imageUrl = it.imageUrl
            )
        }
    }

    @Test
    fun `get blocked members ids`() {
        every { connectionRepo.blockedMembers } returns MutableStateFlow(emptyList())
        coEvery {
            connectionRepo.getBlockedMembers()
        } returns value(
            BlockedMemberList(
                blockedMembers = listOf(
                    FAKE_PROFILE_ID_1,
                    FAKE_PROFILE_ID_2
                )
            )
        )

        runBlockingTest {
            val result = connectionRepo.getBlockedMembers()
            assertThat(result).isInstanceOf(Either.Value::class.java)
            result.ifValue {
                assertThat(it.blockedMembers).containsExactly(FAKE_PROFILE_ID_1, FAKE_PROFILE_ID_2)
            }
        }
    }

    @Test
    fun `get blocked members ids with invalid response`() {
        every { connectionRepo.blockedMembers } returns MutableStateFlow(emptyList())
        coEvery {
            connectionRepo.getBlockedMembers()
        } returns error(ServerError.INVALID_RESPONSE)

        runBlockingTest {
            val result = connectionRepo.getBlockedMembers()
            assertThat(result).isInstanceOf(Either.Error::class.java)
            result.ifError {
                assertThat(it).isEqualTo(ServerError.INVALID_RESPONSE)
            }
        }
    }

    @Test
    fun `get blocked members ids with empty response`() {
        every { connectionRepo.blockedMembers } returns MutableStateFlow(emptyList())
        coEvery {
            connectionRepo.getBlockedMembers()
        } returns empty()

        runBlockingTest {
            val result = connectionRepo.getBlockedMembers()
            assertThat(result).isInstanceOf(Either.Empty::class.java)
        }
    }

    @Test
    fun `get single profile`() {
        every { profileRepo.getProfile(FAKE_PROFILE_ID_1) } returns value(fakeProfile1)

        val result = profileRepo.getProfile(FAKE_PROFILE_ID_1)

        assertThat(result).isInstanceOf(Either.Value::class.java)
        result.ifValue {
            assertThat(it).isEqualTo(fakeProfile1)
        }
    }

    @Test
    fun `get blocked profiles`() {
        every { connectionRepo.blockedMembers } returns MutableStateFlow(
            listOf(
                FAKE_PROFILE_ID_1,
                FAKE_PROFILE_ID_2
            )
        )
        every { profileRepo.getProfile(FAKE_PROFILE_ID_1) } returns value(fakeProfile1)
        every { profileRepo.getProfile(FAKE_PROFILE_ID_2) } returns value(fakeProfile2)

        val data = viewModel.blockedContacts.getOrAwaitValue()
        assertThat(data).containsExactly(fakeBlockedProfile1, fakeBlockedProfile2)
    }

    @Test
    fun `get blocked profiles with broken ids`() = runBlockingTest {
        every { connectionRepo.blockedMembers }.throws(IllegalArgumentException(DummyExceptionStr))

        testCoroutineRule.testCoroutineScope.launch {
            viewModel.errorState.collect {
                assertThat(it).isIn(1..Int.MAX_VALUE)
                return@collect
            }
        }

        val data = viewModel.blockedContacts.getOrAwaitValue()
        assertThat(data).isEmpty()
    }

    @Test
    fun `get blocked profiles with one broken profile`() = runBlockingTest {
        every { connectionRepo.blockedMembers } returns MutableStateFlow(
            listOf(
                FAKE_PROFILE_ID_1,
                FAKE_PROFILE_ID_2
            )
        )
        every { profileRepo.getProfile(FAKE_PROFILE_ID_1) } returns value(fakeProfile1)
        every { profileRepo.getProfile(FAKE_PROFILE_ID_2) }.throws(
            IllegalArgumentException(
                DummyExceptionStr
            )
        )

        val data = viewModel.blockedContacts.getOrAwaitValue()
        assertThat(data).hasSize(1)
        assertThat(data).containsExactly(fakeBlockedProfile1)
    }

    @Test
    fun `unblock profile which throws exception`() = runBlockingTest {
        coEvery { connectionRepo.patchUnblockMember(FAKE_PROFILE_ID_1) }.throws(
            IllegalArgumentException(DummyExceptionStr)
        )

        testCoroutineRule.testCoroutineScope.launch {
            viewModel.errorState.collect {
                assertThat(it).isIn(1..Int.MAX_VALUE)
                return@collect
            }
        }
        viewModel.unblockContact(fakeBlockedProfile1)
    }

    @Test
    fun `unblock profile`() = runBlockingTest {
        val mockedViewModel = spyk(viewModel, recordPrivateCalls = true)

        coEvery { connectionRepo.patchUnblockMember(FAKE_PROFILE_ID_1) } returns empty()

        mockedViewModel.unblockContact(fakeBlockedProfile1)

        verify(exactly = 1) { mockedViewModel["refresh"]() }
    }

}
