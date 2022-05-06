package com.sohohouse.seven.network.core

import com.sohohouse.seven.network.chat.create.DMChannelRequest
import com.sohohouse.seven.network.chat.create.DMChannelResponse
import com.sohohouse.seven.network.core.api.CoreApi
import com.sohohouse.seven.network.core.common.PAYMENT_VENUE_ID_SOHO_HOUSE
import com.sohohouse.seven.network.core.common.extensions.formatWithCommas
import com.sohohouse.seven.network.core.models.*
import com.sohohouse.seven.network.core.models.housepay.PayCheckByCardInfo
import com.sohohouse.seven.network.core.models.notification.DeviceRegistration
import kotlinx.coroutines.CoroutineDispatcher
import moe.banana.jsonapi2.Document
import moe.banana.jsonapi2.HasOne
import retrofit2.Response

class SohoApiService(
    val api: CoreApi,
    errorReporter: NetworkErrorReporter,
    ioDispatcher: CoroutineDispatcher,
) : BaseApiService(errorReporter, ioDispatcher) {

    suspend fun patchDeviceNotificationPrefs(pref: DeviceNotificationPreferences): ApiResponse<DeviceNotificationPreferences> =
        apiCall("patchDeviceNotificationPrefs") {
            api.patchDeviceNotificationPreferences(
                pref,
                pref.id
            )
        }

    suspend fun checkTableAvailability(
        restaurantId: String,
        dateTime: String,
        partySize: Int,
        searchAlternatives: Boolean = true,
    ): ApiResponse<List<TableAvailabilities>> =
        apiCall("checkTableAvailability") {
            api.checkTableAvailability(
                restaurantId,
                dateTime,
                partySize,
                searchAlternatives
            )
        }

    suspend fun lockTable(slotLock: SlotLock): ApiResponse<SlotLock> =
        apiCall("lockTable") { api.lockTableSlot(slotLock) }

    suspend fun getTableDetails(id: String): ApiResponse<TableReservation> =
        apiCall("getTableDetails") { api.getTableBookingDetails(id) }

    suspend fun createReservation(reservation: TableReservation): ApiResponse<TableReservation> =
        apiCall("createReservation") { api.reserveTable(reservation) }

    suspend fun cancelTableBooking(id: String): ApiResponse<Response<Unit>> =
        apiCall("cancelTableBooking") { api.cancelTableBooking(id) }

    suspend fun updateTableBooking(
        id: String,
        booking: TableReservation,
    ): ApiResponse<TableReservation> =
        apiCall("updateTableBooking") { api.updateTableBooking(id, booking) }

    suspend fun registerDeviceForFCM(deviceRegistration: DeviceRegistration): ApiResponse<DeviceRegistration> =
        apiCall("registerDeviceForFCM") {
            api.postFCMDeviceRegistration(deviceRegistration)
        }

    suspend fun postEventBookingRequest(document: Document): ApiResponse<EventBooking> {
        return apiCall { api.postEventBookingV2(document) }
    }

    suspend fun patchEventBookingRequest(
        bookingId: String,
        document: Document,
    ): ApiResponse<EventBooking> {
        return apiCall { api.updateInductionBookingV2(bookingId, document) }
    }

    suspend fun getEvent(
        eventId: String,
        includeResources: Array<String>,
    ): ApiResponse<Event> {
        return apiCall {
            api.getEventV2(eventId, includeResources.formatWithCommas())
        }
    }

    suspend fun getRecommendations(
        industry: List<String> = emptyList(),
        city: List<String> = emptyList(),
        interests: List<String> = emptyList(),
    ): ApiResponse<List<RecommendationDto>> =
        apiCall("recommendations") {
            api.getRecommendations(
                industry = industry.joinToString(separator = ","),
                interests = interests.joinToString(separator = ","),
                city = city.joinToString(separator = ",")
            )
        }

    suspend fun getSendBirdAccessToken(): ApiResponse<SendBirdToken> {
        return apiCall {
            api.getSendBirdAccessToken(SendBirdTokenRequest())
        }
    }

    suspend fun getNoticeboardIcons(): ApiResponse<List<CheckinReactionIcons>> {
        return apiCall {
            api.getReactionIcons()
        }
    }

    suspend fun getProfilesOfUsersWhoHaveReactedToPost(id: String): ApiResponse<List<CheckInReactionByUser>> {
        return apiCall {
            api.getProfilesOfUsersWhoHaveReactedToPost(id)
        }
    }

    suspend fun reactToPost(id: String, reaction: String): ApiResponse<CheckInReactionByUser> {
        return apiCall {
            val linkedResource = CheckinReactionIcons()
            linkedResource.id = reaction
            api.addReaction(id, CheckInReactionByUser(_reaction = HasOne(linkedResource)))
        }
    }

    suspend fun removeReactionFromPost(id: String): ApiResponse<Unit> {
        return apiCall {
            api.removeReaction(id)
        }
    }

    suspend fun createShortProfilesUrls(profileId: String): ApiResponse<ShortProfileUrlResponse> {
        return apiCall {
            api.createShortProfileUrls(ShortProfileUrlRequest(profileId))
        }
    }

    suspend fun getChecks(
        page: Int,
        perPage: Int,
        status: String?,
        include: String?,
    ) = apiCall {
        api.getChecks(page, perPage, status, include)
    }

    suspend fun getCheck(
        id: String,
        include: String?,
    ) = apiCall {
        api.getCheck(id, include)
    }

    suspend fun getWallets() = apiCall {
        api.getWallets()
    }

    suspend fun postCheckDiscount(
        id: String,
        include: String?,
    ) = apiCall {
        api.postCheckDiscount(id, include)
    }

    suspend fun getPaymentMethods() = apiCall {
        api.getPaymentMethods(venue = PAYMENT_VENUE_ID_SOHO_HOUSE)
    }

    suspend fun getHouseCredit(currencyCode: String) = apiCall {
        api.getHouseCredits(currencyCode)
    }

    suspend fun payCheck(payCheckByCardInfo: PayCheckByCardInfo) = apiCall {
        api.postCheckCardPayment(payCheckByCardInfo)
    }

    suspend fun emailReceipt(checkId: String) = apiCall {
        api.postEmailReceipt(checkId)
    }

    suspend fun getAccountOnboardingStatus(): ApiResponse<AccountOnboarding> {
        return apiCall {
            api.getAccountOnboardingStatus()
        }
    }

    suspend fun patchAccountOnboarding(accountOnboarding: AccountOnboarding): ApiResponse<AccountOnboarding> {
        return apiCall {
            api.patchAccountOnboarding(accountOnboarding)
        }
    }

    suspend fun getNoticeboardPost(
        postId: String,
        includeReplies: Boolean,
        includeProfile: Boolean = true,
        includeVenues: Boolean = true,
    ): ApiResponse<Checkin> {
        var includes = arrayOf<String>()
        if (includeReplies) {
            includes = includes.plus(REPLIES_INCLUDE_TYPE)
        }
        if (includeProfile) {
            includes = includes.plus(PROFILE_INCLUDE_TYPE)
            includes = includes.plus(REPLIES_PROFILE_INCLUDE_TYPE)
        }
        if (includeVenues) {
            includes = includes.plus(VENUES_INCLUDE_TYPE)
        }
        includes = includes.plus("user_reaction")
        return apiCall {
            api.getNoticeboardPost(postId, includes.formatWithCommas())
        }
    }

    suspend fun createDMChannel(dmChannelRequest: DMChannelRequest): ApiResponse<DMChannelResponse> {
        return apiCall {
            api.createDMchannel(dmChannelRequest)
        }
    }

    companion object {
        private const val REPLIES_INCLUDE_TYPE = "replies"
        private const val VENUES_INCLUDE_TYPE = "venues"
        private const val PROFILE_INCLUDE_TYPE = "profile"
        private const val REPLIES_PROFILE_INCLUDE_TYPE = "replies.profile"
    }

}