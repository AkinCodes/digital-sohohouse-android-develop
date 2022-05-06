package com.sohohouse.seven.guests

import android.annotation.SuppressLint
import android.text.InputType
import android.view.View
import android.widget.EditText
import com.sohohouse.seven.R
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.onTextChanged
import com.sohohouse.seven.common.renderers.SimpleBindingRenderer
import com.sohohouse.seven.common.renderers.SimpleBindingViewHolder
import com.sohohouse.seven.databinding.ItemNewGuestlistInviteRowBinding

data class NewGuestItem(
    var guestIndex: Int,
    var guestName: String?,
    var invitationId: String?
) : DiffItem

class NewGuestItemRenderer(
    private val onShareLinkClick: (item: NewGuestItem) -> Unit
) : SimpleBindingRenderer<NewGuestItem>(NewGuestItem::class.java) {

    @SuppressLint("DefaultLocale")
    override fun bindViewHolder(
        item: NewGuestItem,
        holder: SimpleBindingViewHolder
    ) {
        with((holder.binding as? ItemNewGuestlistInviteRowBinding) ?: return) {

            guestNameValue.setText(item.guestName ?: "")
            disableEditView(item.invitationId != null, guestNameValue)

            guestNameValue.onTextChanged { text, _ ->
                shareInviteLinkCta.isEnabled = !text.isNullOrEmpty()
                item.guestName = text.toString()
            }
            shareInviteLinkCta.clicks {
                disableEditView(true, guestNameValue)
                onShareLinkClick(item)
            }
        }
    }

    private fun disableEditView(disable: Boolean, editText: EditText) {
        editText.isEnabled = !disable
        editText.isFocusable = !disable
        if (disable) {
            editText.inputType = InputType.TYPE_NULL
            editText.alpha = 0.5f
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT
            editText.alpha = 1.0f
        }
    }

    override fun getLayoutResId() = R.layout.item_new_guestlist_invite_row

    override fun createViewHolder(view: View): SimpleBindingViewHolder {
        return SimpleBindingViewHolder(ItemNewGuestlistInviteRowBinding.bind(view))
    }
}