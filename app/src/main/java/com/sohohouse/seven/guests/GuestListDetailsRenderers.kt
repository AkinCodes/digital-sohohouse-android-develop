package com.sohohouse.seven.guests

import android.annotation.SuppressLint
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.renderers.SimpleBindingRenderer
import com.sohohouse.seven.common.renderers.SimpleBindingViewHolder
import com.sohohouse.seven.common.renderers.SimpleRenderer
import com.sohohouse.seven.databinding.ItemGuestlistInviteRowBinding
import com.sohohouse.seven.databinding.ItemLocationPickerRegionBinding
import com.sohohouse.seven.databinding.ItemNewGuestlistInviteRowBinding
import kotlin.text.isNotEmpty

class GuestListFormItemRenderer :
    SimpleRenderer<GuestListDetailsAdapterItem.GuestListFormItem>(GuestListDetailsAdapterItem.GuestListFormItem::class.java) {
    override fun bindViewHolder(
        item: GuestListDetailsAdapterItem.GuestListFormItem,
        holder: RecyclerView.ViewHolder
    ) {
        with(holder.itemView as GuestListFormView) {
            bindHouseData(item.houseItem)
            bindDateData(item.dateItem)
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.item_guestlist_details_form
    }
}

class GuestItemRenderer(
    private val onEditNameClick: (item: GuestListDetailsAdapterItem.GuestItem) -> Unit,
    private val onShareLinkClick: (item: GuestListDetailsAdapterItem.GuestItem) -> Unit
) : SimpleBindingRenderer<GuestListDetailsAdapterItem.GuestItem>(GuestListDetailsAdapterItem.GuestItem::class.java) {

    @SuppressLint("DefaultLocale")
    override fun bindViewHolder(
        item: GuestListDetailsAdapterItem.GuestItem,
        holder: SimpleBindingViewHolder
    ) {
        with((holder.binding as? ItemGuestlistInviteRowBinding) ?: return) {
            if (item.guestName.isNotEmpty()) {
                circleLetter.text = item.guestName.last().toUpperCase().toString()
            }
            guestNameValue.text = item.guestName.capitalize()
            guestNameValue.clicks { onEditNameClick(item) }
            shareInviteLinkCta.clicks { onShareLinkClick(item) }

            if (item.showStatus) {
                inviteStatusLabel.setVisible()
                shareInviteLinkCta.setGone()
                if (item.status != null) {
                    inviteStatusLabel.setTextColor(inviteStatusLabel.getAttributeColor(item.status.color))
                    inviteStatusLabel.setText(item.status.stringRes)
                    inviteStatusLabel.compoundDrawables.firstOrNull()
                        ?.setTint(inviteStatusLabel.getAttributeColor(item.status.color))
                } else {
                    inviteStatusLabel.text = null
                }
            } else {
                shareInviteLinkCta.setVisible()
                inviteStatusLabel.setGone()
            }
        }
    }

    override fun getLayoutResId() = R.layout.item_guestlist_invite_row

    override fun createViewHolder(view: View): SimpleBindingViewHolder {
        return SimpleBindingViewHolder(ItemGuestlistInviteRowBinding.bind(view))
    }
}

class FormHeaderItemRenderer :
    SimpleBindingRenderer<GuestListDetailsAdapterItem.FormHeaderItem>(GuestListDetailsAdapterItem.FormHeaderItem::class.java) {

    override fun bindViewHolder(
        item: GuestListDetailsAdapterItem.FormHeaderItem,
        holder: SimpleBindingViewHolder
    ) {
        with((holder.binding as? ItemLocationPickerRegionBinding) ?: return) {
            icon.setImageDrawable(ContextCompat.getDrawable(root.context, R.drawable.disclosure))
            icon.rotation = if (item.expanded) 0f else 180f
            buttonText.text = root.context.getString(item.label)
        }
    }

    override fun getLayoutResId() = R.layout.item_location_picker_region    //TODO rename

    override fun createViewHolder(view: View): SimpleBindingViewHolder {
        return SimpleBindingViewHolder(ItemLocationPickerRegionBinding.bind(view))
    }
}

class GuestHeaderItemRenderer :
    SimpleRenderer<GuestListDetailsAdapterItem.GuestsHeaderItem>(GuestListDetailsAdapterItem.GuestsHeaderItem::class.java) {
    override fun bindViewHolder(
        item: GuestListDetailsAdapterItem.GuestsHeaderItem,
        holder: RecyclerView.ViewHolder
    ) {
        with(holder.itemView as TextView) {
            text = context.getString(item.text)
        }
    }

    override fun getLayoutResId() = R.layout.header_03_textview
}

class GuestSubheaderItemRenderer :
    SimpleRenderer<GuestListDetailsAdapterItem.GuestsSubheaderItem>(GuestListDetailsAdapterItem.GuestsSubheaderItem::class.java) {
    override fun bindViewHolder(
        item: GuestListDetailsAdapterItem.GuestsSubheaderItem,
        holder: RecyclerView.ViewHolder
    ) {
        with(holder.itemView as TextView) {
            text = context.getString(item.text)
        }
    }

    override fun getLayoutResId() = R.layout.body_02_textview_light
}