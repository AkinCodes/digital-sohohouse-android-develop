package com.sohohouse.seven.common.views.amountinput

interface InputStateEvaluator {
    fun evaluate(inputOperator: InputOperator): AmountInputState
    val amountCents: Int
}

data class AmountInputState(
    val primary: String,
    val secondary: String?,
    val error: String?,
    val minusEnabled: Boolean,
    val plusEnabled: Boolean,
    val confirmEnabled: Boolean
)

