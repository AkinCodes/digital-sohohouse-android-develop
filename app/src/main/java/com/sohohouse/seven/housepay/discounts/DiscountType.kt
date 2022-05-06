package com.sohohouse.seven.housepay.discounts

import com.sohohouse.seven.R
import com.sohohouse.seven.common.utils.StringProvider

enum class DiscountType(
    private val apiValue: String
) {

    UNDER_27("Under 27"),
    STAFF("CS Staff Discount"),
    CS_DRINK_GOODWILL("CS Goodwill Beverage"),
    CS_FOOD_GOODWILL("CS Goodwill Food"),
    HOUSE_PAY("House Pay"),
    U27_FIFTY_PERCENT("U27 50% Discount"),
    STAFF_DRINK("Staff Discount Bev 50%"),
    STAFF_FOOD("Staff Discount Food 50%"),
    ICARE_STAFF_TWENTY_FIVE("iCare Staff Discount 25%"),
    ICARE_STAFF_FIFTY("iCare Staff Discount 50%"),
    CS_GOODWILL("CS Goodwill"),
    CS_GOODWILL_FOOD("Goodwill Food %"),
    MEMBERS_DRINK("Members Bev %"),
    MEMBERS_FOOD("Members Food %"),
    U27_FOOD("U27 Food"),
    BLACK_CARD_DRINK("Black Card Bev"),
    U27_DRINK("U27 Bev");

    fun localizedString(stringProvider: StringProvider): String {
        return when (this) {
            UNDER_27 -> stringProvider.getString(R.string.discount_u27)
            STAFF -> stringProvider.getString(R.string.discount_staff)
            CS_DRINK_GOODWILL -> stringProvider.getString(R.string.discount_cs_drink_goodwill)
            CS_FOOD_GOODWILL -> stringProvider.getString(R.string.discount_cs_food_goodwill)
            HOUSE_PAY -> stringProvider.getString(R.string.discount_house_pay)
            U27_FIFTY_PERCENT -> stringProvider.getString(R.string.discount_u27_50pc)
            STAFF_DRINK -> stringProvider.getString(R.string.discount_staff_drink)
            STAFF_FOOD -> stringProvider.getString(R.string.discount_staff_food)
            ICARE_STAFF_TWENTY_FIVE -> stringProvider.getString(R.string.discount_icare_staff_25pc)
            ICARE_STAFF_FIFTY -> stringProvider.getString(R.string.discount_icare_staff_50pc)
            CS_GOODWILL -> stringProvider.getString(R.string.discount_cs_goodwill)
            CS_GOODWILL_FOOD -> stringProvider.getString(R.string.discount_cs_goodwill_food)
            MEMBERS_DRINK -> stringProvider.getString(R.string.discount_members_drink)
            MEMBERS_FOOD -> stringProvider.getString(R.string.discount_members_food)
            U27_FOOD -> stringProvider.getString(R.string.discount_u27_food)
            BLACK_CARD_DRINK -> stringProvider.getString(R.string.discount_black_card_drink)
            U27_DRINK -> stringProvider.getString(R.string.discount_u27_drink)
        }
    }


    companion object {
        fun from(apiValue: String): DiscountType? {
            return values().firstOrNull {
                it.apiValue == apiValue
            }
        }
    }

}