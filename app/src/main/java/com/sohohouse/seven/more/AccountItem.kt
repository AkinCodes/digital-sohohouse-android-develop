package com.sohohouse.seven.more

import androidx.annotation.StringRes
import com.sohohouse.seven.R
import com.sohohouse.seven.base.DiffItem

sealed class AccountItem : DiffItem {
    data class ProfileImageItem(var imageUrl: String) : AccountItem() {
        override val key: Any?
            get() = javaClass
    }

    data class ProfileHeaderTextItem(val text: String) : AccountItem() {
        override val key: Any?
            get() = javaClass
    }

    data class ProfileSubtextItem(val text: String) : AccountItem()

    data class SettingsButtonItem(val menu: AccountMenu) : AccountItem() {
        override val key: Any?
            get() = menu
    }

    object LogoutButtonItem : AccountItem() {
        @StringRes
        val label = R.string.more_logout_cta

        override val key: Any?
            get() = javaClass
    }

    data class AppVersionItem(val text: String) : AccountItem() {
        override val key: Any?
            get() = javaClass
    }
}