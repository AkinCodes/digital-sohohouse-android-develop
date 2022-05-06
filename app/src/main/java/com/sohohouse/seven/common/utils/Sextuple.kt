package com.sohohouse.seven.common.utils

import java.io.Serializable

/**
 * Represents a generic set of 6 values.
 *
 * There is no meaning attached to values in this class, it can be used for any purpose.
 * Sextuple exhibits value semantics, i.e. two Sextuples are equal if all components are equal.
 *
 * @param A type of the first value.
 * @param B type of the second value.
 * @param C type of the third value.
 * @param D type of the fourth value.
 * @param E type of the fifth value.
 * @param F type of the sixth value.
 * @property first First value.
 * @property second Second value.
 * @constructor Creates a new instance of Sextuple.
 */
data class Sextuple<out A, out B, out C, out D, out E, out F>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F
) : Serializable {

    /**
     * @return string representation of the [Sextuple] including its [first], [second], [third], [fourth], [fifth], [sixth] values.
     */
    override fun toString(): String = "($first, $second, $third, $fourth, $fifth, $sixth)"
}

/**
 * Converts this Quintuple into a list.
 */
fun <T> Sextuple<T, T, T, T, T, T>.toList(): List<T> =
    listOf(first, second, third, fourth, fifth, sixth)