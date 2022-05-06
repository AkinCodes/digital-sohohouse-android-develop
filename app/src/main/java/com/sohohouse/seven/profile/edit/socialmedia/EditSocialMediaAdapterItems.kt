package com.sohohouse.seven.profile.edit.socialmedia

import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.profile.Error

data class SwitchWithLabelItem(
    var switchedOn: Boolean,
    var label: String,
    val onToggleSwitch: (switchedOn: Boolean) -> Unit
) : DiffItem {
    override val key: Any
        get() = javaClass
}

data class SocialMediaAdapterItem(
    val label: String,
    val hint: String,
    private var _value: String?,
    val errors: Set<Error>,
    private val onEdited: (input: String) -> Unit,
    val enabled: Boolean
) : DiffItem {

    val value: String?
        get() = _value

    fun edit(input: String) {
        this._value = input
        this.onEdited(input)
    }

    override val key: Any
        get() = label
}