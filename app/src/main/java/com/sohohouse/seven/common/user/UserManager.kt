package com.sohohouse.seven.common.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.salesforce.marketingcloud.MarketingCloudSdk
import com.sohohouse.seven.common.extensions.isStaff
import com.sohohouse.seven.common.extensions.toDate
import com.sohohouse.seven.common.prefs.PrefsManager
import com.sohohouse.seven.common.views.EventType
import com.sohohouse.seven.connect.trafficlights.AvailableStatus
import com.sohohouse.seven.connect.trafficlights.UserAvailableStatus
import com.sohohouse.seven.network.chat.model.MiniProfile
import com.sohohouse.seven.network.core.models.Account
import com.sohohouse.seven.network.core.models.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.threeten.bp.LocalDateTime
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserManager @Inject constructor(
    val prefsManager: PrefsManager,
    //    private val featureFlags: FeatureFlags
) {

    private var localHouseIdCached: String? = prefsManager.localHouseId

    var localHouseId: String
        get() = localHouseIdCached ?: prefsManager.localHouseId
        set(value) {
            localHouseIdCached = value
            prefsManager.localHouseId = value
        }
    var canAccessApp: Boolean
        get() = prefsManager.canAccessApp
        set(value) {
            Timber.d("Setting canAccessApp = $value")
            prefsManager.canAccessApp = value
        }

    var membershipType: String
        get() = prefsManager.membershipType
        set(value) {
            prefsManager.membershipType = value
        }

    var membershipStatus: MembershipStatus
        get() = if (MembershipStatus.values().map { it.name }
                .contains(prefsManager.membershipStatus))
            MembershipStatus.valueOf(prefsManager.membershipStatus)
        else
            MembershipStatus.NONE
        set(value) {
            Timber.d("setMembershipStatus = ${value.name}")
            prefsManager.membershipStatus = value.name
        }

    var isStaff: Boolean
        get() = prefsManager.isStaff
        set(value) {
            prefsManager.isStaff = value
        }

    var isInducted: Boolean
        get() = prefsManager.isInducted
        set(value) {
            prefsManager.isInducted = value
        }

    var isEmailVerified: Boolean
        get() = prefsManager.isEmailVerified
        set(value) {
            prefsManager.isEmailVerified = value
        }

    val shouldShowLandingOptInRecommendations: Boolean
        get() {
            val value = prefsManager.landingOptInRecommendationsStateForUser(profileID)
            if (value) prefsManager.landingOptInRecommendationsIsShownForUser(profileID)
            return value && subscriptionType != SubscriptionType.FRIENDS
        }

    var isAppOnboardingComplete: Boolean = false

    var isAppOnboardingNeeded: Boolean = false

    var hasSeenOnboardingBenefitsScreen: Boolean
        get() = prefsManager.hasSeenOnboardingBenefitsScreen
        set(value) {
            prefsManager.hasSeenOnboardingBenefitsScreen = value
        }

    var hasSeenOnboardingWelcomeScreen: Boolean
        get() = prefsManager.hasSeenOnboardingWelcomeScreen
        set(value) {
            prefsManager.hasSeenOnboardingWelcomeScreen = value
        }

    var calendarSubscriptionUrl: String
        get() = prefsManager.calendarSubscriptionUrl
        set(value) {
            prefsManager.calendarSubscriptionUrl = value
        }

    private val _liveFavouriteHouses = MutableStateFlow(favouriteHouses)
    val liveFavouriteHouses: LiveData<List<String>> get() = _liveFavouriteHouses.asLiveData()
    var favouriteHouses: List<String>
        get() = prefsManager.favouriteHouses ?: listOf()
        set(value) {
            prefsManager.favouriteHouses = value
            prefsManager.favouriteHouses = value
        }

    var favouriteContentCategories: List<String>? = null

    var didConsentTermsConditions: Boolean
        get() = prefsManager.didConsentTermsConditions
        set(value) {
            prefsManager.didConsentTermsConditions = value
        }

    var didConsentHousePayTermsConditions: Boolean
        get() = prefsManager.didConsentHousePayTermsConditions
        set(value) {
            prefsManager.didConsentHousePayTermsConditions = value
        }

    var didConsentAnalytics: Boolean
        get() = prefsManager.didConsentAnalytics
        set(value) {
            prefsManager.didDecideAnalytics = true
            prefsManager.didConsentAnalytics = value
        }

    var email: String
        get() = prefsManager.email
        set(value) {
            prefsManager.email = value
        }

    var didDecideAnalytics: Boolean
        get() = prefsManager.didDecideAnalytics
        set(value) {
            prefsManager.didDecideAnalytics = value
        }

    var analyticsConsent: AnalyticsConsent
        get() = prefsManager.analyticsConsent
        set(value) {
            prefsManager.analyticsConsent = value
        }

    var accountId: String
        get() = prefsManager.accountId
        set(value) {
            prefsManager.accountId = value
        }

    var profileID: String
        get() = prefsManager.profileID
        set(value) {
            prefsManager.profileID = value
        }

    var profileImageURL: String
        get() = prefsManager.profileImageURL
        set(value) {
            prefsManager.profileImageURL = value
        }

    var profileFirstName: String
        get() = prefsManager.firstName
        set(value) {
            prefsManager.firstName = value
        }

    var profileLastName: String
        get() = prefsManager.lastName
        set(value) {
            prefsManager.lastName = value
        }

    var profileOccupation: String
        get() = prefsManager.occupation
        set(value) {
            prefsManager.occupation = value
        }

    var profileLocation: String
        get() = prefsManager.location
        set(value) {
            prefsManager.location = value
        }

    var paymentUpdateUrl: String
        get() = prefsManager.paymentUpdateUrl
        set(value) {
            prefsManager.paymentUpdateUrl = value
        }

    var isNotificationDialogComplete: Boolean
        get() = prefsManager.isNotificationDialogComplete
        set(value) {
            prefsManager.isNotificationDialogComplete = value
        }

    var attendedVenueId: String
        get() = prefsManager.attendedVenueId
        set(value) {
            prefsManager.attendedVenueId = value
        }

    var subscriptionType: SubscriptionType
        get() = prefsManager.subscriptionType
        set(value) {
            prefsManager.subscriptionType = value
            Timber.d("setSubcriptionType = ${value.name}")
        }

    var gymMembership: GymMembership
        get() = prefsManager.gymMembership
        set(value) {
            prefsManager.gymMembership = value
        }

    var confirmProfile: Boolean
        get() = prefsManager.confirmProfile
        set(value) {
            prefsManager.confirmProfile = value
        }

    var connectRecommendationOptIn: String
        get() = prefsManager.connectRecommendationOptIn
        set(value) {
            prefsManager.connectRecommendationOptIn = value
        }

    private val _availableStatusFlow = MutableStateFlow(
        UserAvailableStatus(
            id = "",
            availableStatus = AvailableStatus.from(
                prefsManager.availableStatus.ifEmpty {
                    AvailableStatus.UNAVAILABLE.value
                }
            )
        )
    )
    val availableStatusFlow: StateFlow<UserAvailableStatus> get() = _availableStatusFlow

    private val _isCheckedIn = MutableStateFlow(false)
    val isCheckedIn: StateFlow<Boolean> get() = _isCheckedIn

    fun updateInfo(firstName: String, lastName: String, profileImage: String) {
        profileFirstName = firstName
        profileLastName = lastName
        profileImageURL = profileImage
    }

    fun saveUser(account: Account) {
        accountId = account.id
        localHouseId = account.localHouseResource?.get()?.id ?: ""
        canAccessApp = account.canAccessApp
        didConsentTermsConditions = account.termsConditionsConsent == true
        didConsentHousePayTermsConditions = account.housePayTermsConsent == true
        isEmailVerified = account.emailVerified

        saveProfile(account.profile)

        calendarSubscriptionUrl = account.calendarSubscriptionUrl
        paymentUpdateUrl = account.membershipPaymentUrl

        email = account.email
        favouriteHouses = account.favoriteVenuesResource?.map { it.id } ?: listOf()

        favouriteContentCategories = account.favoriteCategoriesResource?.map { it.id }

        analyticsConsent = when (account.analyticsConsent) {
            true -> AnalyticsConsent.ACCEPTED
            false -> AnalyticsConsent.REJECTED
            else -> AnalyticsConsent.NONE
        }

        MarketingCloudSdk.requestSdk {
            it.registrationManager.edit().run {
                setContactKey(accountId)
                commit()
            }
        }

        FirebaseCrashlytics.getInstance().setUserId(accountId)

        if (account.membership?.status?.isNotEmpty() == true) {
            saveUserMembershipInfo(account)
        }
        //TODO Aaron. removed featureFlags will need investigation
        // find the history of this feature.
        // this might have been added to housepay branch could be merged quicker
        //        featureFlags.guestRegistration = account.features.toList()
        //            .any { it.id == FeatureFlags.Ids.FEATURE_ID_GUEST_REGISTRATION }

    }

    fun saveProfile(profile: Profile?) {
        if (profile == null) return

        profileID = profile.id
        profileFirstName = profile.firstName
        profileLastName = profile.lastName
        profileImageURL = profile.imageUrl
        isStaff = profile.isStaff
        profileOccupation = profile.occupation ?: ""
        profileLocation = profile.city ?: ""
        connectRecommendationOptIn = profile.connectRecommendationOptIn ?: ""

        confirmProfile = profile.confirmedAt == null || profile.confirmedAt!!.before(
            LocalDateTime.now().minusYears(1).toDate()
        )
    }

    private fun saveUserMembershipInfo(account: Account) {
        membershipType = account.membership?.membershipType ?: MembershipType.NONE.name

        membershipStatus = try {
            MembershipStatus.valueOf(account.membership?.status ?: "")
        } catch (e: IllegalArgumentException) {
            FirebaseCrashlytics.getInstance()
                .recordException(Throwable("Invalid membership status: ${account.membership?.status}"))
            MembershipStatus.NONE
        }

        isStaff = account.isStaff

        subscriptionType = try {
            SubscriptionType.valueOf(account.membership?.subscriptionType ?: "")
        } catch (e: IllegalArgumentException) {
            FirebaseCrashlytics.getInstance()
                .recordException(Throwable("Invalid subscription type: ${account.membership?.subscriptionType}"))
            SubscriptionType.NONE
        }

        // we're showing onboarding screens again if isInducted flag is cleared at salesforce
        val inducted = account.membership?.inductedAt != null
        isInducted = when (subscriptionType) {
            SubscriptionType.NONE,
            SubscriptionType.FRIENDS,
            SubscriptionType.CWH -> {
                true
            }
            else -> {
                inducted
            }
        }
        isAppOnboardingComplete = inducted
        hasSeenOnboardingWelcomeScreen = inducted

        gymMembership = when {
            account.membership?.isActive == true -> GymMembership.ACTIVE
            account.membership?.isActivePlus == true -> GymMembership.ACTIVE_PLUS
            else -> GymMembership.NONE
        }
    }

    fun saveAvailabilityStatus(userAvailableStatus: UserAvailableStatus) {
        prefsManager.availableStatus = userAvailableStatus.availableStatus.value
        _availableStatusFlow.value = userAvailableStatus
        setIsCheckedIn(true)
    }

    fun setIsCheckedIn(isCheckedIn: Boolean) {
        _isCheckedIn.value = isCheckedIn
    }

    fun hasMembership(eventType: EventType): Boolean {
        return when (eventType) {
            EventType.FITNESS_EVENT -> gymMembership.hasMembership()
            else -> true
        }
    }

    fun clearData() {
        Timber.d("clearData")
        localHouseIdCached = null
        prefsManager.clearData()
    }

    fun getMiniProfileForSB() = MiniProfile(
        profileID,
        profileImageURL,
        profileFirstName,
        isStaff
    )
}