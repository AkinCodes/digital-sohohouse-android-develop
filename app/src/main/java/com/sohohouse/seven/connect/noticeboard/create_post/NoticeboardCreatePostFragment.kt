package com.sohohouse.seven.connect.noticeboard.create_post

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.lifecycle.Observer
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMBottomSheet
import com.sohohouse.seven.base.mvvm.ErrorDialogViewController
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.extensions.afterTextChanged
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.focusAndShowKeyboard
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.connect.filter.FilterBottomSheetFragment
import com.sohohouse.seven.connect.filter.base.Filter
import com.sohohouse.seven.connect.filter.base.FilterType
import com.sohohouse.seven.connect.noticeboard.NoticeboardFilterBottomSheet
import com.sohohouse.seven.databinding.FragmentCreatePostBinding

class NoticeboardCreatePostFragment : BaseMVVMBottomSheet<NoticeboardCreatePostViewModel>(),
    Loadable.View, ErrorDialogViewController {

    val binding by viewBinding(FragmentCreatePostBinding::bind)

    companion object {
        const val TAG = "NoticeboardCreatePostFragment"
    }

    override val contentLayout: Int
        get() = R.layout.fragment_create_post

    override val viewModelClass: Class<NoticeboardCreatePostViewModel>
        get() = NoticeboardCreatePostViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            observeTextChanges()
            submitPostBtn.clicks { viewModel.onPostSubmit(postInput.text.toString().trim()) }
            cancelBtn.clicks { dismiss() }
            observePostCreatedEvent()
            observeLoadingState()
            remainingChars.text = resources.getQuantityString(
                R.plurals.chars_max_label,
                resources.getInteger(R.integer.noticeboard_post_max_chars),
                resources.getInteger(R.integer.noticeboard_post_max_chars)
            )
            postInput.focusAndShowKeyboard()
            observeErrorDialogEvents()
            setSubmitButtonEnabledState(viewModel.loadingState.value, postInput.text)
            setUpTags()
        }
    }

    private fun FragmentCreatePostBinding.setUpTags() {
        viewModel.tags.observe(viewLifecycleOwner) { tags ->
            tagHouse.tagName = tags[FilterType.HOUSE_FILTER]?.title
            tagCity.tagName = tags[FilterType.CITY_FILTER]?.title
            tagTopic.tagName = tags[FilterType.TOPIC_FILTER]?.title
        }

        tagHouse.setOnClickListener { onClickTag(FilterType.HOUSE_FILTER) }
        tagCity.setOnClickListener { onClickTag(FilterType.CITY_FILTER) }
        tagTopic.setOnClickListener { onClickTag(FilterType.TOPIC_FILTER) }
    }

    private fun observeLoadingState() {
        observeLoadingState(lifecycleOwner) {
            setSubmitButtonEnabledState(it, binding.postInput.text)
        }
    }

    private fun observeTextChanges() {
        binding.postInput.afterTextChanged {
            setSubmitButtonEnabledState(viewModel.loadingState.value, it)
        }
    }

    private fun setSubmitButtonEnabledState(loadingState: LoadingState?, input: Editable?) {
        binding.submitPostBtn.isEnabled =
            loadingState != LoadingState.Loading && input?.isNotBlank() == true
    }

    private fun observePostCreatedEvent() {
        viewModel.postCreatedEvent.observe(lifecycleOwner, Observer {
            targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
            dismiss()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            FilterBottomSheetFragment.REQUEST_CODE_TAG -> onReceiveTag(resultCode, data)
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun onClickTag(type: FilterType) {
        if (parentFragmentManager.findFragmentByTag(FilterBottomSheetFragment.TAG) != null) return

        NoticeboardFilterBottomSheet.withFilterTypes(arrayOf(type), viewModel.tags.value?.get(type))
            .also { dialog ->
                dialog.setTargetFragment(this, FilterBottomSheetFragment.REQUEST_CODE_TAG)
            }.show(parentFragmentManager, FilterBottomSheetFragment.TAG)
    }

    private fun onReceiveTag(resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) return

        val filterType = data?.getStringArrayExtra(BundleKeys.FILTER_TYPES)
            ?.firstOrNull()?.let { FilterType.valueOf(it) } ?: return
        val filter = data.getParcelableExtra<Filter>(BundleKeys.FILTER)
        viewModel.onTagAdded(filterType, filter)
    }

    override val loadingView: LoadingView
        get() = binding.fragmentCreatePostLoadingView
}