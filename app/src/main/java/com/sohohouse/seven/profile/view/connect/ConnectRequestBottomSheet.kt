package com.sohohouse.seven.profile.view.connect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.fragment.app.setFragmentResultListener
import com.sohohouse.seven.R
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.common.views.showSafe
import com.sohohouse.seven.databinding.FragmentConnectRequestBinding

@Keep
class ConnectRequestBottomSheet : BaseConnectRequestBottomSheet() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener(ComposeMessageBottomSheet.REQUEST_KEY) { _, _ ->
            dismissWithResult(REQUEST_SEND_CONNECTION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_connect_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentConnectRequestBinding.bind(view).setupViews()
        setupViewModel()
    }

    private fun FragmentConnectRequestBinding.setupViews() {
        writeMessage.setOnClickListener { onClickWriteMessage() }
        sendRequest.setOnClickListener { sendRequest() }
    }

    private fun setupViewModel() {
        viewModel.requestSent.observe(
            viewLifecycleOwner,
            { dismissWithResult(REQUEST_SEND_CONNECTION) })
        viewModel.error.observe(viewLifecycleOwner) { showErrorDialog() }
        observeLoadingState(viewLifecycleOwner, { onLoadingStateChanged(it) })
    }

    private fun onClickWriteMessage() {
        viewModel.onClickWriteMessage()
        ComposeMessageBottomSheet.withProfile(profile)
            .showSafe(parentFragmentManager, ComposeMessageBottomSheet.TAG)
    }

    companion object {
        const val TAG = "connection_request_bottom_sheet"

        const val REQUEST_SEND_CONNECTION = "request_send_connection"

        fun withProfile(profile: ProfileItem): ConnectRequestBottomSheet {
            return ConnectRequestBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(BundleKeys.PROFILE, profile)
                }
            }
        }
    }
}