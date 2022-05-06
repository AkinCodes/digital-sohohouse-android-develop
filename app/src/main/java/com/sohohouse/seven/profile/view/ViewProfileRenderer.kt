package com.sohohouse.seven.profile.view

import android.view.View
import ca.symbilityintersect.rendereradapter.BaseRenderer
import com.sohohouse.seven.R
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.databinding.ItemProfileFieldBinding
import com.sohohouse.seven.databinding.ItemViewProfileHeaderBinding
import com.sohohouse.seven.home.completeyourprofile.*

class ViewProfileHeaderRenderer :
    BaseRenderer<ViewProfileAdapterItem.Header, ViewProfileHeaderViewHolder>(ViewProfileAdapterItem.Header::class.java) {
    override fun bindViewHolder(
        item: ViewProfileAdapterItem.Header,
        holder: ViewProfileHeaderViewHolder
    ) {
        holder.bind(item)
    }

    override fun getLayoutResId() = R.layout.item_view_profile_header
    override fun createViewHolder(view: View): ViewProfileHeaderViewHolder {
        return ViewProfileHeaderViewHolder(ItemViewProfileHeaderBinding.bind(view))
    }
}

class ViewProfileFieldRenderer :
    BaseRenderer<ViewProfileAdapterItem.Field, ViewProfileFieldViewHolder>(ViewProfileAdapterItem.Field::class.java) {
    override fun bindViewHolder(
        item: ViewProfileAdapterItem.Field,
        holder: ViewProfileFieldViewHolder
    ) {
        holder.bind(item)
    }

    override fun getLayoutResId(): Int {
        return R.layout.item_profile_field
    }

    override fun createViewHolder(view: View): ViewProfileFieldViewHolder {
        return ViewProfileFieldViewHolder(ItemProfileFieldBinding.bind(view))
    }
}

class CompleteYourProfileRenderer(val onItemClick: (item: SetUpAppPromptItem) -> Unit) :
    BaseRenderer<BaseAdapterItem.SetUpAppPromptItem.Container, SetUpAppPromptContainerViewHolder>(
        BaseAdapterItem.SetUpAppPromptItem.Container::class.java
    ) {
    override fun bindViewHolder(
        item: BaseAdapterItem.SetUpAppPromptItem.Container,
        holder: SetUpAppPromptContainerViewHolder
    ) {
        val adapter = SetUpAppPromptsCarouselAdapter(
            SET_UP_APP_PROMPT_ITEM_LAYOUT,
            item.dataItems
        ) { clickedItem -> onItemClick(clickedItem) }
        holder.bind(adapter)
    }

    override fun getLayoutResId(): Int {
        return SET_UP_APP_PROMPTS_CONTAINER_LAYOUT
    }

    override fun createViewHolder(view: View): SetUpAppPromptContainerViewHolder {
        return SetUpAppPromptContainerViewHolder(view)
    }
}