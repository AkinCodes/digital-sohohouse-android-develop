package com.sohohouse.seven.profile.edit.pill

import android.view.View
import ca.symbilityintersect.rendereradapter.BaseRenderer
import com.sohohouse.seven.R
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.GenericAdapter
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.renderers.SimpleBindingRenderer
import com.sohohouse.seven.common.renderers.SimpleBindingViewHolder
import com.sohohouse.seven.databinding.ItemInterestsSectionBinding
import com.sohohouse.seven.databinding.ItemPillListItemBinding
import com.sohohouse.seven.profile.edit.interests.EditInterestsViewModel
import timber.log.Timber


class SectionViewHolder(private val binding: ItemInterestsSectionBinding) :
    GenericAdapter.ViewHolder<SectionItem>(binding.root) {
    override fun bind(item: SectionItem) {
        binding.title.text = item.label
    }
}

data class SectionItem(val label: String) : DiffItem {
    override val key: Any?
        get() = label
}

class SectionItemRenderer : BaseRenderer<SectionItem, SectionViewHolder>(SectionItem::class.java) {
    override fun bindViewHolder(item: SectionItem, holder: SectionViewHolder) {
        holder.bind(item)
    }

    override fun getLayoutResId(): Int {
        return R.layout.item_interests_section
    }

    override fun createViewHolder(itemView: View): SectionViewHolder {
        Timber.d("createViewHolder: with View")
        return SectionViewHolder(ItemInterestsSectionBinding.bind(itemView))
    }
}


class InterestItemRenderer : SimpleBindingRenderer<EditInterestsViewModel.InterestItem>(
    EditInterestsViewModel.InterestItem::class.java
) {
    override fun bindViewHolder(
        item: EditInterestsViewModel.InterestItem,
        holder: SimpleBindingViewHolder
    ) {

        (holder.binding as? ItemPillListItemBinding)?.let { binding ->
            with(binding.pillview) {
                label = item.label
                isActivated = item.isSelected
                clicks { item.onClick(item) }
            }
        }
    }

    override fun getLayoutResId() = R.layout.item_pill_list_item

    override fun createViewHolder(view: View): SimpleBindingViewHolder {
        return SimpleBindingViewHolder(ItemPillListItemBinding.bind(view))
    }
}