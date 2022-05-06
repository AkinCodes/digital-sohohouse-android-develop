package com.sohohouse.seven.book.table.guestspeeker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.DialogGuestsPeekerBinding

class GuestsPickerDialog : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "GuestsPickerDialog"
        const val REQUEST_GUESTS_KEY = "REQUEST_GUESTS_KEY"
        const val EXTRA_GUESTS = "EXTRA_GUESTS"
        private const val MAX_GUESTS = 6
        private const val DEFAULT_GUESTS = 2

        fun newInstance(numSeats: Int? = DEFAULT_GUESTS): GuestsPickerDialog {
            return GuestsPickerDialog().apply {
                arguments = bundleOf(EXTRA_GUESTS to numSeats)
            }
        }
    }

    private val maxGuests = MAX_GUESTS
    private var guests = DEFAULT_GUESTS

    private val binding by viewBinding(DialogGuestsPeekerBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_guests_peeker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.guests = (savedInstanceState ?: arguments)?.getInt(EXTRA_GUESTS) ?: DEFAULT_GUESTS
        binding.setupView()
    }

    private fun DialogGuestsPeekerBinding.setupView() {
        btnConfirm.setOnClickListener { onConfirmClick() }
        btnPlus.setOnClickListener { onPlusClick() }
        btnMinus.setOnClickListener { onMinusClick() }
        txtGuestsValue.text = "$guests"
    }

    private fun DialogGuestsPeekerBinding.onPlusClick() {
        if (guests < maxGuests) guests++
        txtGuestsValue.text = "$guests"
    }

    private fun DialogGuestsPeekerBinding.onMinusClick() {
        if (guests > 1) guests--
        txtGuestsValue.text = "$guests"
    }

    private fun onConfirmClick() {
        setFragmentResult(REQUEST_GUESTS_KEY, Bundle().apply { putInt(EXTRA_GUESTS, guests) })
        dismissAllowingStateLoss()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(EXTRA_GUESTS, guests)
    }
}