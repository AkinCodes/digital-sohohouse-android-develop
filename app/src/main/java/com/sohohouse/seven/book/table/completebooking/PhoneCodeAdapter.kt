package com.sohohouse.seven.book.table.completebooking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.sohohouse.seven.book.table.PhoneCode
import com.sohohouse.seven.databinding.ItemBookTableConfirmPhoneCodeBinding

class PhoneCodeAdapter : BaseAdapter() {

    private val items = ArrayList<PhoneCode>()

    override fun getItem(position: Int): PhoneCode = items[position]
    override fun getItemId(position: Int): Long = items[position].hashCode().toLong()
    override fun getCount(): Int = items.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ViewHolder
        if (convertView == null) {
            val binding = ItemBookTableConfirmPhoneCodeBinding.inflate(
                LayoutInflater.from(parent?.context), parent, false
            )
            view = binding.root
            vh = ViewHolder(binding)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as ViewHolder
        }
        vh.bind(items[position].name)

        return view
    }

    fun refresh(codes: List<PhoneCode>) {
        items.clear()
        items.addAll(codes)
        notifyDataSetChanged()
    }

    private class ViewHolder(private val binding: ItemBookTableConfirmPhoneCodeBinding) {
        fun bind(country: String) {
            binding.countryName.text = country
        }
    }

}

