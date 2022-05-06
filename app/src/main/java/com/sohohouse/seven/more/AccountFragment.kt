package com.sohohouse.seven.more

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.book.BookTab
import com.sohohouse.seven.common.Scrollable
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.dagger.Injectable
import com.sohohouse.seven.common.views.CustomDialogFactory
import com.sohohouse.seven.common.views.preferences.ProfilePreference
import com.sohohouse.seven.main.MainNavigationController
import com.sohohouse.seven.more.bookings.MyBookingsActivity
import javax.inject.Inject

class AccountFragment : PreferenceFragmentCompat(),
    Injectable,
    Loadable.View,
    Scrollable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val navigationController: MainNavigationController?
        get() = requireActivity() as? MainNavigationController

    override val viewModel: AccountViewModel by lazy {
        ViewModelProvider(requireActivity(), viewModelFactory).get(AccountViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(PROFILE_EDIT_REQUEST) { _, _ -> viewModel.fetchAccount() }

        viewModel.setScreenName(name= AnalyticsManager.Screens.Account.name)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.fragment_account_preference)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListView()

        viewModel.getViewInfo().map { findPreference<Preference>(it.key) }
            .forEach { it?.isVisible = true }

        viewModel.profile.observe(viewLifecycleOwner, {
            findPreference<ProfilePreference>("pref_account_profile")?.bind(
                it,
                ::customPreferenceButtonClick
            )
        })

        observeLoadingState(viewLifecycleOwner) { navigationController?.setLoadingState(it) }

        if (savedInstanceState != null) {
            navigationController?.setLoadingState(LoadingState.Idle)
        }
    }

    private fun setupListView() {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.colorLayer1, typedValue, true)
        listView.setBackgroundColor(typedValue.data)
        listView.isVerticalScrollBarEnabled = false
        listView.isHorizontalScrollBarEnabled = false
    }

    override fun onStart() {
        super.onStart()
        viewModel.onScreenViewed()
    }

    override fun onResume() {
        super.onResume()
        navigationController?.indicateCurrentTab(tag)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if (AccountMenu.LOGOUT.key == preference?.key) {
            showLogoutDialog()
        }
        return viewModel.onPreferenceClick(requireActivity(), preference?.key)
    }

    private fun customPreferenceButtonClick(strKey: String) {
        viewModel.onPreferenceClick(requireActivity(), strKey)
    }

    override fun setDivider(divider: Drawable?) {
        super.setDivider(null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                MyBookingsActivity.MORE_PAST_BOOKINGS_ACTION_CODE -> {
                    if (data?.hasExtra(MyBookingsActivity.MORE_PAST_BOOKINGS_GO_TO_EXPLORE) == true) {
                        (requireActivity() as? MainNavigationController)?.selectExploreTab(BookTab.EVENTS)
                    }
                }
                REQ_CODE_EDIT_PROFILE -> {
                    viewModel.fetchAccount()
                }
            }
        }
    }

    override fun scrollToPosition(position: Int) {
        listView.smoothScrollToPosition(position)
    }

    private fun showLogoutDialog() {
        CustomDialogFactory.createThemedAlertDialog(context = requireContext(),
            message = requireActivity().getString(R.string.more_logout_confirm_label),
            positiveButtonText = requireActivity().getString(R.string.more_logout_confirm_confirm_cta),
            negativeButtonText = requireActivity().getString(R.string.more_logout_confirm_cancel_cta),
            positiveClickListener = { _, _ ->
                viewModel.logout()
                requireActivity().finish()
            })
            .show()
    }

    companion object {
        const val REQ_CODE_EDIT_PROFILE = 111

        const val PROFILE_EDIT_REQUEST = "profile_edit_request"
    }
}