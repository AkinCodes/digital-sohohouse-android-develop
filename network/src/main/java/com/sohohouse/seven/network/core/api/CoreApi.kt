package com.sohohouse.seven.network.core.api

import com.sohohouse.seven.network.chat.create.DMChannelRequest
import com.sohohouse.seven.network.chat.create.DMChannelResponse
import com.sohohouse.seven.network.chat.invite.SentMessageRequest
import com.sohohouse.seven.network.common.utils.NetworkVariantConfig
import com.sohohouse.seven.network.core.models.*
import com.sohohouse.seven.network.core.models.housepay.Check
import com.sohohouse.seven.network.core.models.housepay.HouseCredit
import com.sohohouse.seven.network.core.models.housepay.PayCheckByCardInfo
import com.sohohouse.seven.network.core.models.housepay.Payment
import com.sohohouse.seven.network.core.models.notification.DeviceRegistration
import com.sohohouse.seven.network.core.models.notification.Notification
import com.sohohouse.seven.network.core.models.notification.NotificationGroup
import moe.banana.jsonapi2.Document
import moe.banana.jsonapi2.ResourceIdentifier
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Body

interface CoreApi {

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @POST("/communications/notification_device_registrations")
    suspend fun postFCMDeviceRegistration(@Body registration: DeviceRegistration): DeviceRegistration

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("tables/availabilities")
    suspend fun checkTableAvailability(
        @Query("filter[restaurant_id]", encoded = true) restaurantId: String,
        @Query("filter[start_date_time]", encoded = true) startDate: String,
        @Query("filter[party_size]", encoded = true) partySize: Int,
        @Query("filter[search_alternatives]", encoded = true) searchAlternatives: Boolean,
    ): List<TableAvailabilities>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @POST("tables/locks?include=venue")
    suspend fun lockTableSlot(@Body tableLockRequest: SlotLock): SlotLock

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("tables/table_bookings/{id}?include=venue,restaurant")
    suspend fun getTableBookingDetails(@Path("id") id: String): TableReservation

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @POST("tables/table_bookings?include=venue")
    suspend fun reserveTable(@Body reservation: TableReservation): TableReservation

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("tables/table_bookings?include=venue,restaurant")
    fun getTableBookings(
        @Query("filter[status]") status: String? = null,
        @Query("filter[date_time][from]") fromDate: String? = null,
        @Query("filter[date_time][to]") toDate: String? = null,
    ): Call<List<TableReservation>>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @DELETE("tables/table_bookings/{id}")
    suspend fun cancelTableBooking(@Path("id") id: String): Response<Unit>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @PATCH("tables/table_bookings/{id}")
    suspend fun updateTableBooking(
        @Path("id") id: String,
        @Body booking: TableReservation,
    ): TableReservation

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @POST("events/bookings")
    fun createBooking(@Body bookingRequest: EventBooking)

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("events/bookings")
    fun getEventBookings(
        @Query("filter[event_type]") filterType: String? = null,
        @Query("per_page") perPage: Int? = null,
        @Query("page") page: Int? = null,
        @Query("include") includeResources: String? = null,
        @Query("filter[starts_at][from]") startsAtFrom: String? = null,
        @Query("filter[starts_at][to]") startsAtTo: String? = null,
        @Query("filter[ends_at][from]") endsAtFrom: String? = null,
        @Query("filter[state]") filterState: String? = null,
        @Query("sort") order: String? = null,
    ): Call<List<EventBooking>>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("rooms/room_bookings")
    fun getRoomBookings(
        @Query("per_page") perPage: Int? = null,
        @Query("page") page: Int? = null,
        @Query("include") includeResources: String? = null,
        @Query("filter[starts_at][from]") startsAtFrom: String? = null,
        @Query("filter[starts_at][to]") startsAtTo: String? = null,
        @Query("filter[ends_at][from]") endsAtFrom: String? = null,
        @Query("sort") order: String? = null,
    ): Call<List<RoomBooking>>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @POST("/events/bookings")
    fun postEventBooking(@Body document: Document): Call<EventBooking>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @POST("/events/bookings?include=event%2Cvenue")
    suspend fun postEventBookingV2(@Body document: Document): EventBooking

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("events/bookings/{booking_id}")
    fun readBooking(
        @Path("booking_id") bookingID: String = "",
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null,
        @Query("filter[type]") filterType: String? = null,
        @Query("filter[state]") filterStates: String? = null,
        @Query("include") includeResources: String? = null,
    ): Call<Document>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @PATCH("/events/bookings/{booking_id}?include=event%2Cvenue")
    fun patchBookingState(
        @Path("booking_id") eventID: String,
        @Body bookingRequest: PatchBookingState,
    ): Call<Void>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @PATCH("events/bookings/{booking_id}")
    fun updateBooking(
        @Path("booking_id") bookingID: String,
        @Body bookingRequest: Document,
    ): Call<EventBooking>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @PATCH("events/bookings/{booking_id}/relationships/events")
    fun updateInductionBooking(
        @Path("booking_id") bookingID: String,
        @Body event: Document,
    ): Call<EventBooking>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @PATCH("events/bookings/{booking_id}/relationships/events")
    suspend fun updateInductionBookingV2(
        @Path("booking_id") bookingID: String,
        @Body event: Document,
    ): EventBooking

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @DELETE("events/bookings/{booking_id}")
    fun deleteBooking(@Path("booking_id") bookingID: String): Call<Void>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("events/events")
    fun getEvents(
        @Query(value = "filter[location_id]", encoded = true) filterLocationID: String? = null,
        @Query(value = "filter[recommend]", encoded = true) recommend: String? = null,
        @Query(
            value = "filter[parent_location_id]",
            encoded = true
        ) filterParentLocationID: String? = null,
        @Query(value = "filter[type]", encoded = true) filterType: String? = null,
        @Query(value = "filter[category]", encoded = true) filterCategories: String? = null,
        @Query(value = "filter[featured]", encoded = true) isFeatured: Boolean? = null,
        @Query(value = "filter[state]", encoded = true) filterState: String? = null,
        @Query(
            value = "filter[minimum_tickets_available]",
            encoded = true
        ) minTicketsAvailable: Int? = null,
        @Query(value = "filter[is_paid]", encoded = true) isPaid: Boolean? = null,
        @Query(value = "filter[venue_date][from]", encoded = true) venueDateFrom: String? = null,
        @Query(value = "filter[venue_date][to]", encoded = true) venueDateTo: String? = null,
        @Query(
            value = "filter[venue_end_date][from]",
            encoded = true
        ) venueEndDateFrom: String? = null,
        @Query(value = "filter[venue_end_date][to]", encoded = true) venueEndDateTo: String? = null,
        @Query(value = "filter[ends_at][from]", encoded = true) endsAtFrom: String? = null,
        @Query(value = "filter[starts_at][from]", encoded = true) startsAtFrom: String? = null,
        @Query(value = "filter[starts_at][to]", encoded = true) startsAtTo: String? = null,
        @Query(value = "page", encoded = true) page: Int? = null,
        @Query(value = "per_page", encoded = true) perPage: Int? = null,
        @Query(value = "include", encoded = true) includeResources: String? = null,
        @Query(value = "filter[digital]", encoded = true) filterDigitalEvents: Boolean? = null,
        @Query(value = "sort", encoded = true) sort: String? = null,
    ): Call<List<Event>>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("events/event_categories")
    fun getEventsCategories(): Call<List<EventCategory>>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("events/events/{event_id}")
    fun getEvent(
        @Path(value = "event_id") eventID: String,
        @Query(value = "include", encoded = true) includeResources: String? = null,
    ): Call<Event>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("events/events/{event_id}")
    suspend fun getEventV2(
        @Path(value = "event_id") eventID: String,
        @Query(value = "include", encoded = true) includeResources: String? = null,
    ): Event

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("profiles/accounts/me")
    fun getAccount(
        @Query(
            value = "include",
            encoded = true
        ) includeResources: String? = null,
    ): Call<Account>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("profiles/memberships/me")
    fun getMembership(
        @Query(
            value = "include",
            encoded = true
        ) includeResources: String? = null,
    ): Call<Membership>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @PATCH("profiles/accounts/me")
    fun patchAccountAttributes(@Body accountUpdate: AccountUpdate): Call<Account>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @PATCH("profiles/memberships/me")
    fun patchMembershipAttributes(@Body membershipUpdate: UpdateMembership): Call<Membership>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("profiles/profiles/me")
    fun getMyProfile(
        @Query(
            value = "include",
            encoded = true
        ) includeResources: String? = "interests,connections,mutual_connections,mutual_connection_requests",
    ): Call<Profile>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("profiles/profiles/{id}")
    fun getProfile(
        @Path(value = "id") id: String,
        @Query(
            value = "include",
            encoded = true
        ) includeResources: String? = "interests,connections,mutual_connections,mutual_connection_requests",
    ): Call<Profile>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @POST("/profiles/short_profile_urls")
    suspend fun createShortProfileUrls(@Body requestBody: ShortProfileUrlRequest): ShortProfileUrlResponse

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @PATCH("profiles/profiles/me")
    fun patchProfile(
        @Body profile: Profile,
        @Query(
            value = "include",
            encoded = true
        ) includeResources: String? = "interests",
    ): Call<Profile>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @PATCH("profiles/profiles/me")
    fun patchProfilePhoto(
        @Body body: ProfilePhotoUpdate,
        @Query(value = "include", encoded = true) includeResources: String? = "interests",
    ): Call<Profile>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @PATCH("profiles/accounts/me")
    fun patchProfileAccount(
        @Body accountUpdate: ProfileAccountUpdate,
        @Query(
            value = "include",
            encoded = true
        ) includeResources: String? = "profile,profile.interests",
    ): Call<Account>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("profiles/account_onboardings/me")
    suspend fun getAccountOnboardingStatus(): AccountOnboarding

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @PATCH("profiles/account_onboardings/me")
    suspend fun patchAccountOnboarding(@Body accountOnboarding: AccountOnboarding): AccountOnboarding

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("venues/venues")
    fun getVenues(
        @Query(value = "include", encoded = true) includeResources: String? = null,
        @Query(value = "filter[is_top_level]", encoded = true) isTopLevel: Boolean? = true,
        @Query(value = "filter[venue_type]", encoded = true) venueTypes: String? = null,
    ): Call<List<Venue>>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @PATCH("profiles/accounts/me/relationships/favorite_venues")
    fun patchVenues(@Body venues: Array<ResourceIdentifier>? = null): Call<List<Venue>>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("content/categories")
    fun getContentCategories(): Call<List<ContentCategory>>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("payments/cards")
    fun getPaymentCards(
        @Query(value = "filter[purpose]", encoded = true) purpose: String? = null,
        @Query(value = "filter[venue]", encoded = true) venue: String? = null,
    ): Call<List<Card>>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/communications/v2/device_notification_preferences")
    fun getDeviceNotificationPreferences(): Call<List<DeviceNotificationPreferences>>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @PATCH("/communications/v2/device_notification_preferences/{id}")
    suspend fun patchDeviceNotificationPreferences(
        @Body devNotify: DeviceNotificationPreferences,
        @Path(value = "id") id: String,
    ): DeviceNotificationPreferences

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/profiles/communication_preferences/me")
    fun getCommunicationPreferences(): Call<CommunicationPreference>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @PATCH("/profiles/communication_preferences/me")
    fun patchCommunicationPreferences(@Body communicationPreference: CommunicationPreference): Call<CommunicationPreference>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/communications/notification_subscriptions")
    fun getNotificationSubscriptions(
        @Query(value = "filter[resource][type]", encoded = true) resourceType: String? = null,
        @Query(value = "filter[resource][id]", encoded = true) resourceId: String? = null,
        @Query(value = "filter[action]", encoded = true) action: String? = null,
    ): Call<List<NotificationSubscription>>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @POST("/communications/notification_subscriptions")
    fun postNotificationSubscriptions(@Body document: Document): Call<NotificationSubscription>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @DELETE("/communications/notification_subscriptions/{subscription_id}")
    fun deleteNotificationSubscription(@Path(value = "subscription_id") subscriptionId: String): Call<Void>

