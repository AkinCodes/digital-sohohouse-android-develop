package com.sohohouse.seven.common.extensions

import android.widget.NumberPicker
import com.sohohouse.seven.profile.edit.PickerItem

fun <T : PickerItem> NumberPicker.setUp(
    values: List<T>,
    currentValue: T?,
    onValueChange: (item: T?) -> Unit, //null passed if placeholder selected
    placeholder: String? = null
) {
    this.minValue = 0
    this.maxValue = if (placeholder != null) values.size else values.size - 1
    this.displayedValues = ArrayList(values.map { it.value })
        .apply { placeholder?.let { add(0, it) } }
        .toTypedArray()

    val indexOfValue = values.indexOf(currentValue)
    if (indexOfValue == -1) {
        this.value = 0
    } else {
        this.value = if (placeholder == null) indexOfValue else indexOfValue + 1
    }

    this.setOnValueChangedListener { _, _, newVal ->
        if (placeholder != null) {
            if (newVal == 0) {
                onValueChange(null)
            } else {
                onValueChange(values[newVal - 1])
            }
        } else {
            onValueChange(values[newVal])
        }
    }
}