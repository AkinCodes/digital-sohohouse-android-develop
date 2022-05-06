package com.sohohouse.seven.perks.details

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.ReloadableErrorStateView
import com.sohohouse.seven.common.views.webview.WebViewBottomSheetFragment
import com.sohohouse.seven.databinding.ActivityPerksDetailsBinding
import com.sohohouse.seven.perks.details.adapter.PerksDetailAdapter

class PerksDetailActivity : BaseMVVMActivity<PerksDetailViewModel>(), Loadable.View,
    Errorable.View {

    override val viewModelClass: Class<PerksDetailViewModel>
        get() = PerksDetailViewModel::class.java

    override fun getContentLayout(): Int = R.layout.activity_perks_details

    val binding by viewBinding(ActivityPerksDetailsBinding::bind)

    override val loadingView: LoadingView
        get() = binding.activityPerksDetailsLoadingView

    override val errorStateView: ReloadableErrorStateView
        get() = binding.errorState

    private val perkId: String?
        get() = intent.getStringExtra(BundleKeys.PERK_ID_KEY) ?: let {
            if (intent.data?.pathSegments?.size == 1) null else intent.data?.lastPathSegment
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(PerksDetailAdapter()) {
            setupViews(this)
            setupViewModel(this)
        }

        perkId?.let { viewModel.fetchData(it) } ?: finish()
        viewModel.setScreenName(name= AnalyticsManager.Screens.PerksDetails.name)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        perkId?.let { viewModel.fetchData(it) } ?: finish()
    }

    private fun setupViews(adapter: PerksDetailAdapter) {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.nav_dark_ar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(this@PerksDetailActivity)
            this.adapter = adapter
        }
    }

    private fun setupViewModel(adapter: PerksDetailAdapter) {
        viewModel.items.observe(lifecycleOwner) { adapter.items = it }
        viewModel.clipboard.observe(lifecycleOwner) { showBottomTab(it) }
        observeErrorState(lifecycleOwner) {
            viewModel.fetchData(perkId ?: return@observeErrorState)
        }
        observeLoadingState(lifecycleOwner)
    }

    private fun onCopyClicked(id: String, name: String, promoCode: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(BundleKeys.PERK_CODE, promoCode)
        clipboard.setPrimaryClip(clip)

        viewModel.logPerksCopyCode(id, name, promoCode)
    }

    private fun onLinkClicked(id: String, name: String, url: String) {
        if (url.isNotBlank()) {
            WebViewBottomSheetFragment.withUrl(url)
                .show(supportFragmentManager, WebViewBottomSheetFragment.TAG)
        }

        viewModel.logPerksVisitSite(id, name, url)
    }

    private fun showBottomTab(item: PerkDetailClipboardItem) {
        with(binding) {
            buttonContainer.run {
                when {
                    item.benefitType.isNotEmpty() -> {
                        if (nextView.id == R.id.show_membership_card) showNext()
                    }
                    item.promoCode.isNotEmpty() -> {
                        if (nextView.id == R.id.copy_code) showNext()
                    }
                    else -> {
                        setGone()
                    }
                }
            }

            copyCode.run {
                setText(R.string.perks_copy_code_cta)
                setOnClickListener {
                    onCopyClicked(item.id, item.name, item.promoCode)
                    setText(R.string.perks_code_copied_label)
                    postDelayed({ setText(R.string.perks_copy_code_cta) }, 2000)
                }
            }

            showMembershipCard.setOnClickListener {
                MembershipCardDialogFragment.withBenefitType(item.benefitType)
                    .show(supportFragmentManager, MembershipCardDialogFragment.TAG)
            }

            visitSite.run {
                if (item.url.isEmpty()) return@run
                setVisible()
                setOnClickListener { onLinkClicked(item.id, item.name, item.url) }
            }

            perksBottomFloatingButtons.setVisible()
        }
    }

    companion object {
        fun start(context: Context, noteID: String) {
            context.startActivity(Intent(context, PerksDetailActivity::class.java).apply {
                putExtra(BundleKeys.PERK_ID_KEY, noteID)
            })
        }

    }

}