    @Headers(NetworkVariantConfig.CORE_API_FORMS_VERSION_HEADER)
    @POST("payments/forms")
    fun getPaymentForm(@Body document: Document): Call<Form>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @POST("payments/cards")
    fun postPaymentCard(@Body document: Document): Call<Card>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @PATCH("/payments/cards/{card_id}")
    fun patchPayment(
        @Path(value = "card_id") cardId: String,
        @Body card: Card,
    ): Call<Card>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @DELETE("/payments/cards/{card_id}")
    fun deletePayment(@Path(value = "card_id") cardId: String): Call<Void>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/connect/checkins")
    fun getRollCall(
        @Query(value = "filter[venue_ids]", encoded = true) venueId: String? = null,
        @Query(value = "filter[tags][city]", encoded = true) cities: String? = null,
        @Query(value = "filter[tags][theme]", encoded = true) topics: String? = null,
        @Query(value = "filter[profile_id]", encoded = true) profileId: String? = null,
        @Query(value = "include", encoded = true) includeResources: String? = null,
        @Query(value = "page[limit]", encoded = true) perPage: Int? = null,
        @Query(value = "page[cursor]", encoded = true) cursor: String? = null,
    ): Call<List<Checkin>>

    @Deprecated("use getNoticeboardPost")
    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/connect/checkins/{checkin_id}")
    fun getPost(
        @Path(value = "checkin_id") postId: String,
        @Query(value = "include", encoded = true) includeResources: String? = null,
    ): Call<Checkin>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/connect/checkins/{checkin_id}")
    suspend fun getNoticeboardPost(
        @Path(value = "checkin_id") postId: String,
        @Query(value = "include", encoded = true) includeResources: String? = null,
    ): Checkin

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @DELETE("/connect/checkins/{checkin_id}")
    fun deleteRollCall(@Path(value = "checkin_id") checkinID: String): Call<Void>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @POST("/connect/checkins")
    fun postRollCall(
        @Body document: Document,
        @Query(value = "include", encoded = true) includes: String? = null,
    ): Call<Checkin>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @POST("/support/inquiries")
    fun postInquiry(@Body document: Document): Call<Inquiry>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/content/house_notes")
    fun getHouseNotes(
        @Query(value = "filter[is_featured]", encoded = true) isFeatured: Boolean? = null,
        @Query(
            value = "filter[content_category_id]",
            encoded = true
        ) contentCategoryIds: String? = null,
        @Query(value = "filter[venue_id]", encoded = true) venueIds: String? = null,
        @Query(value = "filter[type]", encoded = true) types: String? = null,
        @Query(value = "page", encoded = true) page: Int? = null,
        @Query(value = "per_page", encoded = true) perPage: Int? = null,
        @Query(value = "include", encoded = true) includeResources: String? = null,
    ): Call<List<HouseNotes>>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/content/static_pages")
    fun getStaticPages(): Call<List<StaticPages>>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/content/static_pages/privacy_policy")
    fun getPrivacyPolicy(): Call<List<StaticPages>>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/content/static_pages/soho_friends_terms")
    fun getFriendsTerms(): Call<List<StaticPages>>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @POST("/auth/redirect_urls")
    fun postRedirectUrls(@Body document: Document): Call<RedirectUrl>

