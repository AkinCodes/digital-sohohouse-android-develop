package com.sohohouse.seven.guests.list

import android.view.View
import ca.symbilityintersect.rendereradapter.BaseRenderer
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.ViewHolderGuestInvitationListItemBinding


/**
 * Renderers
 */
class GuestInvitationItemRenderer(private val listener: (String) -> Unit) :
    BaseRenderer<GuestListItem.GuestInvitationItem, GuestInvitationViewHolder>(GuestListItem.GuestInvitationItem::class.java) {

    override fun bindViewHolder(
        item: GuestListItem.GuestInvitationItem,
        holder: GuestInvitationViewHolder
    ) {
        holder.bind(item)
        holder.itemView.setOnClickListener { listener(item.id) }
    }

    override fun getLayoutResId(): Int = R.layout.view_holder_guest_invitation_list_item

    override fun createViewHolder(itemView: View): GuestInvitationViewHolder {
        return GuestInvitationViewHolder(ViewHolderGuestInvitationListItemBinding.bind(itemView))
    }
}

class DescriptionItemRenderer :
    BaseRenderer<GuestListItem.DescriptionItem, DescriptionViewHolder>(GuestListItem.DescriptionItem::class.java) {
    override fun bindViewHolder(
        item: GuestListItem.DescriptionItem,
        holder: DescriptionViewHolder
    ) {
    }

    override fun getLayoutResId(): Int = R.layout.view_holder_guest_invitations_description

    override fun createViewHolder(itemView: View): DescriptionViewHolder =
        DescriptionViewHolder(itemView)
}

class NewInvitationItemRenderer(private val listener: () -> Unit) :
    BaseRenderer<GuestListItem.NewInvitationItem, NewInvitationViewHolder>(GuestListItem.NewInvitationItem::class.java) {
    override fun bindViewHolder(
        item: GuestListItem.NewInvitationItem,
        holder: NewInvitationViewHolder
    ) {
    }

    override fun getLayoutResId(): Int = R.layout.view_holder_guest_invitations_new_cta

    override fun createViewHolder(itemView: View): NewInvitationViewHolder {
        itemView.setOnClickListener { listener() }
        return NewInvitationViewHolder(itemView)
    }
}

class ListHeaderItemRenderer :
    BaseRenderer<GuestListItem.ListHeaderItem, ListHeaderViewHolder>(GuestListItem.ListHeaderItem::class.java) {
    override fun bindViewHolder(item: GuestListItem.ListHeaderItem, holder: ListHeaderViewHolder) {}

    override fun getLayoutResId(): Int = R.layout.view_holder_guest_invitation_list_header

    override fun createViewHolder(itemView: View): ListHeaderViewHolder =
        ListHeaderViewHolder(itemView)
}