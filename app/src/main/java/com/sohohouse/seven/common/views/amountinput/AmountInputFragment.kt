package com.sohohouse.seven.common.views.amountinput

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMBottomSheet
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setTextOrHide
import com.sohohouse.seven.common.extensions.stringProvider
import com.sohohouse.seven.common.extensions.withResultListener
import com.sohohouse.seven.databinding.ActivityBlockedMembersBinding
import com.sohohouse.seven.databinding.FragmentAmountInputBinding
import okhttp3.internal.wait
import javax.inject.Inject

class AmountInputFragment : BaseMVVMBottomSheet<AmountInputViewModel>() {

    companion object {
        const val REQ_KEY_INPUT_AMOUNT = "REQ_KEY_INPUT_AMOUNT"

        fun newInstance(
            mode: AmountInputMode,
            listener: (amount: Int) -> Unit
        ): AmountInputFragment {
            return AmountInputFragment().apply {
                arguments = mode.toBundle()
            }.withResultListener(
                REQ_KEY_INPUT_AMOUNT
            ) { _, bundle ->
                listener(bundle.getInt(BundleKeys.AMOUNT_INPUT_CONFIRMED_AMOUNT))
            }
        }
    }

    private val mode: AmountInputMode by lazy {
        AmountInputMode.fromBundle(requireArguments())
    }

    @Inject
    lateinit var assistedFactory: AmountInputViewModel.Factory

    override val contentLayout: Int
        get() = R.layout.fragment_amount_input

    override val viewModelClass: Class<AmountInputViewModel>
        get() = AmountInputViewModel::class.java

    override val viewModel: AmountInputViewModel by lazy {
        assistedFactory.create(mode.evaluator(requireContext().stringProvider))
    }

    override val fixedHeight: Int
        get() = ViewGroup.LayoutParams.WRAP_CONTENT

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentAmountInputBinding.inflate(
            inflater,
            container,
            false
        ).root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAmountInputBinding.bind(view)

        setUpViews(binding)

        viewModel.state.observe(viewLifecycleOwner) {
            with(binding) {
                amountInputConfirm.isEnabled = it.confirmEnabled
                amountInputPrimary.text = it.primary
                amountInputSecondary.text = it.secondary
                amountInputPlus.isEnabled = it.plusEnabled
                amountInputMinus.isEnabled = it.minusEnabled
                amountInputSecondary.setTextOrHide(it.secondary)
                amountInputError.setTextOrHide(it.error)
            }
        }
    }

    private fun setUpViews(binding: FragmentAmountInputBinding) {
        with(binding) {
            amountInputTitle.text = getString(mode.title())
            amountInputMinus.clicks {
                viewModel.onInput(mode.operatorFor(AmountInputKey.MINUS))
            }
            amountInputPlus.clicks { viewModel.onInput(mode.operatorFor(AmountInputKey.PLUS)) }
            amountInputOne.clicks {
                viewModel.onInput(mode.operatorFor(AmountInputKey.ONE))
            }
            amountInputTwo.clicks {
                viewModel.onInput(mode.operatorFor(AmountInputKey.TWO))
            }
            amountInputThree.clicks {
                viewModel.onInput(mode.operatorFor(AmountInputKey.THREE))
            }
            amountInputFour.clicks {
                viewModel.onInput(mode.operatorFor(AmountInputKey.FOUR))
            }
            amountInputFive.clicks {
                viewModel.onInput(mode.operatorFor(AmountInputKey.FIVE))
            }
            amountInputSix.clicks {
                viewModel.onInput(mode.operatorFor(AmountInputKey.SIX))
            }
            amountInputSeven.clicks {
                viewModel.onInput(mode.operatorFor(AmountInputKey.SEVEN))
            }
            amountInputEight.clicks {
                viewModel.onInput(mode.operatorFor(AmountInputKey.EIGHT))
            }
            amountInputNine.clicks {
                viewModel.onInput(mode.operatorFor(AmountInputKey.NINE))
            }
            amountInputZero.clicks {
                viewModel.onInput(mode.operatorFor(AmountInputKey.ZERO))
            }
            amountInputDot.clicks {
                viewModel.onInput(mode.operatorFor(AmountInputKey.DOT))
            }
            amountInputBackspace.clicks {
                viewModel.onInput(mode.operatorFor(AmountInputKey.UNDO))
            }
            amountInputConfirm.clicks {
                setFragmentResult(
                    REQ_KEY_INPUT_AMOUNT, bundleOf(
                        BundleKeys.AMOUNT_INPUT_CONFIRMED_AMOUNT to viewModel.currentAmountCents
                    )
                )
                dismiss()
            }
        }
    }
}
