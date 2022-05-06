package com.sohohouse.seven.common.deeplink

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Patterns
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.extensions.isPastEvent
import com.sohohouse.seven.common.navigation.IntentUtils
import com.sohohouse.seven.common.navigation.NavigationScreen
import com.sohohouse.seven.common.navigation.NavigationTrigger
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.common.views.EventStatusHelper
import com.sohohouse.seven.common.views.EventStatusType
import com.sohohouse.seven.common.views.inappnotification.InAppNotificationAdapterItem
import com.sohohouse.seven.book.eventdetails.eventstatus.EventStatusItem
import com.sohohouse.seven.network.core.models.Event
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

interface DeeplinkViewModel : IViewModel {

    val deeplink: LiveEvent<Any?>

    fun redirectDeeplink(url: String): Completable

    fun setDeeplink(uri: Uri?)

    fun flush()

    fun deeplink(resourceId: String?, screen: NavigationScreen?, trigger: NavigationTrigger?)
}

class DeeplinkViewModelImpl @Inject constructor(
    private val deeplinkRepo: DeeplinkRepo,
    private val stringProvider: StringProvider,
    eventStatusHelper: EventStatusHelper,
    zipRequestsUtil: ZipRequestsUtil
) : DeeplinkViewModel,
    EventStatusHelper by eventStatusHelper,
    EventViewModel by EventViewModelImpl(zipRequestsUtil) {

    private val _deeplink = LiveEvent<Any?>()

    override val deeplink: LiveEvent<Any?>
        get() = _deeplink

    /**
     * DeeplinkViewModel
     */
    override fun flush() {
        val uri = deeplinkRepo.get().value
        deeplinkRepo.delete()
        if (uri == null || uri == Uri.EMPTY) {
            _deeplink.postValue(null)
            return
        }

        when (val screen =
            NavigationScreen.from(uri.getQueryParameter(BundleKeys.NAVIGATION_SCREEN))) {
            NavigationScreen.EVENT_DETAIL,
            NavigationScreen.EVENT_STATUS,
            NavigationScreen.EVENT_BOOKING_DETAIL -> {
                val id = uri.getQueryParameter(BundleKeys.ID)
                if (id.isNullOrEmpty()) {
                    deeplink(NavigationScreen.EVENTS)
                } else {
                    deepLinkEvent(
                        id,
                        screen,
                        NavigationTrigger.from(uri.getQueryParameter(BundleKeys.NAVIGATION_TRIGGER))
                    )
                }
            }
            else -> {
                _deeplink.postValue(Intent(Intent.ACTION_VIEW, uri))
            }
        }
    }

    override fun setDeeplink(uri: Uri?) {
        deeplinkRepo.put(uri)
    }

    override fun redirectDeeplink(url: String): Completable {
        return Completable.create {
            setDeeplink(redirectUrl(url))
            it.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    @SuppressLint("CheckResult")
    fun deepLinkEvent(eventId: String, screen: NavigationScreen?, trigger: NavigationTrigger?) {
        getEvent(eventId).observeOn(Schedulers.io())
            .subscribe(/*onSuccess = */ { event -> deeplink(event, screen, trigger) },
                /* onError = */ { deeplink(NavigationScreen.EVENTS) })
    }

    override fun deeplink(
        resourceId: String?,
        screen: NavigationScreen?,
        trigger: NavigationTrigger?
    ) {
        if ((trigger?.isEventTrigger == true || screen?.isEventsScreen == true) && !resourceId.isNullOrEmpty()) {
            deepLinkEvent(resourceId, screen, trigger)
        } else {
            _deeplink.postValue(IntentUtils.from(screen, resourceId, notificationTrigger = trigger))
        }
    }

    /**
     * private methods
     */
    private fun deeplink(
        event: Event,
        navigationScreen: NavigationScreen?,
        trigger: NavigationTrigger?
    ) {
        when {
            event.id.isNullOrEmpty() -> deeplink(NavigationScreen.EVENTS)
            event.isPastEvent() -> showPastEvent()
            trigger == NavigationTrigger.EVENT_OPEN_FOR_BOOKING -> handleOpenBooking(event)
            trigger == NavigationTrigger.WON_EVENT_LOTTERY || trigger == NavigationTrigger.PROMOTED_TO_EVENT_GUESTLIST -> deeplinkEventDetails(
                event
            )
            NavigationScreen.EVENT_DETAIL == navigationScreen -> deeplinkEventDetails(event)
            else -> deeplink(NavigationScreen.EVENTS)
        }
    }

    private fun deeplink(navigationScreen: NavigationScreen) {
        _deeplink.postValue(IntentUtils.from(navigationScreen))
    }

    private fun showPastEvent() {
        val item = InAppNotificationAdapterItem(
            imageDrawableId = R.drawable.icon_link_your_calendar,
            status = stringProvider.getString(R.string.event_unavailable_header),
            textBody = stringProvider.getString(R.string.event_unavailable_supporting),
            primaryButtonString = stringProvider.getString(R.string.event_unavailable_explore_cta),
            secondaryButtonString = stringProvider.getString(R.string.event_unavailable_dismiss_cta),
            isTextBodyVisible = true,
            isSecondaryButtonVisible = true
        )
        _deeplink.postValue(item)
    }

    private fun handleOpenBooking(event: Event) {
        when (val eventStatus = getRestrictedEventStatus(event)) {
            EventStatusType.FULLY_BOOKED,
            EventStatusType.WAITING_LIST -> {
                val venue = event.venue?.get(event.document)
                if (venue == null) {
                    deeplink(NavigationScreen.EVENTS)
                    return
                }
                val intent = IntentUtils.from(
                    screen = NavigationScreen.EVENT_STATUS,
                    navigationResourceId = event.id,
                    imageUrl = event.images?.large
                ) ?: return
                val item = EventStatusItem(
                    event,
                    eventStatus,
                    venue.timeZone,
                    venue.name,
                    venue.venueColors.house
                )
                intent.putExtra(BundleKeys.EVENT_STATUS_ITEM, item)
                _deeplink.postValue(intent)
            }
            else -> {
                deeplinkEventDetails(event)
            }
        }
    }

    private fun deeplinkEventDetails(event: Event) {
        _deeplink.postValue(
            IntentUtils.from(
                screen = NavigationScreen.EVENT_DETAIL,
                navigationResourceId = event.id,
                imageUrl = event.images?.large
            )
        )
    }

    private fun redirectUrl(url: String): Uri {
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.instanceFollowRedirects = false

            when (connection.responseCode) {
                HttpURLConnection.HTTP_OK,
                HttpURLConnection.HTTP_MOVED_PERM,
                HttpURLConnection.HTTP_MOVED_TEMP -> getDeepLink(connection.inputStream)
                else -> Uri.EMPTY
            }
        } catch (e: Exception) {
            Timber.d(e.localizedMessage)
            Uri.EMPTY
        }
    }

    private fun getDeepLink(inputStream: InputStream): Uri {
        val response = BufferedReader(InputStreamReader(inputStream)).use { it.readText() }
        val matcher = Patterns.WEB_URL.matcher(response)
        if (matcher.find()) {
            return Uri.parse(matcher.group()).buildUpon()
                .scheme(DeeplinkBuilder.APPS_SCHEME)
                .clearQuery()
                .build()
        }
        return Uri.EMPTY
    }
}