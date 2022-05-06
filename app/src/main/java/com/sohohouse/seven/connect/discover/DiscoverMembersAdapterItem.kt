package com.sohohouse.seven.connect.discover

import com.sohohouse.seven.base.DiffItem

class GetDiscoverMembersAdapterItem(
    private val invoker: () -> DiscoverMembersAdapterItem
) : () -> DiscoverMembersAdapterItem by invoker, DiffItem {
    override val key: Any get() = invoke().key
}

enum class DiscoverMembersAdapterItem : DiffItem {

    ShowOptInPrompt,
    ShowCompleteProfilePrompt,
    ShowSuggestedPeople;

    override val key: Any get() = this::class.java

}
