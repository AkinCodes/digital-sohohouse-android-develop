package com.sohohouse.seven.perks.details.adapter

import android.content.Context
import com.sohohouse.seven.R

interface PerkItem {
    val id: String
    val title: String?
    val summary: String?
    val promotionCode: String?
    val imageUrl: String?
    val city: String?
    val category: String?
    val onlineOnly: Boolean?

    fun cityTag(context: Context): String? {
        return when {
            onlineOnly == true -> {
                if (category?.isNotEmpty() == true) {
                    context.getString(R.string.perks_online_only_formatter, category)
                } else {
                    context.getString(R.string.perks_online_only)
                }
            }
            city.isNullOrEmpty() && category.isNullOrEmpty() -> ""
            city.isNullOrEmpty() -> category
            category.isNullOrEmpty() -> city
            else -> "$city, $category"
        } ?: ""
    }
}