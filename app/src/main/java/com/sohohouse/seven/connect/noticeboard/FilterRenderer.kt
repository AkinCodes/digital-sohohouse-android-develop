package com.sohohouse.seven.connect.noticeboard

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ca.symbilityintersect.rendereradapter.BaseRenderer
import com.sohohouse.seven.R
import com.sohohouse.seven.connect.filter.base.Filter
import com.sohohouse.seven.databinding.ViewHolderListFilterItemBinding

class FilterRenderer(private val onItemClick: (Filter) -> Unit) :
    BaseRenderer<Filter, FilterViewHolder>(Filter::class.java) {

    override fun getLayoutResId(): Int = R.layout.view_holder_list_filter_item

    override fun createViewHolder(itemView: View): FilterViewHolder = FilterViewHolder(itemView)

    override fun bindViewHolder(item: Filter, holder: FilterViewHolder) {
        holder.bind(item, onItemClick)
    }
}

class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = ViewHolderListFilterItemBinding.bind(itemView)

    init {
        binding.pillView.apply {
            setActionButton(R.drawable.ic_close)
        }
    }

    fun bind(filter: Filter, onItemClick: (Filter) -> Unit) = binding.apply {
        pillView.label = filter.title
        pillView.setOnClickListener { onItemClick(filter) }
    }

}