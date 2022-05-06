package com.sohohouse.seven.more.contact.recycler

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.sohohouse.seven.databinding.MoreContactBottomSectionBinding
import com.sohohouse.seven.databinding.ViewContactHeaderBinding
import com.sohohouse.seven.databinding.ViewSpinnerEnquiryBinding
import com.sohohouse.seven.databinding.ViewTextInputEnquiryBinding
import com.sohohouse.seven.more.contact.EnquiryType
import com.sohohouse.seven.more.contact.recycler.EnquiryItemType.*

enum class EnquiryItemType {
    CONTACT_HEADER,
    ENQUIRY_HEADER,
    SPINNER,
    TEXT_INPUT,
    FOOTER
}

interface EnquiryAdapterListener {
    fun canSubmitUpdated(canSubmit: Boolean)
    fun visitFaqClicked()
    fun contactHouseClicked()
    fun newItemAdded()
}

class EnquiryAdapter(
    val dataItems: MutableList<BaseEnquiryItem>,
    var selectedEnquiries: MutableList<EnquiryType>,
    val listener: EnquiryAdapterListener,
    private val isAccountBarred: Boolean
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), EnquiryTextChangedListener,
    EnquirySpinnerSelectedListener {

    private var textData = TextInputEnquiryItemType("")
    var canSubmitFlag = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (values()[viewType]) {
            CONTACT_HEADER -> HeaderContactViewHolder(
                ViewContactHeaderBinding.inflate(inflater, parent, false)
            )
            ENQUIRY_HEADER -> HeaderEnquiryViewHolder(
                inflater.inflate(HEADER_ENQUIRY_LAYOUT, parent, false)
            )
            SPINNER -> SpinnerEnquiryViewHolder(
                ViewSpinnerEnquiryBinding.inflate(inflater, parent, false)
            )
            TEXT_INPUT -> TextInputEnquiryViewHolder(
                ViewTextInputEnquiryBinding.inflate(inflater, parent, false)
            )
            FOOTER -> FooterEnquiryViewHolder(
                MoreContactBottomSectionBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return dataItems[position].type.ordinal
    }

    override fun getItemCount(): Int {
        return dataItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (values()[getItemViewType(position)]) {
            CONTACT_HEADER -> {
                val viewHolder = holder as HeaderContactViewHolder
                viewHolder.bind(isAccountBarred)
                viewHolder.clicks {
                    listener.visitFaqClicked()
                }
            }
            ENQUIRY_HEADER -> {
            }
            SPINNER -> {
                val viewHolder = holder as SpinnerEnquiryViewHolder
                val data = dataItems[position] as SpinnerEnquiryItemType
                viewHolder.bind(data, this)
                viewHolder.setIsEnabled(!isAccountBarred)
            }
            TEXT_INPUT -> {
                val viewHolder = holder as TextInputEnquiryViewHolder
                val data = dataItems[position] as TextInputEnquiryItemType
                textData = data
                viewHolder.bind(data, this)
                viewHolder.focusOnContactForm()
            }
            FOOTER -> {
                (holder as? FooterEnquiryViewHolder)?.clicks { listener.contactHouseClicked() }
            }
        }
    }

    override fun textChanges(text: CharSequence) {
        textData.text = text
        if (text.isBlank() && canSubmitFlag) {
            updateCanSubmit(false)
        } else if (text.isNotBlank() && !canSubmitFlag) {
            updateCanSubmit(true)
        }
    }

    fun updateCanSubmit(bool: Boolean) {
        canSubmitFlag = bool
        listener.canSubmitUpdated(bool)
    }

    fun getText(): CharSequence {
        return textData.text
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setText(text: CharSequence) {
        textData.text = text
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun spinnerSelected(
        parentData: SpinnerEnquiryItemType,
        childEnquiryType: EnquiryType,
        selectedIndex: Int
    ) {
        val oldItemCount = dataItems.size
        parentData.selectedIndex = selectedIndex
        if (childEnquiryType.apiFieldIndex < selectedEnquiries.size) {
            //if spinner that was selected is up in the hierarchy of enquiry items,
            //remove the old ones from the selected enquiries and from the adapter data
            dataItems.removeAll {
                val index = (it as? SpinnerEnquiryItemType)?.enquiryType?.apiFieldIndex
                index != null && index >= childEnquiryType.apiFieldIndex
            }
            selectedEnquiries.removeAll { it.apiFieldIndex >= childEnquiryType.apiFieldIndex }
        }
        if (childEnquiryType.childEnqTypes == null) {
            //no child items so it's the final spinner
            insertTextInputItem()
        } else {
            //child items,
            deleteTextInputItem()
            insertEnquirySpinner(childEnquiryType)
        }
        selectedEnquiries.add(childEnquiryType)
        notifyDataSetChanged()
        if (oldItemCount != dataItems.size) {
            listener.newItemAdded()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun resetSelected() {
        dataItems.removeAll { selectedEnquiries.contains((it as? SpinnerEnquiryItemType)?.enquiryType) }
        selectedEnquiries = mutableListOf()
        deleteTextInputItem()
        notifyDataSetChanged()
    }

    private fun insertTextInputItem() {
        if (!dataItems.contains(textData)) {
            dataItems.add(dataItems.lastIndex, textData)
        }
        if (textData.text.isNotBlank()) {
            updateCanSubmit(true)
        }
    }

    private fun deleteTextInputItem() {
        dataItems.remove(textData)
        updateCanSubmit(false)
    }

    private fun insertEnquirySpinner(enquiryType: EnquiryType) {
        val data = SpinnerEnquiryItemType(enquiryType)
        val index = dataItems.lastIndex
        dataItems.add(index, data)
    }
}