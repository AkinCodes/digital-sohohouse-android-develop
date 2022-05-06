package com.sohohouse.seven.common.extensions

import android.content.Context
import com.sohohouse.seven.R

import kotlin.math.pow
import kotlin.math.roundToInt

fun Int?.defaultIfMinus1(defValue: Int?): Int? {
    return if (this == -1) defValue else this
}

fun Int.toWords(context: Context): String {
    if (this == 0) {
        return context.getString(R.string.number_0)
    }

    val numberMap = mapOf(
        Pair(1000, R.string.number_1000),
        Pair(1000000, R.string.number_1000000),
        Pair(1000000000, R.string.number_1000000000)
    )

    val fraction = 1000
    var num = this
    var modNum = num % fraction
    num /= fraction
    var result = numToWordsBelowThousand(context, modNum)

    numberMap.forEach {
        if (num > 0) {
            modNum = num % fraction
            num /= fraction

            val modNumToWord = numToWordsBelowThousand(context, modNum)
            //if num is 0 modStr will be empty
            if (modNumToWord.isNotEmpty()) {
                result = "$modNumToWord ${context.getString(it.value)} $result"
            }
        }
    }
    return result.trim()
}

//helpers
private fun numToWordsBelowThousand(context: Context, numParam: Int): String {
    var num = numParam
    var words = String()
    var fraction = 100

    while (num != 0) {
        val quotient = num / fraction

        if (num >= 100 && fraction == 100 && quotient > 0) {
            words += " ${getStringFromNumMap(context, quotient)} ${
                getStringFromNumMap(
                    context,
                    fraction
                )
            }"
        } else if (num > 20 && fraction == 10 && quotient > 0) {
            // quotient*10 for handling cases like Thirty/Forty
            words += " ${getStringFromNumMap(context, quotient * 10)}"
        } else if (num in 10..20) {
            words += " ${getStringFromNumMap(context, num)}"
            num = 0
        } else if (num in 1..9 && fraction == 1) {
            words += " ${getStringFromNumMap(context, quotient)}"
        }
        // going for 23 after 100 for 123 example
        num %= fraction
        fraction /= 10
    }

    return words.trim()
}

private fun getStringFromNumMap(context: Context, number: Int): String {
    numMap[number]?.let {
        return context.getString(it)
    }
    return number.toString()
}

private val numMap = hashMapOf(
    Pair(1, R.string.number_1),
    Pair(2, R.string.number_2),
    Pair(3, R.string.number_3),
    Pair(4, R.string.number_4),
    Pair(5, R.string.number_5),
    Pair(6, R.string.number_6),
    Pair(7, R.string.number_7),
    Pair(8, R.string.number_8),
    Pair(9, R.string.number_9),
    Pair(10, R.string.number_10),
    Pair(11, R.string.number_11),
    Pair(12, R.string.number_12),
    Pair(13, R.string.number_13),
    Pair(14, R.string.number_14),
    Pair(15, R.string.number_15),
    Pair(16, R.string.number_16),
    Pair(17, R.string.number_17),
    Pair(18, R.string.number_18),
    Pair(19, R.string.number_19),
    Pair(20, R.string.number_20),
    Pair(30, R.string.number_30),
    Pair(40, R.string.number_40),
    Pair(50, R.string.number_50),
    Pair(60, R.string.number_60),
    Pair(70, R.string.number_70),
    Pair(80, R.string.number_80),
    Pair(90, R.string.number_90),
    Pair(100, R.string.number_100)
)
// end helpers

//Formats to whole number string if no decimal places
fun Float.formatWhole(): String {
    return if (this == (this.toInt()).toFloat()) {
        String.format("%d", this.toInt())
    } else {
        String.format("%s", this)
    }
}

fun Float.formatToTwoDecimalPlaces(): String {
    return "%.2f".format(this)
}