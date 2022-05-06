package com.sohohouse.seven.profile.edit.socialmedia

import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.common.design.adapter.RendererDiffAdapter
import com.sohohouse.seven.common.form.FormAdapter
import com.sohohouse.seven.common.form.FormRowType
import com.sohohouse.seven.common.renderers.SimpleBindingViewHolder
import com.sohohouse.seven.common.renderers.SimpleViewHolder
import com.sohohouse.seven.databinding.ItemSwitchWithLabelBinding
import com.sohohouse.seven.profile.edit.EditConnectedAccountView

class EditSocialMediaAdapter : RendererDiffAdapter<DiffItem>(), FormAdapter {

    init {
        registerRenderers(
            SocialMediaVisibilityToggleItemRenderer(),
            SocialMediaItemRenderer()
        )
    }

    override fun getFormRowType(adapterPosition: Int): FormRowType {
        if (adapterPosition == itemCount - 1) return FormRowType.NONE
        return FormRowType.rowTypeFor(
            formItemCount = itemCount - 1,    //exclude last item (toggle)
            itemIndex = adapterPosition
        )
    }

}

class SocialMediaItemRenderer : Renderer<SocialMediaAdapterItem, SimpleViewHolder> {
    override val type: Class<SocialMediaAdapterItem>
        get() = SocialMediaAdapterItem::class.java

    override fun createViewHolder(parent: ViewGroup): SimpleViewHolder {
        return SimpleViewHolder(createItemView(parent, R.layout.item_edit_social_media_account))
    }

    override fun bindViewHolder(holder: SimpleViewHolder, item: SocialMediaAdapterItem) {
        with(holder.itemView as EditConnectedAccountView) {
            this.setTextChangeListener { item.edit(it) }
            this.setLabel(item.label)
            this.setHint(item.hint)
            this.value = item.value
            this.setErrors(item.errors)
            this.isEnabled = item.enabled
        }

    }
}

class SocialMediaVisibilityToggleItemRenderer :
    Renderer<SwitchWithLabelItem, SimpleBindingViewHolder> {
    override val type: Class<SwitchWithLabelItem>
        get() = SwitchWithLabelItem::class.java

    override fun createViewHolder(parent: ViewGroup): SimpleBindingViewHolder {
        return SimpleBindingViewHolder(
            ItemSwitchWithLabelBinding.bind(
                createItemView(parent, R.layout.item_switch_with_label)
            )
        )
    }

    override fun bindViewHolder(holder: SimpleBindingViewHolder, item: SwitchWithLabelItem) {
        with((holder.binding as? ItemSwitchWithLabelBinding) ?: return) {
            switchLabel.text = item.label
            switchView.isChecked = item.switchedOn
            switchView.setOnCheckedChangeListener { _, isChecked ->
                item.switchedOn = isChecked
                item.onToggleSwitch(isChecked)
            }
        }
    }
}