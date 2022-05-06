package com.sohohouse.seven.common.views.inappnotification

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.databinding.InAppNotificationLayoutBinding

@Deprecated("Use InAppNotification")
interface InAppNotificationListener {
    fun onNotificationDataReady(context: Context, item: InAppNotificationAdapterItem) {}
    fun onDialogPrimaryButtonClicked() // Dialog will handle dismiss
    fun onDialogSecondaryButtonClicked() // for both of these.
}

class InAppNotificationDialog constructor(
    context: Context, val item: InAppNotificationAdapterItem,
    private val inAppNotificationListener: InAppNotificationListener? = null,
    private val primaryClickListener: (() -> Unit)? = null,
    private val secondaryClickListener: (() -> Unit)? = null
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = InAppNotificationLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding.bind()
    }

    private fun InAppNotificationLayoutBinding.bind() {

        notificationDialogImage.visibility = View.VISIBLE

        if (item.imageDrawableId != -1) {
            notificationDialogImage.setImageResource(item.imageDrawableId)
        } else {
            notificationDialogImage.setImageFromUrl(item.imageUrl)
        }

        notificationDialogHeadline.visibility = getVisibility(item.status.isNotEmpty())
        notificationDialogDate.visibility =
            getVisibility(!item.isTextBodyVisible, item.eventDate.isNotEmpty())
        notificationDialogTitle.visibility =
            getVisibility(!item.isTextBodyVisible, item.eventTitle.isNotEmpty())
        notificationDialogCaption.visibility =
            getVisibility(!item.isTextBodyVisible, item.eventHouse.isNotEmpty())
        notificationDialogBody.visibility = getVisibility(item.isTextBodyVisible)
        notificationDialogSecondaryButton.visibility =
            getVisibility(item.isSecondaryButtonVisible)

        notificationDialogHeadline.text = item.status
        notificationDialogDate.text = item.eventDate
        notificationDialogTitle.text = item.eventTitle
        notificationDialogCaption.text = item.eventHouse
        notificationDialogBody.text = item.textBody
        notificationDialogPrimaryButton.text = item.primaryButtonString
        notificationDialogSecondaryButton.text = item.secondaryButtonString

        notificationDialogPrimaryButton.clicks {
            item.primaryClicked?.let { onclick -> onclick() }
            inAppNotificationListener?.onDialogPrimaryButtonClicked()
            primaryClickListener?.invoke()
            dismiss()
        }
        notificationDialogSecondaryButton.clicks {
            item.secondaryClicked?.let { onclick -> onclick() }
            inAppNotificationListener?.onDialogSecondaryButtonClicked()
            secondaryClickListener?.invoke()
            dismiss()
        }
    }

    private fun getVisibility(clause0: Boolean, clause1: Boolean = true): Int {
        return if (clause0 && clause1) View.VISIBLE else View.GONE
    }
}