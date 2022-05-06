package com.sohohouse.seven.fcm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sendbird.android.SendBird
import com.sohohouse.seven.R
import com.sohohouse.seven.common.dagger.qualifier.AllNotificationMapper
import com.sohohouse.seven.common.deeplink.DeeplinkBuilder
import com.sohohouse.seven.common.navigation.NavigationScreen
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.VersionUtils
import com.sohohouse.seven.fcm.mappers.NotificationPayloadMapper
import com.sohohouse.seven.splash.SplashActivity
import dagger.android.AndroidInjection
import javax.inject.Inject


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class SohoFCMService : FirebaseMessagingService() {

    @Inject
    lateinit var userManager: UserManager

    @Inject
    @AllNotificationMapper
    lateinit var mapper: NotificationPayloadMapper

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
    }

    override fun onMessageReceived(message: RemoteMessage) {

        if (message.data.isEmpty()) return

        with(mapper.mapToMessageParams(remoteMessage = message)) {
            if (shouldShowNotification(this)) {
                sendNotification(this)
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        registerTokenToSendBird(token)
    }

    private fun registerTokenToSendBird(token: String) {
        SendBird.registerPushTokenForCurrentUser(token) { pushTokenRegistrationStatus, sendBirdException ->
            if (pushTokenRegistrationStatus == SendBird.PushTokenRegistrationStatus.PENDING) {
                sendBroadCast(TYPE_PENDING_REGISTRATION)
            }
        }
    }

    private fun sendBroadCast(eventType: String) {
        val broadcaster = LocalBroadcastManager.getInstance(baseContext)
        val intent = Intent(REQUEST_PUSH_MSG).apply {
            putExtra(KEY_EVENT_TYPE, eventType)
        }
        broadcaster.sendBroadcast(intent)
    }

    private fun sendNotification(params: MessageParams) {
        val intent = Intent(this, SplashActivity::class.java)
        intent.data = DeeplinkBuilder.buildUri(
            NavigationScreen.from(params.screenName),
            params.id,
            params.screenName,
            params.trigger
        )

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, CHANNEL_ID) //
                .setSmallIcon(R.drawable.ic_notification_soho_house)
                .setContentTitle(getNotificationTitle(params.title))
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle().bigText(params.alert))
                .setSound(alarmSound)
                .setContentText(params.alert)
                .setContentIntent(pendingIntent)
                .setPriority(params.priority)

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (VersionUtils.isAtLeastOreo) {
            val name: CharSequence = getString(R.string.notification_channel_name)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            notificationManager.createNotificationChannel(mChannel)
        }

        notificationManager.notify(params.sid, params.notificationId.hashCode(), builder.build())
    }

    private fun getNotificationTitle(title: String): String {
        return if (TextUtils.isEmpty(title)) getString(R.string.app_name) else title
    }

    private fun shouldShowNotification(params: MessageParams): Boolean {
        val hasRequirements = userManager.prefsManager.token.isNotEmpty()
        return if (params.navigationTrigger?.notYetSupported == true)
            false
        else
            hasRequirements
    }

    companion object {
        private const val CHANNEL_ID = "notification_channel"
        const val REQUEST_PUSH_MSG = "REQUEST_PUSH_MSG"
        const val KEY_EVENT_TYPE = "KEY_EVENT_TYPE"
        const val TYPE_PENDING_REGISTRATION = "TYPE_PENDING_REGISTRATION"
    }
}