package com.sohohouse.seven.book.table.booked

import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.LiveEvent
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.book.table.TableBookingErrorMapper
import com.sohohouse.seven.book.table.model.BookedTable
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.isSuccessful
import com.sohohouse.seven.network.core.models.TableReservation
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class BookedTableDetailsViewModel @AssistedInject constructor(
    private val apiService: SohoApiService,
    private val stringProvider: StringProvider,
    private val venueRepo: VenueRepo,
    @Assisted private val id: String,
    @Assisted private val isOpenedFromNotification: Boolean,
    analyticsManager: AnalyticsManager,
    dispatcher: CoroutineDispatcher,
) : BaseViewModel(analyticsManager, dispatcher), Loadable.ViewModel by Loadable.ViewModelImpl() {

    private lateinit var booking: BookedTable

    val finish = MutableLiveData(false)
    val error = MutableLiveData<Int>()
    private val _navigateToUpdateBooking = LiveEvent<BookedTable>()
    val navigateToUpdateBooking: LiveData<BookedTable> get() = _navigateToUpdateBooking


    private val _details = MutableLiveData<BookedTable>()
    val details: LiveData<BookedTable> get() = _details

    init {
        viewModelScope.launch(viewModelContext) {
            setLoading()

            when (val tableDetails = apiService.getTableDetails(id)) {
                is ApiResponse.Error -> {
                    error.postValue(TableBookingErrorMapper.handleError(tableDetails.firstErrorCode()))
                }
                is ApiResponse.Success -> {
                    val it: TableReservation = tableDetails.response
                    booking = BookedTable.from(
                        it,
                        venueRepo.venues().findById(tableDetails.response.venue?.parentId),
                        stringProvider
                    )
                    _details.postValue(booking)
                }
            }

            setIdle()
        }
    }


    fun cancelBooking() {
        if (booking.bookedTableDate.isCancellable()) {
            cancelBookingInternal()
        } else {
            logBookingEventAction(AnalyticsManager.Action.TableBookingCancelRestricted)
            error.postValue(R.string.no_longer_able_to_cancel_reservation)
        }
    }

    private fun cancelBookingInternal() {
        logBookingEventAction(
            if (isOpenedFromNotification)
                AnalyticsManager.Action.TableBookingCancelFromNotification
            else
                AnalyticsManager.Action.TableBookingCancel
        )

        viewModelScope.launch(viewModelContext) {
            setLoading()

            val result = apiService.cancelTableBooking(booking.id)

            if (result.isSuccessful())
                finish.postValue(true)
            else
                error.postValue(TableBookingErrorMapper.handleError(result.firstErrorCode()))


            setIdle()
        }
    }

    fun updateBooking() {
        if (booking.bookedTableDate.isEditable()) {
            logBookingEventAction(
                if (isOpenedFromNotification)
                    AnalyticsManager.Action.TableBookingEditFromNotification
                else
                    AnalyticsManager.Action.TableBookingEdit
            )
            _navigateToUpdateBooking.value = booking
        } else {
            logBookingEventAction(AnalyticsManager.Action.TableBookingEditRestricted)
            error.postValue(R.string.no_longer_able_to_modify_reservation)
        }

    }


    private fun logBookingEventAction(action: AnalyticsManager.Action) {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.TableBookingEditRestricted,
            bundleOf(
                AnalyticsManager.Action.BookingId.value to booking.id,
                AnalyticsManager.Action.HouseId.value to booking.venueId
            )
        )
    }

    fun logBookedTableDateCrash(bookedTable: BookedTable, e: Exception) {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.TableBookingModifyException,
            AnalyticsManager.Bookings.getBookedTableParams(bookedTable)
        )
        FirebaseCrashlytics.getInstance().recordException(e)
    }

    @AssistedFactory
    interface Factory {
        fun create(id: String, isOpenedFromNotification: Boolean): BookedTableDetailsViewModel
    }

}