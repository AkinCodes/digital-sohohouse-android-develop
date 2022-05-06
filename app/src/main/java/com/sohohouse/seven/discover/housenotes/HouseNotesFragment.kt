package com.sohohouse.seven.discover.housenotes

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.Scrollable
import com.sohohouse.seven.common.dagger.Injectable
import com.sohohouse.seven.common.design.adapter.PagedRendererAdapter
import com.sohohouse.seven.common.design.card.XLargeCardRenderer
import com.sohohouse.seven.common.design.list.ListItemRenderer
import com.sohohouse.seven.common.design.textblock.SmallTextBlockRenderer
import com.sohohouse.seven.common.views.ReloadableErrorStateView
import com.sohohouse.seven.databinding.FragmentHouseNotesBinding
import com.sohohouse.seven.discover.housenotes.model.HouseNoteCard
import com.sohohouse.seven.discover.housenotes.model.HouseNoteItem
import com.sohohouse.seven.discover.housenotes.model.HouseNoteListItem
import com.sohohouse.seven.discover.housenotes.model.HouseNoteSmallHeading
import com.sohohouse.seven.housenotes.detail.sitecore.HouseNoteDetailsActivity
import com.sohohouse.seven.main.MainNavigationController


class HouseNotesFragment : BaseMVVMFragment<HouseNotesViewModel>(),
    Loadable.View, Errorable.View, Injectable, Scrollable {

    private val adapter = PagedRendererAdapter<DiffItem>().apply {
        registerRenderers(
            SmallTextBlockRenderer(HouseNoteSmallHeading::class.java),
            XLargeCardRenderer(HouseNoteCard::class.java, ::onHouseNoteClicked),
            ListItemRenderer(HouseNoteListItem::class.java) { item, imageView, position ->
                onHouseNoteClicked(
                    item,
                    position
                )
            }
        )
    }

    override val viewModelClass: Class<HouseNotesViewModel> = HouseNotesViewModel::class.java

    override val viewModel: HouseNotesViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(viewModelClass)
    }

    override val errorStateView: ReloadableErrorStateView
        get() = binding.errorState

    override val swipeRefreshLayout: SwipeRefreshLayout
        get() = binding.swipeRefreshLayout

    override val contentLayoutId: Int
        get() = R.layout.fragment_house_notes

    private val binding by viewBinding(FragmentHouseNotesBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupViewModels()
    }

    override fun onResume() {
        super.onResume()
        viewModel.logView()
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }

    private fun setupViewModels() {
        viewModel.houseNotes.observe(viewLifecycleOwner, { adapter.submitList(it) })
        observeErrorState(viewLifecycleOwner, { viewModel.invalidate() })
        observeLoadingState(viewLifecycleOwner) {
            if (isVisible && isResumed) {
                (requireActivity() as? MainNavigationController)?.setLoadingState(it)
            }
        }
    }

    private fun setupViews() {
        with(binding.recyclerView) {
            addItemDecoration(
                HouseNoteItemPaddingDecoration(
                    resources.getDimensionPixelSize(
                        R.dimen.dp_16
                    ), resources.getDimensionPixelSize(R.dimen.dp_16)
                )
            )
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@HouseNotesFragment.adapter
        }
        binding.swipeRefreshLayout.setOnRefreshListener { viewModel.invalidate() }
    }

    override fun scrollToPosition(position: Int) {
        binding.recyclerView.scrollToPosition(position)
    }

    private fun onHouseNoteClicked(item: HouseNoteItem, position: Int) {
        viewModel.onHouseNoteClicked(item.id, position)
        HouseNoteDetailsActivity.startHouseNoteDetailActivity(requireContext(), item.id)
    }

    companion object {
        internal const val TAG = "discover_house_notes"
    }
}