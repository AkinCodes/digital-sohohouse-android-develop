package com.sohohouse.seven.guests.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.StickyHeaderDecoration
import com.sohohouse.seven.databinding.ActivityGuestListIndexBinding
import com.sohohouse.seven.guests.GuestListDetailsActivity
import com.sohohouse.seven.guests.GuestListDetailsMode
import com.sohohouse.seven.guests.NewGuestListActivity

class GuestListIndexActivity : BaseMVVMActivity<GuestListIndexViewModel>(), Loadable.View {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, GuestListIndexActivity::class.java)
        }
    }

    private val binding by viewBinding(ActivityGuestListIndexBinding::bind)


    override val viewModelClass: Class<GuestListIndexViewModel>
        get() = GuestListIndexViewModel::class.java

    private val adapter = GuestListIndexAdapter().apply {
        registerRenderers(
            DescriptionItemRenderer(),
            ListHeaderItemRenderer(),
            GuestInvitationItemRenderer(::onGuestListItemClick)
        )
    }

    override fun getContentLayout() = R.layout.activity_guest_list_index

    override val loadingView: LoadingView
        get() = binding.activityGuestListIndexLoadingView

    override val swipeRefreshLayout: SwipeRefreshLayout?
        get() = binding.swipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.setupViews()
        setupViewModel()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getGuestLists()
    }

    private fun ActivityGuestListIndexBinding.setupViews() {
        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
            setHomeAsUpIndicator(R.drawable.ic_left_arrow)
        }
        toolbarTitle.text = getString(R.string.label_guest_invitations)

        guestInvitationsRv.addItemDecoration(StickyHeaderDecoration(adapter))
        guestInvitationsRv.adapter = adapter

        btnNewInvite.setOnClickListener { onNewInvitationClick() }

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.getGuestLists()
        }
    }

    private fun setupViewModel() {
        viewModel.items.observe(this, Observer {
            adapter.setItems(it)
        })
        observeLoadingState(this)
    }

    private fun onNewInvitationClick() {
        viewModel.logClickNewInvitation()
        startActivity(NewGuestListActivity.getIntent(this))
    }

    private fun onGuestListItemClick(id: String) {
        startActivity(
            GuestListDetailsActivity.getIntent(
                this,
                id,
                GuestListDetailsMode.MODE_EXISTING_GUEST_LIST
            )
        )
    }

}