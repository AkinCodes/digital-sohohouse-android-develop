package com.sohohouse.seven.more.notifications

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.views.CustomDialogFactory
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.ReloadableErrorStateView
import com.sohohouse.seven.databinding.ActivityMoreNotificationsBinding

class NotificationSettingsFragment : BaseMVVMFragment<MoreNotificationsViewModel>(),
    MoreNotificationsSwitchListener,
    MoreNotificationsButtonListener,
    Loadable.View,
    Errorable.View {

    val binding by viewBinding(ActivityMoreNotificationsBinding::bind)

    override val viewModelClass: Class<MoreNotificationsViewModel>
        get() = MoreNotificationsViewModel::class.java

    override val loadingView: LoadingView
        get() = binding.activityMoreNotificationsLoadingView

    override val errorStateView: ReloadableErrorStateView
        get() = binding.errorState

    override val contentLayoutId: Int
        get() = R.layout.activity_more_notifications

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted { fetchCommunicationPrefs() }
        viewModel.setScreenName(
            requireActivity().localClassName,
            AnalyticsManager.Screens.NotificationSetting.name
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.items.observe(viewLifecycleOwner) {
            binding.recyclerView.layoutManager = LinearLayoutManager(context)
            binding.recyclerView.adapter = MoreNotificationsAdapter(it, this, this)
        }
        observeLoadingState(viewLifecycleOwner)
        observeErrorState(viewLifecycleOwner) { fetchCommunicationPrefs() }
    }

    private fun fetchCommunicationPrefs() {
        val notificationEnabled =
            NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()
        viewModel.fetchCommunicationPrefs(notificationEnabled)
    }

    override fun onResume() {
        super.onResume()
        viewModel.logNotificationOptionsView()
    }

    override fun onStop() {
        super.onStop()
        viewModel.flagNotificationsCustomised()
    }

    private fun launchNotificationSettingsActivity() {
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context?.packageName)
        } else {
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("app_package", context?.packageName)
            intent.putExtra("app_uid", context?.applicationInfo?.uid)
        }
        context?.startActivity(intent)
    }

    private fun showNotificationOffAlert(
        key: String,
        listener: NotificationEventsToggleListener,
        default: Boolean
    ) {
        context?.let { context ->
            CustomDialogFactory.createThemedAlertDialog(
                context,
                context.getString(R.string.turn_off_notifications_android_header),
                context.getString(R.string.turn_off_notifications_android_supporting),
                context.getString(R.string.turn_off_notifications_android_cta),
                context.getString(R.string.turn_off_notifications_android_cancel_cta),
                DialogInterface.OnClickListener { _, _ ->
                    viewModel.turnOffNotification(key)
                    listener.toggle(true)
                }
            ).show()
        }
    }

    override fun onOptionSwitched(
        key: String,
        currentValue: Boolean,
        isPushNotification: Boolean,
        defaultState: Boolean,
        listener: NotificationEventsToggleListener
    ) {
        if (isPushNotification && !NotificationManagerCompat.from(requireContext())
                .areNotificationsEnabled() && !currentValue
        ) {
            viewModel.onNotificationSettingsClicked()
            launchNotificationSettingsActivity()
        } else if (isPushNotification) {
            onPushNotificationClicked(key, currentValue, defaultState, listener)
        } else {
            viewModel.onToggleEmail(key, currentValue)
            listener.toggle(false)
        }
    }

    private fun onPushNotificationClicked(
        key: String,
        currentValue: Boolean,
        defaultState: Boolean,
        listener: NotificationEventsToggleListener
    ) {
        if (viewModel.notificationOffAlertRequired(key, currentValue)) {
            showNotificationOffAlert(key, listener, defaultState)
        } else {
            viewModel.onTogglePushNotification(key, listener, currentValue)
        }
    }

    override fun onAndroidNotificationsSettingsClicked() = viewModel.onNotificationSettingsClicked()

}
