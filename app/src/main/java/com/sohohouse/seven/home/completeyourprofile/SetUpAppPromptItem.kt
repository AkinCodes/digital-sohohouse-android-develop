package com.sohohouse.seven.home.completeyourprofile

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sohohouse.seven.base.DiffItem

data class SetUpAppPromptItem constructor(
    val image: String? = null,
    @DrawableRes val placeholder: Int,
    @StringRes val title: Int,
    @StringRes val subtitle: Int,
    val prompt: Prompt
) : DiffItem {
    override val key: Any?
        get() = title

    enum class Prompt {
        COMPLETE_PROFILE,
        CUSTOMISE_NOTIFICATIONS
    }
}