package com.sohohouse.seven.connect.filter

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.sohohouse.seven.connect.filter.base.Filter
import com.sohohouse.seven.connect.noticeboard.FilterRenderer
import com.sohohouse.seven.databinding.SelectedFiltersViewBinding
import com.sohohouse.seven.home.houseboard.RendererDiffAdapter

class SelectedFiltersView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0,
) : LinearLayoutCompat(context, attrs, defStyleAttrs) {

    private val binding = SelectedFiltersViewBinding.inflate(
        LayoutInflater.from(context), this
    )
    var onRefineClicked: (() -> Unit)? = null
    val adapter = RendererDiffAdapter()
    var onFilterClicked: ((filter: Filter) -> Unit)? = null

    init {
        with(binding) {
            adapter.apply {
                registerRenderers(FilterRenderer {
                    onFilterClicked?.invoke(it)
                })
            }
            filterButton.setOnClickListener {
                onRefineClicked?.invoke()
            }
            filterRecyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            filterRecyclerView.adapter = adapter
        }
    }

}