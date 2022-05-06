package com.sohohouse.seven.perks.filter

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.flexbox.FlexboxLayoutManager
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.base.mvvm.ErrorViewStateViewController
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.renderers.SimpleRenderer
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.ReloadableErrorStateView
import com.sohohouse.seven.databinding.FragmentBenefitsFilterCityBinding
import com.sohohouse.seven.databinding.ItemLocationPickerRegionBinding
import com.sohohouse.seven.databinding.ItemMultiSelectHeaderBinding
import com.sohohouse.seven.databinding.ItemPillListItemBinding
import com.sohohouse.seven.home.houseboard.RendererDiffAdapter

@Keep
class BenefitsFilterCityFragment : BaseMVVMFragment<BenefitsFilterCityViewModel>(), Loadable.View,
    ErrorViewStateViewController {

    override val contentLayoutId get() = R.layout.fragment_benefits_filter_city

    val binding by viewBinding(FragmentBenefitsFilterCityBinding::bind)

    override val viewModelClass: Class<BenefitsFilterCityViewModel>
        get() = BenefitsFilterCityViewModel::class.java

    private val adapter = RendererDiffAdapter().apply {
        registerRenderers(
            CityRenderer(),
            HeaderRenderer(),
            RegionRenderer()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLoadingState(lifecycleOwner)
        observeErrorViewEvents()
        with(binding) {
            citiesRv.apply {
                this.adapter = this@BenefitsFilterCityFragment.adapter
                layoutManager = FlexboxLayoutManager(requireContext())
            }
            viewModel.items.observe(viewLifecycleOwner) {
                adapter.setItems(it)
            }
            filterConfirmBtn.clicks {
                viewModel.onConfirmClick()
                (requireActivity() as Listener).onCityFilterConfirmed()
            }
            filterCancelBtn.clicks { (requireActivity() as Listener).onCanceled() }
        }
    }

    interface Listener {
        fun onCityFilterConfirmed()
        fun onCanceled()
    }

    override val loadingView: LoadingView
        get() = binding.fragmentBenefitsFilterCityLoadingView

    override fun getErrorStateView(): ReloadableErrorStateView {
        return binding.errorView
    }
}

class CityRenderer : SimpleRenderer<CityItem>(CityItem::class.java) {

    override fun getLayoutResId() = R.layout.item_pill_list_item

    override fun bindViewHolder(item: CityItem, viewHolder: RecyclerView.ViewHolder) {
        with(ItemPillListItemBinding.bind(viewHolder.itemView).pillview) {
            with(item) {
                label = name
                isActivated = activated
                setActionButton(if (showRemoveBtn) R.drawable.ic_close else 0)
                clicks { onCityClick(item) }
            }
        }
    }
}

class HeaderRenderer : SimpleRenderer<HeaderItem>(HeaderItem::class.java) {

    override fun getLayoutResId() = R.layout.item_multi_select_header

    override fun bindViewHolder(headerItem: HeaderItem, viewHolder: RecyclerView.ViewHolder) {
        with(ItemMultiSelectHeaderBinding.bind(viewHolder.itemView)) {
            with(headerItem) {
                header.setText(primaryText)
                subtext.setText(secondaryText)
                resetBtn.clicks { onResetClick() }
            }
        }
    }
}

class RegionRenderer : SimpleRenderer<RegionItem>(RegionItem::class.java) {

    override fun getLayoutResId() = R.layout.item_location_picker_region

    override fun bindViewHolder(item: RegionItem, holder: RecyclerView.ViewHolder) {
        with(ItemLocationPickerRegionBinding.bind(holder.itemView)) {
            icon.setImageDrawable(
                ContextCompat.getDrawable(
                    root.context,
                    R.drawable.ic_chevron_down
                )
            )
            icon.rotation = if (item.expanded) 180f else 0f
            if (item.selectedCount > 0) {
                buttonText.text = root.context.getString(
                    R.string.name_and_count,
                    root.context.getString(item.name),
                    item.selectedCount.toString()
                )
            } else {
                buttonText.text = root.context.getString(item.name)
            }
            buttonText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            with(root) {
                clicks { item.onRegionClick(item) }
                setPadding(
                    paddingLeft,
                    context.resources.getDimensionPixelOffset(R.dimen.dp_24),
                    paddingRight,
                    paddingBottom
                )
            }
        }
    }

}