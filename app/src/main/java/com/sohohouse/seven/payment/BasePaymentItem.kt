package com.sohohouse.seven.payment

import com.sohohouse.seven.base.DiffItem
import java.io.Serializable

open class BasePaymentItem(val type: PaymentItemType) : Serializable, DiffItem