package com.sohohouse.seven.connect.noticeboard

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.connect.filter.FilterBottomSheetFragment
import com.sohohouse.seven.connect.filter.FilterBottomSheetViewModel
import com.sohohouse.seven.connect.filter.base.Filter
import com.sohohouse.seven.connect.filter.base.FilterType

class NoticeboardFilterBottomSheet : FilterBottomSheetFragment() {

    override val viewModel: FilterBottomSheetViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[NoticeboardFilterFragmentViewModel::class.java]
    }

    companion object {
        fun withFilterTypes(
            types: Array<FilterType>? = null,
            filter: Filter? = null
        ): NoticeboardFilterBottomSheet {
            return NoticeboardFilterBottomSheet().apply {
                arguments = Bundle().apply {
                    putStringArray(BundleKeys.FILTER_TYPES, types?.map { it.name }?.toTypedArray())
                    filter?.let { putParcelableArray(BundleKeys.FILTERS, arrayOf(it)) }
                }
            }
        }
    }
}