    @GET("/content/house_notes/{house_notes_id}")
    fun getHouseNoteDetails(
        @Path(value = "house_notes_id") houseNoteId: String,
        @Query(value = "include", encoded = true) includeResources: String? = null,
    ): Call<HouseNotes>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/content/perks")
    fun getPerks(
        @Query(value = "page", encoded = true) page: Int? = null,
        @Query(value = "per_page", encoded = true) perPage: Int? = null,
        @Query(value = "filter[region]", encoded = true) region: String? = null,
        @Query(value = "filter[city]", encoded = true) cities: String? = null,
    ): Call<List<Perk>>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/content/perks/{perks_id}")
    fun getPerkbyId(@Path(value = "perks_id") perksId: String): Call<Perk>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/profiles/interests")
    fun getInterests(
        @Query(value = "filter[name][prefix]") filter: String?,
        @Query(value = "per_page", encoded = true) perPage: Int? = null,
    ): Call<List<Interest>>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/profiles/occupations")
    fun getOccupations(
        @Query(value = "filter[name][prefix]") filter: String,
        @Query(value = "per_page") pageSize: Int,
    ): Call<List<Occupation>>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/communications/notifications")
    fun getNotifications(
        @Query(
            value = "include",
            encoded = true
        ) include: String?,
    ): Call<List<Notification>>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @PATCH("/communications/notifications/{notification_id}")
    fun patchNotification(
        @Path(value = "notification_id") id: String,
        @Body notification: Notification,
    ): Call<Notification>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @PATCH("/communications/notification_groups/{notification_group_id}")
    fun patchNotificationGroup(
        @Path(value = "notification_group_id") id: String,
        @Body notificationGroup: NotificationGroup,
    ): Call<NotificationGroup>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @POST("/profiles/account_verification_emails")
    fun postAccountVerificationEmail(@Body requestBody: SendVerificationLink): Call<SendVerificationLink>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/profiles/availability_statuses")
    fun getProfileAvailabilityStatuses(
        @Query(value = "per_page", encoded = true) perPage: Int = 100,
        @Query(value = "page", encoded = true) page: Int = 1,
    ): Call<List<ProfileAvailabilityStatus>>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/profiles/availability_statuses/me")
    fun getProfileAvailabilityStatus(
    ): Call<ProfileAvailabilityStatus>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @PATCH("/profiles/availability_statuses/me")
    fun patchProfileAvailabilityStatus(@Body profileAvailabilityStatus: ProfileAvailabilityStatus): Call<ProfileAvailabilityStatus>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @DELETE("/profiles/availability_statuses/{id}")
    fun deleteProfileAvailabilityStatus(@Path(value = "id") id: String): Call<Void>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/guests/guest_lists")
    fun getGuestLists(
        @Query(value = "filter[date][from]") dateFrom: String? = null,
        @Query(value = "filter[date][to]") dateTo: String? = null,
        @Query(value = "include", encoded = true) include: String? = null,
    ): Call<List<GuestList>>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/guests/guest_lists/{id}")
    fun getGuestList(
        @Path(value = "id") id: String,
        @Query("include") includeResources: String? = null,
    ): Call<GuestList>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @POST("/guests/guest_lists")
    fun postGuestList(@Body guestList: PostGuestList): Call<GuestList>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @POST("/guests/invites")
    fun postInvite(@Body invite: Invite): Call<Invite>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @PATCH("/guests/invites/{id}")
    fun patchInvite(@Body invite: InviteUpdate, @Path(value = "id") id: String): Call<Invite>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @DELETE("/guests/guest_lists/{id}")
    fun deleteGuestList(@Path(value = "id") id: String): Call<Void>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/content/perk_cities")
    fun getPerksCities(): Call<List<City>>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/profiles/mutual_connections")
    fun getConnections(
        @Query("per_page") perPage: Int? = null,
        @Query("page") page: Int? = null,
        @Query(value = "include", encoded = true) include: String? = "sender,receiver",
    ): Call<List<MutualConnections>>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/profiles/mutual_connection_requests")
    fun getConnectionRequests(
        @Query("per_page") perPage: Int? = null,
        @Query("page") page: Int? = null,
        @Query(value = "include", encoded = true) include: String? = "sender,receiver",
    ): Call<List<MutualConnectionRequests>>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @POST("/profiles/mutual_connection_requests")
    fun postConnectionRequest(@Body connection: MutualConnectionRequests): Call<MutualConnectionRequests>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @PATCH("/profiles/mutual_connection_requests/{id}")
    fun patchAcceptConnectionRequest(
        @Path(value = "id") id: String,
        @Body connection: MutualConnectionRequests,
    ): Call<MutualConnectionRequests>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @DELETE("/profiles/mutual_connections/{id}")
    fun deleteMutualConnection(@Path(value = "id") id: String): Call<Void>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/chat/blocked_members_lists/me")
    fun getBlockedMembers(
        @Query(
            value = "include",
            encoded = true
        ) include: String? = "sender,receiver",
    ): Call<BlockedMemberList>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @PATCH("/chat/blocked_members_lists/me")
    fun patchUnblockMember(@Body body: BlockedMemberList): Call<BlockedMemberList>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @POST("/support/v2/inquiries")
    fun postReportUser(@Body body: ReportMember): Call<Void>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @POST("/chat/message_invites")
    fun sendMessageRequest(@Body body: SentMessageRequest): Call<Void>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @POST("/chat/chat_tokens")
    suspend fun getSendBirdAccessToken(@Body tokenType: SendBirdTokenRequest): SendBirdToken

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/recommendation-engine-service/recommendations")
    suspend fun getRecommendations(
        @Query(value = "filter[industry]") industry: String? = null,
        @Query(value = "filter[city]") city: String? = null,
        @Query(value = "filter[interests]") interests: String? = null,
    ): List<RecommendationDto>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/connect/checkin_reaction_icons")
    suspend fun getReactionIcons(): List<CheckinReactionIcons>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @DELETE("/connect/checkins/{id}/checkin_reactions/me")
    suspend fun removeReaction(
        @Path(value = "id") id: String,
    ): Response<Unit> // TODO improve error handling

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @POST("/connect/checkins/{id}/checkin_reactions")
    suspend fun addReaction(
        @Path(value = "id") checkInID: String,
        @Body body: CheckInReactionByUser,
    ): CheckInReactionByUser

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/connect/checkins/{id}/checkin_reactions")
    suspend fun getProfilesOfUsersWhoHaveReactedToPost(
        @Path(value = "id") id: String,
        @Query(value = "include", encoded = true) include: String? = "profile,venues",
    ): List<CheckInReactionByUser>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/checks/checks")

