package com.sohohouse.seven.profile.view.model

import androidx.annotation.AttrRes
import androidx.annotation.StringRes
import com.sohohouse.seven.R

sealed class ProfileAction {
    @StringRes
    open val title: Int = 0

    @AttrRes
    open val color: Int = R.attr.colorDialogTextColorPrimary
}

object Remove : ProfileAction() {
    override val title: Int = R.string.connect_remove_connection_cta
}

object Block : ProfileAction() {
    override val title: Int = R.string.connect_block_member_cta
}

object Report : ProfileAction() {
    override val title: Int = R.string.connect_report_member_cta
    override val color: Int = R.attr.colorError
}