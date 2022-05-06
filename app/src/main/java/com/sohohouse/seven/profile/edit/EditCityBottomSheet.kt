package com.sohohouse.seven.profile.edit

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseBottomSheet
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.bottomsheet.BottomSheetFactory
import com.sohohouse.seven.common.dagger.Injectable

class EditCityBottomSheet : FreeTextWithSuggestionsBottomSheet(), Injectable {

    companion object {
        const val EXTRA_INITIAL_VALUE = "EXTRA_INITIAL_VALUE"
        const val REQ_KEY_PICK_CITY = "REQ_KEY_PICK_CITY"

        class Factory(val initialValue: String?) : BottomSheetFactory {
            override fun create(): BaseBottomSheet {
                return EditCityBottomSheet().apply {
                    arguments = Bundle().apply {
                        putString(EXTRA_INITIAL_VALUE, this@Factory.initialValue)
                    }
                }
            }
        }
    }

    override val initialValue: String? by lazy {
        arguments?.getString(EditOccupationBottomSheet.EXTRA_INITIAL_VALUE)
    }

    override val title: String
        get() = getString(R.string.profile_city_label)
    override val placeholder: String
        get() = getString(R.string.profile_city_placeholder)
    override val viewModel: AutoCompleteViewModel<*>
        get() = ViewModelProvider(this, viewModelFactory).get(EditCityViewModel::class.java)

    override fun onInputConfirmed(currentValue: String?) {
        (context as? Listener?)?.onCityConfirmed(currentValue)
        setFragmentResult(REQ_KEY_PICK_CITY, bundleOf(BundleKeys.CITY to currentValue))
        dismiss()
    }

    interface Listener {
        fun onCityConfirmed(value: String?)
    }
}