    suspend fun getChecks(
        @Query(value = "page") page: Int,
        @Query(value = "per_page") perPage: Int = 10,
        @Query(value = "filter[status]") status: String?,
        @Query(value = "include", encoded = true) include: String? = null,
    ): List<Check>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/checks/wallets/me")
    suspend fun getWallets(): List<Wallet>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/checks/checks/{id}")
    suspend fun getCheck(
        @Path(value = "id") id: String,
        @Query(value = "include", encoded = true) include: String? = null,
    ): Check

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @POST("/checks/checks/{id}/discount")
    suspend fun postCheckDiscount(
        @Path(value = "id") id: String,
        @Query(value = "include", encoded = true) include: String? = null,
    ): Check

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("payments/cards")
    suspend fun getPaymentMethods(
        @Query(value = "filter[purpose]", encoded = true) purpose: String? = null,
        @Query(value = "filter[venue]", encoded = true) venue: String? = null,
    ): List<Card>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @GET("/checks/soho_house_credits")
    suspend fun getHouseCredits(
        @Query(value = "currency") currency: String,
    ): HouseCredit

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @POST("/checks/payments")
    suspend fun postCheckCardPayment(
        @Body body: PayCheckByCardInfo,
    ): Payment

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @POST("/checks/checks/{id}/email")
    suspend fun postEmailReceipt(
        @Path("id") checkId: String,
    ): Response<Unit>

    @Headers(NetworkVariantConfig.CORE_API_VERSION_HEADER)
    @POST("/chat/message_channels")
    suspend fun createDMchannel(@Body body: DMChannelRequest): DMChannelResponse

}
