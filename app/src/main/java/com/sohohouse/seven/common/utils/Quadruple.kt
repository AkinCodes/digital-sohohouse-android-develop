@file:kotlin.jvm.JvmName("TuplesKt")

package com.sohohouse.seven.common.utils

import java.io.Serializable

/**
 * Represents a quadruple of values
 *
 * There is no meaning attached to values in this class, it can be used for any purpose.
 * Quadruple exhibits value semantics, i.e. two Qaudruple are equal if all four components are equal.
 *
 * @param A type of the first value
 * @param B type of the second value
 * @param C type of the third value
 * @param D type of the fourth value
 * @property first first value.
 * @property second second value.
 * @property third third value.
 * @property fourth fourth value.
 */
data class Quadruple<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
) : Serializable {

    /**
     * Returns tring representation of the [Quadruple] including its [first], [second], [third] and [fourth] values.
     */
    override fun toString(): String = "($first, $second, $third, $fourth)"
}

/**
 * Converts this Quadruple into a list
 */
fun <T> Quadruple<T, T, T, T>.toList(): List<T> = listOf(first, second, third, fourth)