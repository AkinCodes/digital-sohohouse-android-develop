package com.sohohouse.seven.housepay.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.utils.CurrencyUtils
import com.sohohouse.seven.network.core.models.housepay.Check
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class HousePayReceiptsAdapter(
    private val itemClickListener: (id: String) -> Unit
) : BaseExpandableListAdapter() {

    companion object {
        private const val DATE_FORMAT_PATTERN = "EEE, dd MMMM yyyy - HH:mm"
    }

    private var items: Map<CharSequence, List<Check>> = emptyMap()

    fun setItemsList(items: HashMap<CharSequence, List<Check>>) {
        this.items = items
        notifyDataSetChanged()
    }

    private fun getGroupChildList(listPosition: Int): List<Check> =
        items[getGroup(listPosition)] ?: emptyList()

    override fun getGroupCount(): Int = items.keys.size

    override fun getChildrenCount(listPosition: Int): Int = getGroupChildList(listPosition).size

    override fun getGroup(listPosition: Int): CharSequence = items.keys.toList()[listPosition]

    override fun getChild(
        listPosition: Int,
        expandedListPosition: Int
    ): Check = getGroupChildList(listPosition)[expandedListPosition]

    override fun getGroupId(listPosition: Int): Long = listPosition.toLong()

    override fun getChildId(
        listPosition: Int,
        expandedListPosition: Int
    ): Long = expandedListPosition.toLong()

    override fun hasStableIds(): Boolean = false

    override fun isChildSelectable(
        listPosition: Int,
        expandedListPosition: Int
    ): Boolean = true

    @SuppressLint("InflateParams")
    override fun getGroupView(
        listPosition: Int,
        isExpanded: Boolean,
        view: View?,
        parent: ViewGroup
    ): View? {
        var convertView = view
        if (convertView == null) {
            val layoutInflater = parent.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.item_house_pay_receipt_header, null)
        }
        val listTitle = getGroup(listPosition) as String
        val listTitleTextView = convertView?.findViewById<TextView>(R.id.receipts_title_date_text)
        listTitleTextView?.text = listTitle
        return convertView
    }

    @SuppressLint("InflateParams")
    override fun getChildView(
        listPosition: Int,
        expandedListPosition: Int,
        isLastChild: Boolean,
        view: View?,
        parent: ViewGroup
    ): View? {
        var convertView = view
        if (convertView == null) {
            val layoutInflater = parent.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.item_house_pay_receipt_details, null)
        }
        val check = getChild(listPosition, expandedListPosition)
        val receiptDateText = convertView?.findViewById<TextView>(R.id.receipt_date_text)
        val receiptDetailsText = convertView?.findViewById<TextView>(R.id.receipt_details_text)
        val receiptAmountText = convertView?.findViewById<TextView>(R.id.receipt_amount_text)

        receiptDateText?.text = SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.UK)
            .format(check.paidAt!!)
        receiptDetailsText?.text = check.locationName
        receiptAmountText?.text = CurrencyUtils.getFormattedPrice(
            check.totalCents,
            check.currency,
            showCurrency = true
        )
        convertView?.setOnClickListener {
            itemClickListener.invoke(check.id)
        }
        return convertView
    }
}