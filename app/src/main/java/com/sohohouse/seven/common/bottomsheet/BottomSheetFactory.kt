package com.sohohouse.seven.common.bottomsheet

import com.sohohouse.seven.base.BaseBottomSheet

interface BottomSheetFactory {
    fun create(): BaseBottomSheet
}