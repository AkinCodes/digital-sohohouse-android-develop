package com.sohohouse.seven.more.payment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MenuItem
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import ca.symbilityintersect.autoformat.formatters.CreditCardAutoFormatter
import ca.symbilityintersect.autoformat.formatters.CreditCardExpirationAutoFormatter
import ca.symbilityintersect.forms.Form
import ca.symbilityintersect.forms.StringProvider
import ca.symbilityintersect.forms.SubForm
import ca.symbilityintersect.forms.adapter.FormRendererAdapter
import ca.symbilityintersect.forms.item.text.TextValidatedItem
import ca.symbilityintersect.forms.renderers.TextValidatedItemRenderer
import ca.symbilityintersect.ui_components.textinputfield.OnDoneListener
import com.sohohouse.seven.App
import com.sohohouse.seven.BuildConfig
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseViewControllerActivity
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.isStringEmpty
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.common.forms.TextItem
import com.sohohouse.seven.common.forms.TextItemRenderer
import com.sohohouse.seven.common.utils.DateUtils
import com.sohohouse.seven.common.utils.LuhnValidationUtils
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.ReloadableErrorStateView
import com.sohohouse.seven.databinding.ActivityAddPaymentBinding
import com.sohohouse.seven.network.core.models.Card
import com.sohohouse.seven.network.core.models.PaymentFormFields
import com.sohohouse.seven.payment.CardPaymentItem
import com.sohohouse.seven.payment.PaymentCardStatus
import com.sohohouse.seven.payment.PaymentCardType
import com.sohohouse.seven.payment.PaymentFieldType

class AddPaymentActivity : BaseViewControllerActivity<AddPaymentPresenter>(),
    AddPaymentViewController {

    private val binding by viewBinding(ActivityAddPaymentBinding::bind)

    private var formItems: MutableList<Triple<String, TextValidatedItem, Boolean>> = mutableListOf()

    //region BaseViewControllerActivity
    override fun createPresenter(): AddPaymentPresenter {
        return App.appComponent.addPaymentPresenter
    }

    override fun getContentLayout(): Int = R.layout.activity_add_payment

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    //endregion

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (!BuildConfig.DEBUG) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }

        with(binding.componentToolbar) {
            toolbarTitle.text = getString(R.string.payment_methods_new_title)
            toolbarBackBtn.clicks { onBackPressed() }
        }
    }

    //region AddPaymentViewController
    override fun initLayout(id: String, fields: List<PaymentFormFields>) {
        val form = Form()
        val subForm = SubForm()

        subForm.addItem(TextItem(getString(presenter.getPaymentMethodsNewSupportingMsg())))

        for (field in fields) {
            val builder = TextValidatedItem.Builder(field.fieldLabel)
            val inputType = when (PaymentFieldType.valueOf(field.fieldType)) {
                PaymentFieldType.CARD_NUMBER -> InputType.TYPE_CLASS_DATETIME
                PaymentFieldType.DATE -> InputType.TYPE_CLASS_DATETIME
                PaymentFieldType.TEXT -> InputType.TYPE_CLASS_TEXT
                PaymentFieldType.NUMBER -> InputType.TYPE_CLASS_NUMBER
            }
            var isExpiryDate = false
            builder.inputType(inputType)

            if (PaymentFieldType.valueOf(field.fieldType) == PaymentFieldType.DATE) {
                builder.autoFormatter(CreditCardExpirationAutoFormatter(), true)
                    .addValidator({ value -> !value.isStringEmpty() },
                        { R.string.field_credit_date_empty_error })
                    .addValidator({ value -> value.length == 4 },
                        { R.string.field_credit_date_length_error })
                    .addValidator({ value ->
                        DateUtils.isExpiryDateStringValid(value)
                    },
                        { R.string.field_credit_date_invalid_error })
                isExpiryDate = true
            }

            if (PaymentFieldType.valueOf(field.fieldType) == PaymentFieldType.CARD_NUMBER) {
                builder.autoFormatter(CreditCardAutoFormatter(), true)
                    .addValidator({ value -> !value.isStringEmpty() },
                        { R.string.field_credit_number_empty_error })
                    .addValidator({ value -> value.length == 15 || value.length == 16 },
                        { R.string.field_credit_number_length_error })
                    .addValidator({ value -> LuhnValidationUtils.isValid(value) },
                        { R.string.field_credit_number_invalid_error })
            }

            if (PaymentFieldType.valueOf(field.fieldType) == PaymentFieldType.NUMBER && field.fieldId == FIELD_ID_CCV) {
                builder.addValidator({ value -> !value.isStringEmpty() },
                    { R.string.empty_field_validation })
                    .addValidator({ value -> value.length == 3 || value.length == 4 },
                        { R.string.payment_methods_new_cvv_error })
            }

            if (fields.indexOf(field) == fields.size - 1) {
                builder.isLastItem(true)
            }

            val formField = builder.build()
            formItems.add(Triple(field.fieldId, formField, isExpiryDate))
            subForm.addItem(formField)
        }

        form.addSubForm(subForm)
        form.setValidationChangedListener { binding.addButton.isEnabled = it }

        val formRendererAdapter = FormRendererAdapter(this, form)
        formRendererAdapter.registerRenderers(
            TextValidatedItemRenderer(R.layout.payment_text_input_item,
                R.id.text_input_item,
                StringProvider { getString(it) },
                OnDoneListener {
                    formRendererAdapter.onDone()
                    if (form.isValid) {
                        presenter.onAddButtonClicked(id, formItems.map {
                            Pair(
                                it.first,
                                if (it.third) presenter.flipExpiryDate(it.second.value) else it.second.value
                            )
                        })
                    }
                }),
            TextItemRenderer(R.layout.list_payment_description_item, R.id.text)
        )

        with(binding.paymentRecyclerView) {
            layoutManager = LinearLayoutManager(this@AddPaymentActivity)
            adapter = formRendererAdapter
        }

        binding.addButton.clicks {
            presenter.onAddButtonClicked(id, formItems.map {
                Pair(
                    it.first,
                    if (it.third) presenter.flipExpiryDate(it.second.value) else it.second.value
                )
            })
        }
    }

    override fun onDestroy() {
        for (item in formItems) {
            item.second.value = null
        }
        super.onDestroy()
    }

    override fun addCardSuccess(card: Card) {
        val intent = Intent()
        intent.putExtra(
            CARD, CardPaymentItem(
                card.id, PaymentCardType.valueOf(card.cardType),
                card.lastFour, card.isPrimary, PaymentCardStatus.valueOf(card.status)
            )
        )
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
    //endregion

    override val loadingView: LoadingView
        get() = binding.activityAddPaymentLoadingView
    //endregion

    //region error
    override fun getErrorStateView(): ReloadableErrorStateView = binding.errorState

    override fun showReloadableErrorState() {
        super.showReloadableErrorState()
        binding.addButton.setGone()
    }

    override fun hideReloadableErrorState() {
        super.hideReloadableErrorState()
        binding.addButton.setVisible()
    }

    //endregion

    companion object {
        const val CARD = "card"
        const val FIELD_ID_CCV = "CardCvv2"
    }
}
