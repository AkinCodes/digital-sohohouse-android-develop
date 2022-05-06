package com.sohohouse.seven.connect.filter

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.sohohouse.seven.R
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.dagger.Injectable
import com.sohohouse.seven.common.extensions.getParcelableTypedArray
import com.sohohouse.seven.common.extensions.setInvisible
import com.sohohouse.seven.connect.filter.base.Filter
import com.sohohouse.seven.connect.filter.base.FilterMode
import com.sohohouse.seven.connect.filter.base.FilterType
import com.sohohouse.seven.databinding.FragmentNoticeBoardFilterBinding
import javax.inject.Inject

abstract class FilterBottomSheetFragment : BottomSheetDialogFragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    abstract val viewModel: FilterBottomSheetViewModel

    private val filterTypes: Array<FilterType>
        get() = arguments?.getStringArray(BundleKeys.FILTER_TYPES)
            ?.map { FilterType.valueOf(it) }
            ?.toTypedArray()
            ?: arrayOf(FilterType.HOUSE_FILTER, FilterType.CITY_FILTER, FilterType.TOPIC_FILTER)

    val binding by viewBinding(FragmentNoticeBoardFilterBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notice_board_filter, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener { setupBottomSheet(it as BottomSheetDialog) }
            setOnCancelListener { dismissWithResult(Activity.RESULT_CANCELED) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            setupViews()
            setupViewModel()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onScreenViewed()
    }

    private fun FragmentNoticeBoardFilterBinding.setupViews() {
        val mode =
            if (targetRequestCode == REQUEST_CODE_TAG) FilterMode.TAGGING else FilterMode.FILTERS
        val filters = arguments?.getParcelableTypedArray<Filter>(BundleKeys.FILTERS)
        val adapter = FilterViewPagerAdapter(
            this@FilterBottomSheetFragment,
            mode,
            filterTypes,
            filters
        )

        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 2
        TabLayoutMediator(
            tabLayout,
            viewPager
        ) { tab, pos -> tab.setText(adapter.getTabTitle(pos)) }.attach()

        if (REQUEST_CODE_TAG == targetRequestCode) {
            applyFilter.text = getString(R.string.cta_confirm)
            resetFilter.setInvisible()
        }

        resetFilter.setOnClickListener { resetFilters(adapter) }
        applyFilter.setOnClickListener { applyFilter(adapter) }
        cancelButton.setOnClickListener { dismissWithResult(Activity.RESULT_CANCELED) }
    }

    private fun setupViewModel() {
        viewModel.screenNameEvent.observe(viewLifecycleOwner, Observer { screen ->
            screen?.let { viewModel.setScreenName(name= screen) }
        })
    }

    private fun applyFilter(adapter: FilterViewPagerAdapter) {
        when (targetRequestCode) {
            REQUEST_CODE_FILTERS -> {
                adapter.getFragment(binding.viewPager.currentItem)?.saveFilters()
                viewModel.onApplyFilters()
                dismissWithResult(Activity.RESULT_OK)
            }
            REQUEST_CODE_TAG -> {
                dismissWithResult(Activity.RESULT_OK, Intent().also { intent ->
                    intent.putExtra(
                        BundleKeys.FILTER,
                        adapter.getFragment(binding.viewPager.currentItem)
                            ?.getFilters()
                            ?.firstOrNull()
                    )
                    intent.putExtra(
                        BundleKeys.FILTER_TYPES,
                        filterTypes.map { it.name }.toTypedArray()
                    )
                })
            }
            else -> {
                dismissWithResult(Activity.RESULT_CANCELED)
            }
        }
    }

    private fun resetFilters(adapter: FilterViewPagerAdapter) {
        for (pos in 0 until adapter.itemCount) {
            adapter.getFragment(pos)?.resetFilters()
        }
    }

    private fun dismissWithResult(resultCode: Int, data: Intent? = null) {
        if (resultCode == Activity.RESULT_CANCELED) viewModel.onCancelled()

        targetFragment?.onActivityResult(targetRequestCode, resultCode, data)
        dismiss()
    }

    private fun setupBottomSheet(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet: View = bottomSheetDialog.findViewById(R.id.design_bottom_sheet) ?: return
        bottomSheet.layoutParams =
            bottomSheet.layoutParams.apply { height = WindowManager.LayoutParams.MATCH_PARENT }

        BottomSheetBehavior.from<View?>(bottomSheet).apply {
            this.state = BottomSheetBehavior.STATE_EXPANDED
            this.peekHeight = 0

            this.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        dismissWithResult(Activity.RESULT_CANCELED)
                    }
                }
            })
        }
    }

    companion object {
        const val TAG = "noticeboard_filter"

        const val REQUEST_CODE_FILTERS = 20001
        const val REQUEST_CODE_TAG = 20002
    }
}