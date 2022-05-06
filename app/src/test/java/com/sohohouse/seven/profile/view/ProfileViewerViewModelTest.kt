package com.sohohouse.seven.profile.view

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.contains
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.connect.mynetwork.ConnectionRepository
import com.sohohouse.seven.connect.trafficlights.repo.TrafficLightsRepo
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.chat.ChatChannelsRepo
import com.sohohouse.seven.network.chat.ChatConnectionRepo
import com.sohohouse.seven.network.core.models.BlockedMemberList
import com.sohohouse.seven.network.core.models.MutualConnectionRequests
import com.sohohouse.seven.network.core.models.MutualConnections
import com.sohohouse.seven.network.core.models.Profile
import com.sohohouse.seven.profile.ProfileRepository
import com.sohohouse.seven.profile.view.model.*
import com.sohohouse.seven.profile.view.renderer.ProfileHeaderRenderer
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import moe.banana.jsonapi2.HasOne
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ProfileViewerViewModelTest {

    companion object {
        private const val MY_PROFILE_ID = "myProfID"
        private const val OTHERS_PROFILE_ID = "otherProfID"
    }

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var userManager: UserManager

    @MockK
    private lateinit var profileRepo: ProfileRepository

    @MockK
    private lateinit var connectionRepo: ConnectionRepository

    @MockK(relaxed = true)
    private lateinit var chatChannelsRepo: ChatChannelsRepo

    @MockK(relaxed = true)
    private lateinit var chatConnectionRepo: ChatConnectionRepo

    @MockK
    lateinit var analyticsManager: AnalyticsManager

    @MockK
    lateinit var trafficLightsRepo: TrafficLightsRepo

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)

        every { userManager.profileID } returns MY_PROFILE_ID
        every { connectionRepo.blockedMembers } returns MutableStateFlow(emptyList())
        coEvery { connectionRepo.patchConnectionRequest(any()) } returns Either.Value(
            MutualConnectionRequests()
        )
    }

    @After
    fun tearDown() {
    }


    @Test
    fun `Given a profile with social opted in, Then ViewModel emits items with social account`() {
        val profile = createProfile(OTHERS_PROFILE_ID, true)
        every { profileRepo.getProfile(any()) } returns Either.Value(profile)

        val viewModel = createViewModel(profile)

        val observer = mockk<Observer<List<DiffItem>>>(relaxed = true)
        viewModel.items.observeForever(observer)

        CapturingSlot<List<DiffItem>>().let { slot ->
            verify { observer.onChanged(capture(slot)) }
            assert(slot.captured.contains { it is ProfileHeaderRenderer.ProfileHeaderItem })
            assert(slot.captured.contains { it is SocialAccounts })
        }
        viewModel.items.removeObserver(observer)
    }

    @Test
    fun `Given a profile with social opted out, Then ViewModel emits items without social account`() {
        val profile = createProfile(OTHERS_PROFILE_ID, false)
        every { profileRepo.getProfile(any()) } returns Either.Value(profile)

        val viewModel = createViewModel(profile)

        val observer = mockk<Observer<List<DiffItem>>>(relaxed = true)
        viewModel.items.observeForever(observer)

        CapturingSlot<List<DiffItem>>().let { slot ->
            verify { observer.onChanged(capture(slot)) }
            assert(slot.captured.contains { it is ProfileHeaderRenderer.ProfileHeaderItem })
            assert(!slot.captured.contains { it is SocialAccounts })
        }
        viewModel.items.removeObserver(observer)
    }

    @Test
    fun `Given my profile, Then ViewModel emits items with buttons edit and share profile`() {
        val profile = createProfile(MY_PROFILE_ID)
        every { profileRepo.getProfile(any()) } returns Either.Value(profile)

        val viewModel = createViewModel(profile)

        val observer = mockk<Observer<List<DiffItem>>>(relaxed = true)
        viewModel.items.observeForever(observer)

        CapturingSlot<List<DiffItem>>().let { slot ->
            verify { observer.onChanged(capture(slot)) }

            val buttons = slot.captured.filterIsInstance<Buttons>().firstOrNull()?.list
            assert(buttons?.size == 2)
            assert(buttons?.contains { it is EditButton } == true)
        }
        viewModel.items.removeObserver(observer)
    }

    @Test
    fun `Given others profile with connected, Then ViewModel emits items with menu buttons`() {
        val connections = MutualConnections(
            sender = HasOne(Profile().apply { this.id = MY_PROFILE_ID }),
            receiver = HasOne(Profile().apply { this.id = OTHERS_PROFILE_ID })
        )
        val profile =
            createProfile(id = OTHERS_PROFILE_ID, socialsOptIn = true, connections = connections)
        every { profileRepo.getProfile(any()) } returns Either.Value(profile)

        val viewModel = createViewModel(profile)

        val observer = mockk<Observer<List<DiffItem>>>(relaxed = true)
        viewModel.items.observeForever(observer)

        CapturingSlot<List<DiffItem>>().let { slot ->
            verify { observer.onChanged(capture(slot)) }

            val buttons = slot.captured.filterIsInstance<Buttons>().firstOrNull()?.list
            println(buttons)
            assert(buttons?.size == 2) {
                "Buttons size are not equal to 2. ${buttons?.map { it.buttonTitle }}"
            }
            assert(buttons?.contains { it is OptionsMenu } == true)
            assert(buttons?.contains { it is MessageButton } == true)
        }
        viewModel.items.removeObserver(observer)
    }

    @Test
    fun `Given others profile with blocked, Then ViewModel emits items with menu buttons`() {
        val profile = createProfile(OTHERS_PROFILE_ID, true)
        every { profileRepo.getProfile(any()) } returns Either.Value(profile)

        val viewModel = createViewModel(
            profile,
            blockedList = BlockedMemberList(blockedMembers = listOf(OTHERS_PROFILE_ID))
        )

        val observer = mockk<Observer<List<DiffItem>>>(relaxed = true)
        viewModel.items.observeForever(observer)

        CapturingSlot<List<DiffItem>>().let { slot ->
            verify { observer.onChanged(capture(slot)) }

            val buttons = slot.captured.filterIsInstance<Buttons>().firstOrNull()?.list
            assert(buttons?.size == 1)
            assert(buttons?.contains { it is UnblockButton } == true)
        }
        viewModel.items.removeObserver(observer)
    }

    @Test
    fun `Given others profile with not connected, Then ViewModel emits items with connect and menu buttons`() {
        val profile = createProfile(OTHERS_PROFILE_ID, true)
        every { profileRepo.getProfile(any()) } returns Either.Value(profile)

        val viewModel = createViewModel(profile)

        val observer = mockk<Observer<List<DiffItem>>>(relaxed = true)
        viewModel.items.observeForever(observer)

        CapturingSlot<List<DiffItem>>().let { slot ->
            verify { observer.onChanged(capture(slot)) }

            val buttons = slot.captured.filterIsInstance<Buttons>().firstOrNull()?.list
            assert(buttons?.size == 3)
            assert(buttons?.contains { it is ConnectButton } == true)
            assert(buttons?.contains { it is OptionsMenu } == true)
            assert(buttons?.contains { it is MessageButton } == true)
        }
        viewModel.items.removeObserver(observer)
    }

    @Test
    fun `Given others profile with connect request received, When accept  it, Then ViewModel emits items with menu buttons only`() {
        val request = MutualConnectionRequests(
            sender = HasOne(Profile().apply { this.id = OTHERS_PROFILE_ID }),
            receiver = HasOne(Profile().apply { this.id = MY_PROFILE_ID })
        ).also { it.id = "connection_id" }
        val profile = createProfile(id = OTHERS_PROFILE_ID, socialsOptIn = true, requests = request)

        val connections = MutualConnections(
            sender = HasOne(Profile().apply { this.id = OTHERS_PROFILE_ID }),
            receiver = HasOne(Profile().apply { this.id = MY_PROFILE_ID })
        )
        val connectedProfile =
            createProfile(id = OTHERS_PROFILE_ID, socialsOptIn = true, connections = connections)

        every { profileRepo.getProfile(any()) } returns Either.Value(profile) andThen Either.Value(
            connectedProfile
        )

        val viewModel = createViewModel(profile)

        val observer = mockk<Observer<List<DiffItem>>>(relaxed = true)
        viewModel.items.observeForever(observer)

        viewModel.acceptRequest()

        ArrayList<List<DiffItem>>().let { slot ->
            verify { observer.onChanged(capture(slot)) }

            val buttons = slot.last().filterIsInstance<Buttons>().firstOrNull()?.list
            println(buttons)
            assertEquals(2, buttons?.size)
            assert(buttons?.contains { it is OptionsMenu } == true)
            assert(buttons?.contains { it is MessageButton } == true)
        }
    }

    @Test
    fun `Given others profile with connect request sent, Then ViewModel emits items with connect and menu buttons`() {
        val request = MutualConnectionRequests(
            sender = HasOne(Profile().apply { this.id = MY_PROFILE_ID }),
            receiver = HasOne(Profile().apply { this.id = OTHERS_PROFILE_ID })
        )
        val profile = createProfile(id = OTHERS_PROFILE_ID, socialsOptIn = true, requests = request)
        every { profileRepo.getProfile(any()) } returns Either.Value(profile)

        val viewModel = createViewModel(profile)

        val observer = mockk<Observer<List<DiffItem>>>(relaxed = true)
        viewModel.items.observeForever(observer)

        CapturingSlot<List<DiffItem>>().let { slot ->
            verify { observer.onChanged(capture(slot)) }

            val buttons = slot.captured.filterIsInstance<Buttons>().firstOrNull()?.list
            assert(buttons?.size == 3)
            assert(buttons?.contains { it is RequestSentButton } == true)
            assert(buttons?.contains { it is OptionsMenu } == true)
            assert(buttons?.contains { it is MessageButton } == true)
        }
        viewModel.items.removeObserver(observer)
    }

    @Test
    fun `Given others profile with connect request received, Then ViewModel emits items with accept, ignore and menu buttons`() {
        val request = MutualConnectionRequests(
            sender = HasOne(Profile().apply { this.id = OTHERS_PROFILE_ID }),
            receiver = HasOne(Profile().apply { this.id = MY_PROFILE_ID })
        )
        val profile = createProfile(id = OTHERS_PROFILE_ID, socialsOptIn = true, requests = request)
        every { profileRepo.getProfile(any()) } returns Either.Value(profile)

        val viewModel = createViewModel(profile)

        val observer = mockk<Observer<List<DiffItem>>>(relaxed = true)
        viewModel.items.observeForever(observer)

        CapturingSlot<List<DiffItem>>().let { slot ->
            verify { observer.onChanged(capture(slot)) }

            val buttons = slot.captured.filterIsInstance<Buttons>().firstOrNull()?.list
            assert(buttons?.size == 3)
            assert(buttons?.contains { it is AcceptButton } == true)
            assert(buttons?.contains { it is IgnoreButton } == true)
            assert(buttons?.contains { it is OptionsMenu } == true)
        }
        viewModel.items.removeObserver(observer)
    }

    private fun createViewModel(
        profile: Profile,
        message: String? = null,
        blockedList: BlockedMemberList = BlockedMemberList()
    ): ProfileViewerViewModel {
        coEvery { connectionRepo.getBlockedMembers() } returns Either.Value(blockedList)
        every { connectionRepo.blockedMembers } returns MutableStateFlow(
            (blockedList.blockedMembers ?: emptyList())
        )

        return ProfileViewerViewModel(
            _profile = ProfileItem(profile),
            message = message,
            userManager = userManager,
            profileRepo = profileRepo,
            connectionRepo = connectionRepo,
            analyticsManager = analyticsManager,
            dispatcher = Dispatchers.Unconfined,
            trafficLightsRepo = trafficLightsRepo,
            chatChannelsRepo = chatChannelsRepo,
            chatConnectionRepo = chatConnectionRepo
        )
    }

    private fun createProfile(
        id: String,
        socialsOptIn: Boolean = false,
        requests: MutualConnectionRequests? = null,
        connections: MutualConnections? = null
    ): Profile {
        return mockk<Profile>().also { profile ->
            every { profile.id } returns id

            every { profile.firstName } returns "Team"
            every { profile.lastName } returns "Android"
            every { profile.occupation } returns "Developer"
            every { profile.city } returns "London"
            every { profile.imageUrl } returns "url"
            every { profile.isStaff } returns false

            every { profile.socialsOptIn } returns socialsOptIn
            every { profile.twitterHandle } returns "handle"
            every { profile.twitterUrl } returns "url"
            every { profile.youtubeHandle } returns "handle"
            every { profile.youtubeUrl } returns "url"
            every { profile.instagramHandle } returns "handle"
            every { profile.instagramUrl } returns "url"
            every { profile.linkedInHandle } returns "handle"
            every { profile.linkedInUrl } returns "url"
            every { profile.spotifyHandle } returns "handle"
            every { profile.spotifyUrl } returns "url"
            every { profile.website } returns "url"
            every { profile.pronouns } returns emptyList()

            every { profile.mutualConnectionRequest } returns listOfNotNull(requests)
            every { profile.mutualConnections } returns listOfNotNull(connections)
            every { profile.connections } returns listOfNotNull(requests, connections)
        }
    }

}
