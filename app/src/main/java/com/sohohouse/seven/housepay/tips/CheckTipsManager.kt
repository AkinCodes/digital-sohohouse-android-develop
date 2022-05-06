package com.sohohouse.seven.housepay.tips

import com.sohohouse.seven.network.core.common.extensions.nullIfEmpty
import com.sohohouse.seven.network.core.models.housepay.Check
import com.sohohouse.seven.network.core.models.housepay.Payment
import java.math.BigDecimal
import java.math.MathContext
import javax.inject.Inject
import kotlin.math.roundToInt

interface CheckTipsManager {

    val tipValueCents: Int

    var tip: Tip

    fun useCheck(check: Check)
    fun removeTipIfAlreadyPaid()
}

class CheckTipsManagerImpl @Inject constructor() : CheckTipsManager {

    private var check: Check? = null
    private var _tip: Tip = Tip.NoTip

    private val shouldRoundTip: Boolean
        get() = check?.locationCode == "SHK"

    private val amountUsedToCalculateTip: Float
        get() = ((check?.subtotal ?: 0) - (check?.amountPaidByOthers ?: 0)).toFloat()

    private val maxCustomTip: Int
        get() = amountUsedToCalculateTip.toInt()

    override var tip: Tip
        get() = _tip
        set(value) {
            _tip = if ((value as? Tip.CustomAmount?)?.amountInCents == 0) {
                Tip.NoTip
            } else {
                value
            }
        }

    override val tipValueCents: Int
        get() = when (val tip = this.tip) {
            is Tip.NoTip -> {
                0
            }
            is Tip.CustomAmount -> {
                minOf(tip.amountInCents, maxCustomTip)
            }
            is Tip.Percentage -> {
                val base = amountUsedToCalculateTip
                roundAmountIfNeeded(base * tip.percentage)
            }
        }

    override fun useCheck(check: Check) {
        this.check = check
        applyDefaultTipIfSet(check)
    }

    override fun removeTipIfAlreadyPaid() {
        val tipsPaid = check?.payments?.filter {
            it.status == Payment.STATUS_PAID
        }?.sumOf {
            (it.tipCents ?: 0)
        }

        if (tipsPaid == tipValueCents) {
            this.tip = Tip.NoTip
        }
    }

    private fun roundAmountIfNeeded(amount: Float): Int {
        if (shouldRoundTip.not()) return amount.roundToInt()
        val decimalAmount = BigDecimal(amount.toDouble()).div(BigDecimal(100))
        return decimalAmount.round(MathContext.UNLIMITED).intValueExact() * 100
    }

    private fun applyDefaultTipIfSet(check: Check) {
        val default: Int = check
            .location
            ?.variableTipsDefault
            ?.let {
                (it.toIntOrNull())?.takeIf { it > 0 }
            }
            ?: return

        val variableTips = check
            .location
            ?.variableTipsValues
            ?.mapNotNull {
                it.toIntOrNull()
            }
            ?.nullIfEmpty()
            ?: return

        if (variableTips.contains(default)) {
            this.tip = Tip.Percentage(default / 100f)
        }
    }
}