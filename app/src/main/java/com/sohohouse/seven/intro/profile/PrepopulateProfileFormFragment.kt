package com.sohohouse.seven.intro.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseFragment
import com.sohohouse.seven.base.GenericAdapter
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.stringProvider
import com.sohohouse.seven.common.extensions.withResultListener
import com.sohohouse.seven.common.form.FormAdapter
import com.sohohouse.seven.common.form.FormItemDecoration
import com.sohohouse.seven.common.form.FormRowType
import com.sohohouse.seven.common.utils.StringProviderImpl
import com.sohohouse.seven.databinding.FragmentPrepopulateProfileFormBinding
import com.sohohouse.seven.databinding.ItemPrepopulateProfileFieldBinding
import com.sohohouse.seven.profile.ProfileField
import com.sohohouse.seven.profile.edit.EditCityBottomSheet
import com.sohohouse.seven.profile.edit.EditCityBottomSheet.Companion.REQ_KEY_PICK_CITY
import com.sohohouse.seven.profile.edit.EditOccupationBottomSheet
import com.sohohouse.seven.profile.edit.EditOccupationBottomSheet.Companion.REQ_KEY_PICK_OCCUPATION
import com.sohohouse.seven.profile.edit.SpinnerPickerBottomSheet
import timber.log.Timber

class PrepopulateProfileFormFragment : BaseFragment() {

    private val viewModel: PrepopulateProfileViewModel by activityViewModels()

    override val contentLayoutId get() = R.layout.fragment_prepopulate_profile_form

    private val adapter = PrepopulateProfileFieldsAdapter(::onFieldClick)

    private lateinit var binding: FragmentPrepopulateProfileFormBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPrepopulateProfileFormBinding.bind(view)
        with(binding.formRv) {
            adapter = this@PrepopulateProfileFormFragment.adapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                FormItemDecoration(
                    requireContext(),
                    this@PrepopulateProfileFormFragment.adapter
                )
            )
        }
        viewModel.fields.observe(viewLifecycleOwner) {
            adapter.items = it
        }
        viewModel.itemChangeEvent.observe(viewLifecycleOwner) {
            adapter.notifyItemChanged(it)
        }
    }

    private fun onFieldClick(profileField: ProfileField<*>) {
        if (viewModel.loadingState.value == LoadingState.Loading) return
        when (profileField) {
            is ProfileField.Industry -> {
                SpinnerPickerBottomSheet.Builder(
                    header = getString(R.string.profile_industry_label),
                    initialValue = profileField.getEditDisplayValue(requireContext().stringProvider),
                    placeholder = if (profileField.hasValue) null else profileField.placeholder?.takeIf { it > 0 }
                        ?.let { getString(it) },
                    options = viewModel.industryOptions,
                    listener = { viewModel.onIndustryPick(it) })
                    .build()
                    .show(parentFragmentManager, SpinnerPickerBottomSheet::class.java.simpleName)
            }
            is ProfileField.Occupation -> {
                EditOccupationBottomSheet.Companion.Factory(
                    profileField.getEditDisplayValue(
                        requireContext().stringProvider
                    )
                )
                    .create()
                    .withResultListener(REQ_KEY_PICK_OCCUPATION) { _, bundle ->
                        viewModel.onOccupationPick(bundle.getString(BundleKeys.OCCUPATION))
                    }
                    .show(parentFragmentManager, EditOccupationBottomSheet::class.java.simpleName)
            }
            is ProfileField.City -> {
                EditCityBottomSheet.Companion.Factory(
                    profileField.getEditDisplayValue(
                        requireContext().stringProvider
                    )
                )
                    .create()
                    .withResultListener(REQ_KEY_PICK_CITY) { _, bundle ->
                        viewModel.onCityPick(bundle.getString(BundleKeys.CITY))
                    }
                    .show(parentFragmentManager, EditCityBottomSheet::class.java.simpleName)
            }
            else -> Timber.i("Unknown profileField[$profileField]")
        }
    }
}

class PrepopulateProfileFieldsAdapter(private val onFieldClick: (field: ProfileField<*>) -> Unit) :
    GenericAdapter<ProfileField<*>>(), FormAdapter {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<ProfileField<*>> {
        return PrepopulateProfileFieldItemViewHolder(
            ItemPrepopulateProfileFieldBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onFieldClick
        )
    }

    override fun getFormRowType(adapterPosition: Int): FormRowType {
        return FormRowType.rowTypeFor(itemCount, adapterPosition)
    }
}

class PrepopulateProfileFieldItemViewHolder(
    private val binding: ItemPrepopulateProfileFieldBinding,
    private val onClick: (field: ProfileField<*>) -> Unit
) : GenericAdapter.ViewHolder<ProfileField<*>>(binding.root) {
    override fun bind(item: ProfileField<*>) {
        with(binding) {
            root.clicks { onClick(item) }
            label.text = item.getLabel(StringProviderImpl(root.resources))
            value.text = item.getEditDisplayValue(root.context.stringProvider)
        }
    }
}