package com.sohohouse.seven.common.views.amountinput

import com.sohohouse.seven.common.extensions.formatWhole
import com.sohohouse.seven.common.extensions.getPrecision

interface InputOperator {

    fun operate(editingValue: EditingValue): EditingValue

    data class Plus(private val step: Float) : InputOperator {
        override fun operate(editingValue: EditingValue): EditingValue {
            return EditingValue(
                (editingValue.floatValue + step).formatWhole()
            )
        }
    }

    data class Minus(private val step: Float) : InputOperator {
        override fun operate(editingValue: EditingValue): EditingValue {
            val newValue = maxOf(0f, editingValue.floatValue - step)
            return EditingValue(
                newValue.formatWhole()
            )
        }
    }

    object Dot : InputOperator {
        override fun operate(editingValue: EditingValue): EditingValue = when {
            editingValue.isInitialValue -> {
                EditingValue(
                    "0."
                )
            }
            editingValue.stringValue.contains(".") -> {
                editingValue
            }
            else -> {
                EditingValue(
                    editingValue.stringValue.plus(".")
                )
            }
        }
    }

    object Backspace : InputOperator {
        override fun operate(editingValue: EditingValue): EditingValue = when {
            editingValue.isInitialValue -> {
                EditingValue("0")
            }
            editingValue.stringValue == "0" -> {
                editingValue
            }
            editingValue.stringValue.length == 1 -> {
                EditingValue("0")
            }
            else -> EditingValue(
                editingValue.stringValue.dropLast(1)
            )
        }
    }


    class Number(val number: Int) : InputOperator {
        override fun operate(editingValue: EditingValue): EditingValue = when {
            editingValue.isInitialValue || editingValue.stringValue == "0" -> {
                //for initial value or 0, number key resets the input to that number
                EditingValue(number.toString())
            }
            editingValue.stringValue.getPrecision() == 2 -> {
                editingValue    //dont add digits to a value with 2 floating points e.g. 6.75
            }
            else -> {
                EditingValue(
                    editingValue.stringValue.plus(number.toString())
                )
            }
        }
    }

    object None : InputOperator {
        override fun operate(editingValue: EditingValue): EditingValue {
            return editingValue
        }
    }
}

data class EditingValue(
    val stringValue: String,
    val isInitialValue: Boolean = false
) {
    val floatValue: Float
        get() = stringValue.toFloatOrNull() ?: 0f
}