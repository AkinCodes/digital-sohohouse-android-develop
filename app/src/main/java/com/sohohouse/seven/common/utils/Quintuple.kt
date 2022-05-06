package com.sohohouse.seven.common.utils

import java.io.Serializable

/**
 * Represents a generic set of 5 values.
 *
 * There is no meaning attached to values in this class, it can be used for any purpose.
 * Quintuple exhibits value semantics, i.e. two Quintuples are equal if all components are equal.
 *
 * @param A type of the first value.
 * @param B type of the second value.
 * @param C type of the third value.
 * @param D type of the fourth value.
 * @param E type of the fifth value.
 * @property first First value.
 * @property second Second value.
 * @constructor Creates a new instance of Quintuple.
 */
data class Quintuple<out A, out B, out C, out D, out E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E

) : Serializable {

    /**
     * Returns string representation of the [Quintuple] including its [first], [second], [third], [fourth], [fifth] values.
     */
    override fun toString(): String = "($first, $second, $third, $fourth, $fifth)"
}

/**
 * Converts this Quintuple into a list.
 */
fun <T> Quintuple<T, T, T, T, T>.toList(): List<T> = listOf(first, second, third, fourth, fifth)