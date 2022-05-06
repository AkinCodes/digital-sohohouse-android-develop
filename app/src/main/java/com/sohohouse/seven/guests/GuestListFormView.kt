package com.sohohouse.seven.guests

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.view.forEach
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.common.views.FormView
import com.sohohouse.seven.databinding.FormViewRowContainerBinding
import com.sohohouse.seven.databinding.GuestlistFormLocationContainerBinding
import com.sohohouse.seven.databinding.ItemInviteFormDateFilledBinding
import com.sohohouse.seven.databinding.ItemLocationPickerHouseBinding

class GuestListFormView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : FormView(context, attrs, defStyleAttrs) {

    private val layoutInflater by lazy { LayoutInflater.from(context) }

    private val formBinding: FormViewRowContainerBinding =
        FormViewRowContainerBinding.inflate(layoutInflater, this, false)
    private val guestListBinding: GuestlistFormLocationContainerBinding =
        GuestlistFormLocationContainerBinding.inflate(layoutInflater, this, false)

    init {
        addFormRowContainers()
    }

    private fun addFormRowContainers() {
        addView(guestListBinding.root)
        addView(formBinding.root)
    }

    fun bindHouseData(item: GuestListFormHouseItem?) {
        guestListBinding.inviteFormLocation.isEnabled = item?.enabled ?: true
        guestListBinding.inviteFormLocation.removeAllViews()
        if (item != null) {
            val itemBinding = ItemLocationPickerHouseBinding
                .inflate(layoutInflater, guestListBinding.inviteFormLocation, false).apply {
                    houseIcon.setImageFromUrl(item.iconUrl)
                    houseIcon.contentDescription = item.name
                    houseName.text = item.name
                    houseLocation.text = item.location
                }
            guestListBinding.inviteFormLocation.addView(
                itemBinding.root,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginStart = resources.getDimensionPixelOffset(R.dimen.dp_7)
                    marginEnd = resources.getDimensionPixelOffset(R.dimen.dp_7)
                    gravity = Gravity.CENTER_VERTICAL
                }
            )

            val alpha = if (item.enabled) 1f else 0.5f
            with(itemBinding) {
                houseName.alpha = alpha
                houseIcon.alpha = alpha
                houseLocation.alpha = alpha
            }
        } else {
            guestListBinding.inviteFormLocation.addView(
                layoutInflater.inflate(
                    R.layout.guestlist_form_house_placeholder,
                    guestListBinding.inviteFormLocation,
                    false
                )
            )
        }
    }

    fun bindDateData(item: GuestListFormDateItem?) {
        formBinding.inviteFormDate.removeAllViews()
        if (item != null) {
            val filledDateBinding = ItemInviteFormDateFilledBinding
                .inflate(layoutInflater, formBinding.inviteFormDate, false)
                .apply {
                    dateLabel.setText(item.label)
                    dateValue.text = item.dateString
                }
            formBinding.inviteFormDate.addView(
                filledDateBinding.root,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER_VERTICAL
                }
            )

            val alpha = if (item.enabled) 1f else 0.5f
            filledDateBinding.dateLabel.alpha = alpha
            filledDateBinding.dateValue.alpha = alpha
        } else {
            formBinding.inviteFormDate.addView(
                layoutInflater.inflate(
                    R.layout.guestlist_form_date_placeholder,
                    formBinding.inviteFormDate,
                    false
                )
            )
        }
    }

    fun onLocationClick(onClick: () -> Unit) {
        guestListBinding.inviteFormLocation.clicks { onClick() }
    }

    fun onDateClick(onClick: () -> Unit) {
        formBinding.inviteFormDate.clicks { onClick() }
    }

    fun disableForm() {
        formBinding.inviteFormDate.isClickable = false
        formBinding.inviteFormDate.forEach { v ->
            v.isEnabled = false
            v.alpha = 0.5f
        }


        guestListBinding.inviteFormLocation.isClickable = false
        guestListBinding.inviteFormLocation.forEach { v ->
            v.isEnabled = false
            v.alpha = 0.5f
        }
    }
}

data class GuestListFormHouseItem(
    val id: String,
    val name: String,
    val location: String,
    val iconUrl: String,
    val enabled: Boolean = true
)

data class GuestListFormDateItem(
    @StringRes val label: Int,
    val dateString: String,
    val enabled: Boolean = true
)
