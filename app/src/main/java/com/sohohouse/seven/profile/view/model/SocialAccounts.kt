package com.sohohouse.seven.profile.view.model

import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.profile.SocialMediaItem

data class SocialAccounts(val items: List<SocialMediaItem>) : DiffItem {

    override val key: Any
        get() = this

}