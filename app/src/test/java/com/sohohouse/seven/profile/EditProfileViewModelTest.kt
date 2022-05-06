package com.sohohouse.seven.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.mock
import com.sohohouse.seven.common.utils.EmptyStringProvider
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.models.Account
import com.sohohouse.seven.network.core.models.Occupation
import com.sohohouse.seven.profile.edit.EditProfileAdapterItem
import com.sohohouse.seven.profile.edit.EditProfileViewModel
import io.reactivex.Single
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class EditProfileViewModelTest {

    @Mock
    private lateinit var profileRepo: ProfileRepository

    @Mock
    private lateinit var industriesRepo: IndustriesRepository

    @Mock
    private lateinit var profileQuestionsRepo: ProfileQuestionsRepository

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var analyticsManager: AnalyticsManager

    private lateinit var viewModel: EditProfileViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    private fun setUpSuccessPath() {
        val profile = ProfileTestHelper.fullMockProfile()

        val account = mock<Account>()
        Mockito.`when`(account.profile).thenReturn(profile)
        Mockito.`when`(profileRepo.getMyAccountWithProfile())
            .thenReturn(Single.just(Either.Value(account)))
        Mockito.`when`(industriesRepo.getIndustries())
            .thenReturn(ProfileTestHelper.getMockIndustryOptions())

        viewModel = EditProfileViewModel(
            profileRepo,
            industriesRepo,
            profileQuestionsRepo,
            EmptyStringProvider(),
            analyticsManager
        )
        viewModel.fetchData()
    }

    @Test
    fun `on init viewmodel it retrieves my profile and if successful emits expected values`() {
        setUpSuccessPath()

        verify(profileRepo).getMyAccountWithProfile()
        verifyNoMoreInteractions(profileRepo)

        val dataObserver = mock<Observer<List<EditProfileAdapterItem>>>()
        viewModel.profileAdapterItems.observeForever(dataObserver)
        verify(dataObserver).onChanged(Mockito.anyList<EditProfileAdapterItem>())   //ProfileListItemFactory is separately tested
        verifyNoMoreInteractions(dataObserver)

        val errorObserver = mock<Observer<Any>>()
        viewModel.errorViewState.observeForever(errorObserver)
        verify(errorObserver, never()).onChanged(any())
        verifyNoMoreInteractions(errorObserver)
    }

    @Test
    fun `on init viewmodel retrieves profile and if unsuccessful emits expected values`() {
        Mockito.`when`(profileRepo.getMyAccountWithProfile())
            .thenReturn(Single.just(Either.Error(ServerError.BAD_REQUEST)))

        viewModel = EditProfileViewModel(
            profileRepo,
            industriesRepo,
            profileQuestionsRepo,
            EmptyStringProvider(),
            analyticsManager
        )

        val dataObserver = mock<Observer<List<EditProfileAdapterItem>>>()
        viewModel.profileAdapterItems.observeForever(dataObserver)

        val errorObserver = mock<Observer<Any>>()
        viewModel.errorViewState.observeForever(errorObserver)

        viewModel.fetchData()
        verify(profileRepo).getMyAccountWithProfile()
        verifyNoMoreInteractions(profileRepo)
        verify(dataObserver, never()).onChanged(any())
        verify(errorObserver).onChanged(any())
        verifyNoMoreInteractions(errorObserver)
    }

    @Test
    fun `when dropdown field is tapped, state is toggled and dropdown selector is added`() {
        setUpSuccessPath()

        val initialItems = viewModel.profileAdapterItems.value!!
        val dropDownField =
            initialItems.first { it is EditProfileAdapterItem.Field.Dropdown<*> } as EditProfileAdapterItem.Field.Dropdown<*>

        val itemChangeObserver = mock<Observer<EditProfileViewModel.ItemChangeEvent>>()
        viewModel.itemChangeEvent.observeForever(itemChangeObserver)

        viewModel.onEditProfileFieldClick(dropDownField)

        val newItems = viewModel.profileAdapterItems.value!!
        val dropdownFieldIndex = newItems.indexOf(dropDownField)

        //verify dropdown field state toggled
        Assert.assertEquals(EditProfileAdapterItem.Field.Dropdown.State.OPEN, dropDownField.state)

        // Assert dropdown selector item added after dropdown field item
        Assert.assertTrue(newItems[dropdownFieldIndex + 1] is EditProfileAdapterItem.DropdownSelector)
        Assert.assertEquals(initialItems.size + 1, newItems.size)
    }

    @Test
    fun `when item selected in dropdown selector, relevant field updates`() {
        setUpSuccessPath()

        val initialItems = viewModel.profileAdapterItems.value!!

        val dropdownItem =
            initialItems.first { it is EditProfileAdapterItem.Field.Dropdown<*> } as EditProfileAdapterItem.Field.Dropdown<*>
        val dropdownField = dropdownItem.field

        val option = dropdownItem.multipleChoiceOptions.get(1)

        val itemChangeObserver = mock<Observer<EditProfileViewModel.ItemChangeEvent>>()
        viewModel.itemChangeEvent.observeForever(itemChangeObserver)

        viewModel.onOptionSelected(dropdownField, option)

        // Assert field value has changed to dropdown option selected
        Assert.assertEquals(option.value, dropdownField.getEditDisplayValue(EmptyStringProvider()))
        verify(itemChangeObserver).onChanged(
            EditProfileViewModel.ItemChangeEvent(
                initialItems.indexOf(
                    dropdownItem
                )
            )
        )
    }

    @Test
    fun `when ask me about is tapped, correct open editor event is emitted`() {
        setUpSuccessPath()

        val items = viewModel.profileAdapterItems.value!!
        val bioItem =
            items.first { it is EditProfileAdapterItem.Field<*> && it.field is ProfileField.AskMeAbout } as EditProfileAdapterItem.Field<*>

        val observer = mock<Observer<ProfileField<*>>>()
        viewModel.openEditorEvent.observeForever(observer)
        viewModel.onEditProfileFieldClick(bioItem)

        verify(observer).onChanged(bioItem.field)
    }

    @Test
    fun `when ask me about is edited, value updates in list and correct change event is emitted`() {
        setUpSuccessPath()

        val newValue = "Ask me about that time I did the thing"

        val observer = mock<Observer<EditProfileViewModel.ItemChangeEvent>>()

        viewModel.itemChangeEvent.observeForever(observer)

        viewModel.onAskMeAboutUpdated(newValue)

        val items = viewModel.profileAdapterItems.value!!
        val itemIndex =
            items.indexOfFirst { it is EditProfileAdapterItem.Field<*> && it.field is ProfileField.AskMeAbout }

        val field =
            ((items.get(itemIndex) as EditProfileAdapterItem.Field<*>).field as ProfileField.AskMeAbout)

        verify(observer).onChanged(EditProfileViewModel.ItemChangeEvent(itemIndex))
        Assert.assertEquals(newValue, field.data)
    }

    @Test
    fun `when connected accounts is tapped, correct open editor event is emitted`() {
        setUpSuccessPath()

        val items = viewModel.profileAdapterItems.value!!
        val connectedAccountsField = items.filterIsInstance<EditProfileAdapterItem.Field<*>>()
            .associateBy { it.field::class }
            .get(ProfileField.SocialMedia::class)
                as EditProfileAdapterItem.Field

        val observer = mock<Observer<ProfileField<*>>>()
        viewModel.openEditorEvent.observeForever(observer)
        viewModel.onEditProfileFieldClick(connectedAccountsField)

        verify(observer).onChanged(connectedAccountsField.field)
    }

    @Test
    fun `when connected accounts is edited, value updates in list and correct change event is emitted`() {
        setUpSuccessPath()

        val observer = mock<Observer<EditProfileViewModel.ItemChangeEvent>>()

        viewModel.itemChangeEvent.observeForever(observer)

        val mockItems =
            ProfileField.SocialMedia(ProfileTestHelper.createSocialMediaItems(), optIn = false)
        viewModel.onConnectedAccountsUpdated(mockItems)

        val items = viewModel.profileAdapterItems.value!!

        val itemIndex =
            items.indexOfFirst { it is EditProfileAdapterItem.Field<*> && it.field is ProfileField.SocialMedia }

        val resultItems =
            ((items.get(itemIndex) as EditProfileAdapterItem.Field<*>).field as ProfileField.SocialMedia).data

        verify(observer).onChanged(EditProfileViewModel.ItemChangeEvent(itemIndex))

        Assert.assertEquals(mockItems.data, resultItems)
    }

    @Test
    fun `when occupation is tapped, correct open editor event is emitted`() {
        setUpSuccessPath()

        val items = viewModel.profileAdapterItems.value!!
        val occupationField = items.filterIsInstance<EditProfileAdapterItem.Field<*>>()
            .associateBy { it.field::class }
            .get(ProfileField.Occupation::class)
                as EditProfileAdapterItem.Field

        val observer = mock<Observer<ProfileField<*>>>()
        viewModel.openEditorEvent.observeForever(observer)
        viewModel.onEditProfileFieldClick(occupationField)

        verify(observer).onChanged(occupationField.field)
    }

    @Test
    fun `when occupation is edited, value updates in list and correct change event is emitted`() {
        setUpSuccessPath()

        val observer = mock<Observer<EditProfileViewModel.ItemChangeEvent>>()

        viewModel.itemChangeEvent.observeForever(observer)

        val newOccupation = Occupation("iOS Dev :O")

        viewModel.onOccupationUpdated(newOccupation)

        val items = viewModel.profileAdapterItems.value!!
        val itemIndex =
            items.indexOfFirst { it is EditProfileAdapterItem.Field<*> && it.field is ProfileField.Occupation }

        val field =
            ((items.get(itemIndex) as EditProfileAdapterItem.Field<*>).field as ProfileField.Occupation)

        verify(observer).onChanged(EditProfileViewModel.ItemChangeEvent(itemIndex))

        Assert.assertEquals(newOccupation, Occupation(field.data))
    }

    @Test
    fun `when city is tapped, correct open editor event is emitted`() {
        setUpSuccessPath()

        val items = viewModel.profileAdapterItems.value!!
        val cityField = items.filterIsInstance<EditProfileAdapterItem.Field<*>>()
            .associateBy { it.field::class }
            .get(ProfileField.City::class)
                as EditProfileAdapterItem.Field

        val observer = mock<Observer<ProfileField<*>>>()
        viewModel.openEditorEvent.observeForever(observer)
        viewModel.onEditProfileFieldClick(cityField)

        verify(observer).onChanged(cityField.field)
    }

    @Test
    fun `when city is edited, value updates in list and correct change event is emitted`() {
        setUpSuccessPath()

        val observer = mock<Observer<EditProfileViewModel.ItemChangeEvent>>()

        viewModel.itemChangeEvent.observeForever(observer)

        val newCity = "LA"

        viewModel.onCityUpdated(newCity)

        val items = viewModel.profileAdapterItems.value!!
        val itemIndex =
            items.indexOfFirst { it is EditProfileAdapterItem.Field<*> && it.field is ProfileField.City }

        val field =
            ((items.get(itemIndex) as EditProfileAdapterItem.Field<*>).field as ProfileField.City)

        verify(observer).onChanged(EditProfileViewModel.ItemChangeEvent(itemIndex))

        Assert.assertEquals(newCity, field.data)
    }

    @Test
    fun `when phone is tapped, correct open editor event is emitted`() {
        setUpSuccessPath()

        val items = viewModel.profileAdapterItems.value!!
        val phoneField = items.filterIsInstance<EditProfileAdapterItem.Field<*>>()
            .associateBy { it.field::class }
            .get(ProfileField.Phone::class)
                as EditProfileAdapterItem.Field

        val observer = mock<Observer<ProfileField<*>>>()
        viewModel.openEditorEvent.observeForever(observer)
        viewModel.onEditProfileFieldClick(phoneField)

        verify(observer).onChanged(phoneField.field)
    }

    @Test
    fun `when phone is edited, value updates in list and correct change event is emitted`() {
        setUpSuccessPath()

        val observer = mock<Observer<EditProfileViewModel.ItemChangeEvent>>()

        viewModel.itemChangeEvent.observeForever(observer)

        val newPhone = "+447765811388"

        viewModel.onPhoneUpdated(newPhone)

        val items = viewModel.profileAdapterItems.value!!
        val itemIndex =
            items.indexOfFirst { it is EditProfileAdapterItem.Field<*> && it.field is ProfileField.Phone }

        val field =
            ((items.get(itemIndex) as EditProfileAdapterItem.Field<*>).field as ProfileField.Phone)

        verify(observer).onChanged(EditProfileViewModel.ItemChangeEvent(itemIndex))

        Assert.assertEquals(newPhone, field.data)
    }

}