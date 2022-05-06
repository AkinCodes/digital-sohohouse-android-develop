package com.sohohouse.seven.home.adapter.renderers

import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.home.completeyourprofile.SetUpAppPromptContainerViewHolder
import com.sohohouse.seven.home.completeyourprofile.SetUpAppPromptItem

class SetupAppPromptCarouselRenderer(private val onClick: (SetUpAppPromptItem) -> Unit) :
    Renderer<BaseAdapterItem.SetUpAppPromptItem.Container, SetUpAppPromptContainerViewHolder> {

    override val type: Class<BaseAdapterItem.SetUpAppPromptItem.Container> =
        BaseAdapterItem.SetUpAppPromptItem.Container::class.java

    override fun createViewHolder(parent: ViewGroup): SetUpAppPromptContainerViewHolder {
        return SetUpAppPromptContainerViewHolder(
            createItemView(
                parent,
                R.layout.item_set_up_app_prompts_container
            )
        )
    }

    override fun bindViewHolder(
        holder: SetUpAppPromptContainerViewHolder,
        item: BaseAdapterItem.SetUpAppPromptItem.Container
    ) {
        item.bindViewHolder(holder, onClick)
    }

}