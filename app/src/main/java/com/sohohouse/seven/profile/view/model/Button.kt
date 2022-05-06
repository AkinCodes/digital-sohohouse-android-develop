package com.sohohouse.seven.profile.view.model

import androidx.annotation.StringRes
import com.sohohouse.seven.R
import com.sohohouse.seven.base.DiffItem

sealed class Button : DiffItem {
    override val key: Any
        get() = this

    @StringRes
    open val buttonTitle: Int = 0
    open val buttonEnabled: Boolean = false
    open val buttonVisible: Boolean = false
}

abstract class PrimaryButton : Button()

abstract class SecondaryButton : Button()

object ConnectButton : PrimaryButton() {
    override val buttonTitle: Int = R.string.connect_request_connect_cta
    override val buttonEnabled: Boolean = true
    override val buttonVisible: Boolean = true
}

object MessageButton : SecondaryButton() {
    override val buttonTitle: Int = R.string.message
    override val buttonEnabled: Boolean = true
    override val buttonVisible: Boolean = true
}

object AcceptButton : PrimaryButton() {
    override val buttonTitle: Int = R.string.connect_request_accept_cta
    override val buttonEnabled: Boolean = true
    override val buttonVisible: Boolean = true
}

object IgnoreButton : SecondaryButton() {
    override val buttonTitle: Int = R.string.connect_request_ignore_cta
    override val buttonEnabled: Boolean = true
    override val buttonVisible: Boolean = true
}

object RequestSentButton : PrimaryButton() {
    override val buttonTitle: Int = R.string.connect_request_requested_cta
    override val buttonEnabled: Boolean = false
    override val buttonVisible: Boolean = true
}

object ViewProfileButton : PrimaryButton() {
    override val buttonTitle: Int = R.string.view_profile_cta
    override val buttonEnabled: Boolean = true
    override val buttonVisible: Boolean = true
}

object EmptyButton : PrimaryButton() {
    override val buttonEnabled: Boolean = false
    override val buttonVisible: Boolean = false
    override val buttonTitle: Int = 0
}

object UnblockButton : PrimaryButton() {
    override val buttonTitle: Int = R.string.connect_unblock_cta
    override val buttonEnabled: Boolean = true
    override val buttonVisible: Boolean = true
}

object EditButton : SecondaryButton() {
    override val buttonTitle: Int = R.string.profile_edit_cta
    override val buttonEnabled: Boolean = true
    override val buttonVisible: Boolean = true
}

object OptionsMenu : Button() {
    override val buttonEnabled: Boolean = true
    override val buttonVisible: Boolean = true
}

object ShareProfileButton : PrimaryButton() {
    override val buttonTitle: Int = R.string.cta_share_profile
    override val buttonEnabled: Boolean = true
    override val buttonVisible: Boolean = true
}