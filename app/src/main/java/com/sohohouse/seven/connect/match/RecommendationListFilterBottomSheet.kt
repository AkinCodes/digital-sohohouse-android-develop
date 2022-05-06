package com.sohohouse.seven.connect.match

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.connect.filter.FilterBottomSheetFragment
import com.sohohouse.seven.connect.filter.FilterBottomSheetViewModel
import com.sohohouse.seven.connect.filter.base.FilterType

class RecommendationListFilterBottomSheet : FilterBottomSheetFragment() {

    override val viewModel: FilterBottomSheetViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(
            RecommendationListFilterViewModel::class.java
        )
    }

    companion object {
        fun withFilterTypes(
            types: Array<FilterType>? = null
        ): BottomSheetDialogFragment {
            return RecommendationListFilterBottomSheet().apply {
                arguments = Bundle().apply {
                    putStringArray(BundleKeys.FILTER_TYPES, types?.map { it.name }?.toTypedArray())
                }
            }
        }
    }
}