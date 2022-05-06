package com.sohohouse.seven.more.contact

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.sohohouse.seven.R

class EnquirySpinnerArrayAdapter(context: Context, resId: Int, val itemList: List<EnquiryType>) :
    ArrayAdapter<EnquiryType>(context, resId, itemList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val item = getItem(position)
        if (item != null && view is TextView) {
            view.text = context.getText(item.displayTextRes)
        }
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val item = getItem(position)
        if (item != null && view is TextView) {
            view.text = context.getText(item.displayTextRes)
        }

        if (position == 0) {
            view?.setPadding(
                context.resources.getDimensionPixelSize(R.dimen.dp_16),
                context.resources.getDimensionPixelSize(R.dimen.dp_20),
                context.resources.getDimensionPixelSize(R.dimen.dp_16),
                context.resources.getDimensionPixelSize(R.dimen.dp_20)
            )
        } else {
            view?.setPadding(
                context.resources.getDimensionPixelSize(R.dimen.dp_16),
                0,
                context.resources.getDimensionPixelSize(R.dimen.dp_16),
                context.resources.getDimensionPixelSize(R.dimen.dp_20)
            )
        }

        return view
    }

    override fun getItem(position: Int): EnquiryType? {
        return if (position <= itemList.lastIndex) {
            super.getItem(position)
        } else {
            //this is a hack to have the last item show up as hint text
            EnquiryType(
                "",
                0,
                R.string.enquiry_your_enquiry_header,
                R.string.enquiry_dropdown_placeholder,
                null
            )
        }
    }
}
