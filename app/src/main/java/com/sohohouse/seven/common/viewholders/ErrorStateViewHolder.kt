package com.sohohouse.seven.common.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.databinding.MorePastBookingsReloadableErrorStateBinding
import com.sohohouse.seven.databinding.ReloadableErrorStateViewholderLayoutBinding

const val ERROR_STATE_LAYOUT = R.layout.reloadable_error_state_viewholder_layout
const val MORE_PAST_BOOKINGS_ERROR_STATE = R.layout.more_past_bookings_reloadable_error_state

interface ErrorStateListener {
    fun onReloadButtonClicked()
}

class ErrorStateOldViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ReloadableErrorStateViewholderLayoutBinding.bind(view)

    fun reloadClicks(onNext: (Any) -> Unit) {
        binding.includedErrorLayout.reloadButton.clicks(onNext)
    }
}

class ErrorStateViewHolder(vBinding: ViewBinding) : RecyclerView.ViewHolder(vBinding.root) {

    private val binding = when (vBinding) {
        is ReloadableErrorStateViewholderLayoutBinding -> vBinding.includedErrorLayout
        is MorePastBookingsReloadableErrorStateBinding -> vBinding.includedErrorLayout
        else -> null
    }

    fun reloadClicks(onNext: (Any) -> Unit) {
        binding?.reloadButton?.clicks(onNext)
    }
}
