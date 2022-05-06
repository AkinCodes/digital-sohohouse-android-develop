package com.sohohouse.seven.book.table.completebooking

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LiveEvent
import com.sohohouse.seven.book.table.PhoneCode
import com.sohohouse.seven.book.table.PhoneCodeRepository
import com.sohohouse.seven.book.table.TableBookingDetails
import com.sohohouse.seven.book.table.TableBookingErrorMapper
import com.sohohouse.seven.book.table.model.BookedTable
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.dateTime
import com.sohohouse.seven.common.extensions.getFormattedDateTime
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.ErrorResponse
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.models.Phone
import com.sohohouse.seven.network.core.models.TableReservation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import moe.banana.jsonapi2.HasOne
import javax.inject.Inject

class TableCompleteBookingViewModel @Inject constructor(
    private val apiService: SohoApiService,
    private val phoneCodeRepo: PhoneCodeRepository,
    private val userManager: UserManager,
    analyticsManager: AnalyticsManager,
    dispatcher: CoroutineDispatcher
) : BaseViewModel(analyticsManager, dispatcher), Loadable.ViewModel by Loadable.ViewModelImpl() {

    companion object {
        const val DEFAULT_COUNTRY_CODE = "UK"
    }

    private var details: TableBookingDetails? = null
    private var detailsUpdate: BookedTable? = null
    private var isTermsChecked: Boolean = false
    private var isConfirmationChecked: Boolean = false

    val phoneCodes = MutableLiveData<List<PhoneCode>>()
    val selectedCode = MutableLiveData<Int>()
    val bookingCompleted = MutableLiveData<TableBookingDetails>()
    val bookingErrorMessage = MutableLiveData<Int>()
    val enableBookButton = MutableLiveData<Boolean>()
    val phone = MutableLiveData<String>()
    val updateCompleted = LiveEvent<BookedTable>()

    fun init(details: TableBookingDetails?, detailsUpdate: BookedTable?): ConfirmationData {
        this.details = details
        this.detailsUpdate = detailsUpdate

        refreshBookButtonState()
        populatePhoneNumbers()

        return if (details != null) {
            ConfirmationData(
                details,
                "${userManager.profileFirstName} ${userManager.profileLastName}",
                userManager.prefsManager.email
            )
        } else {
            ConfirmationData(
                detailsUpdate!!,
                "${userManager.profileFirstName} ${userManager.profileLastName}",
                userManager.prefsManager.email
            )
        }
    }

    fun checkTermsOfConditions(isChecked: Boolean) {
        isTermsChecked = isChecked
        refreshBookButtonState()
    }

    fun checkConfirmation(isChecked: Boolean) {
        isConfirmationChecked = isChecked
        refreshBookButtonState()
    }

    fun createBooking(countryCode: String, phone: String, comments: String) {
        viewModelScope.launch(viewModelContext) {

            setLoading()

            if (details != null) {
                if (comments.isNotBlank()) {
                    analyticsManager.logEventAction(
                        AnalyticsManager.Action.TableBookingAdditionalComment,
                        Bundle().apply { putString("house_id", details?.venueId) })
                }

                val result = apiService.createReservation(
                    TableReservation(
                        comments,
                        true,
                        true,
                        Phone(phone, countryCode),
                        HasOne(details?.slotLock)
                    )
                )

                when (result) {
                    is ApiResponse.Success<TableReservation> -> onBookingSuccess(result.response)
                    is ApiResponse.Error -> onBookingFails(result.response)
                }
            } else if (detailsUpdate != null) {
                if (comments.isNotBlank()) {
                    analyticsManager.logEventAction(
                        AnalyticsManager.Action.TableBookingAdditionalComment,
                        Bundle().apply { putString("house_id", detailsUpdate?.id) })
                }

                detailsUpdate?.specialComment = comments
                val result = apiService.updateTableBooking(
                    detailsUpdate?.id ?: "",
                    TableReservation(comments, true, true, null, HasOne(detailsUpdate?.slotLock))
                )

                when (result) {
                    is ApiResponse.Success<TableReservation> -> onBookingSuccess(result.response)
                    is ApiResponse.Error -> onBookingFails(result.response)
                }
            }

            setIdle()
        }
    }

    private fun populatePhoneNumbers() {
        viewModelScope.launch(viewModelContext) {
            val result = phoneCodeRepo.getPhoneCodes()
            phoneCodes.postValue(result)
            selectedCode.postValue(result.indexOfFirst { it.code == DEFAULT_COUNTRY_CODE })

            detailsUpdate?.let { populatePhone(it, result) }
        }
    }

    private fun populatePhone(details: BookedTable, codes: List<PhoneCode>) {
        val code = codes.find { it.code == details.phone.country_code }?.dial_code ?: ""
        phone.postValue(code + details.phone.number)
    }

    private fun refreshBookButtonState() {
        enableBookButton.postValue(isTermsChecked && isConfirmationChecked)
    }

    private fun onBookingSuccess(booking: TableReservation) {
        if (details != null) {
            analyticsManager.logEventAction(
                AnalyticsManager.Action.TableBookingSummaryCreate,
                Bundle().apply {
                    putString("booking_id", booking.id); putString(
                    "house_id",
                    details?.venueId
                )
                })
            details?.booking = booking
            bookingCompleted.postValue(details)
        } else {
            analyticsManager.logEventAction(
                AnalyticsManager.Action.TableBookingSummaryEdit,
                Bundle().apply {
                    putString("booking_id", booking.id); putString(
                    "house_id",
                    detailsUpdate?.venueId
                )
                })
            detailsUpdate?.confirmationNumber = booking.confirmation_number
            updateCompleted.postValue(detailsUpdate)
        }
    }

    private fun onBookingFails(error: ErrorResponse?) {
        error?.let { bookingErrorMessage.postValue(TableBookingErrorMapper.handleError(it.errors?.firstOrNull()?.code)) }
    }

}

data class ConfirmationData(
    val name: String,
    val address: String,
    val country: String,
    val imageUrl: String,
    val date: String,
    val persons: String,
    val specialNotes: String,
    val username: String,
    val email: String,
    val specialComments: String
) {
    constructor(details: BookedTable, username: String, email: String) : this(
        name = details.name,
        address = details.address,
        country = details.country,
        imageUrl = details.imageUrl,
        date = details.slotLock?.dateTime?.getFormattedDateTime("") ?: "",
        persons = "${details.slotLock?.party_size} seats",
        specialNotes = details.specialNotes,
        username = username,
        email = email,
        specialComments = details.specialComment
    )

    constructor(details: TableBookingDetails, username: String, email: String) : this(
        name = details.name,
        address = details.address,
        country = details.country,
        imageUrl = details.imageUrl,
        date = details.slotLock?.dateTime?.getFormattedDateTime("") ?: "",
        persons = "${details.persons} seats",
        specialNotes = details.specialNotes,
        username = username,
        email = email,
        specialComments = ""
    )
}