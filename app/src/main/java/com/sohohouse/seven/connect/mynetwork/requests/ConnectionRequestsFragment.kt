package com.sohohouse.seven.connect.mynetwork.requests

import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.ErrorDialogFragment
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.design.adapter.PagedRendererAdapter
import com.sohohouse.seven.common.design.list.ListItemPaddingDecoration
import com.sohohouse.seven.common.extensions.setFragmentResult
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.common.views.showSafe
import com.sohohouse.seven.connect.mynetwork.connections.ConnectionsFragment.Companion.MY_CONNECTIONS_CHANGED
import com.sohohouse.seven.databinding.FragmentEmptyStateRecyclerViewBinding
import com.sohohouse.seven.profile.view.ProfileViewerFragment

class ConnectionRequestsFragment : BaseMVVMFragment<ConnectionRequestsViewModel>(), Loadable.View {

    val binding by viewBinding(FragmentEmptyStateRecyclerViewBinding::bind)

    override val contentLayoutId: Int
        get() = R.layout.fragment_empty_state_recycler_view

    override val viewModelClass: Class<ConnectionRequestsViewModel>
        get() = ConnectionRequestsViewModel::class.java

    override val swipeRefreshLayout: SwipeRefreshLayout
        get() = binding.swipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener(CONNECTION_REQUEST_CHANGED) { _, _ -> viewModel.refresh() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PagedRendererAdapter<ConnectionRequestItem>().apply {
            registerRenderer(
                ConnectionRequestRenderer(::onClickProfile, ::onClickAccept, ::onClickIgnore)
            )
        }
        with(binding) {
            setupView(adapter)
        }
        observeViewModel(adapter)
    }

    private fun FragmentEmptyStateRecyclerViewBinding.setupView(adapter: PagedRendererAdapter<ConnectionRequestItem>) {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(
            ListItemPaddingDecoration(
                orientation = RecyclerView.VERTICAL,
                verticalSpacing = resources.getDimensionPixelSize(R.dimen.dp_10),
                horizontalSpacing = resources.getDimensionPixelSize(R.dimen.dp_8)
            )
        )

        swipeRefreshLayout.setOnRefreshListener { viewModel.refresh() }
        emptyStateTitle.setText(R.string.my_connections_no_requests_title)
        emptyStateSubtitle.setText(R.string.my_connections_no_requests_subtitle)
    }

    private fun observeViewModel(adapter: PagedRendererAdapter<ConnectionRequestItem>) {
        viewModel.connectionRequests.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })
        viewModel.connectionsChanged.observe(viewLifecycleOwner, {
            setFragmentResult(MY_CONNECTIONS_CHANGED)
        })
        observeLoadingState(viewLifecycleOwner) {
            showEmptyState(LoadingState.Idle == it && adapter.itemCount == 0)
        }
        viewModel.error.observe(viewLifecycleOwner) {
            ErrorDialogFragment().showSafe(childFragmentManager, ErrorDialogFragment.TAG)
        }
    }

    private fun onClickProfile(item: ConnectionRequestItem) {
        ProfileViewerFragment.withProfile(profile = item.profile, message = item.message)
            .showSafe(parentFragmentManager, ProfileViewerFragment.TAG)
    }

    private fun onClickAccept(item: ConnectionRequestItem) {
        viewModel.acceptRequest(item.id)
    }

    private fun onClickIgnore(item: ConnectionRequestItem) {
        viewModel.ignoreRequest(item.id)
    }

    private fun showEmptyState(isEmpty: Boolean) {
        binding.emptyState.setVisible(isEmpty)
        binding.recyclerView.setVisible(!isEmpty)
    }

    companion object {
        const val CONNECTION_REQUEST_CHANGED = "connection_request_changed"
    }
}