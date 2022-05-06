package com.sohohouse.seven.profile.share

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMBottomSheet
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.dagger.Injectable
import com.sohohouse.seven.common.utils.collectLatest
import com.sohohouse.seven.databinding.FragmentShareProfileDialogBinding
import com.sohohouse.seven.network.core.models.ShortProfileUrlResponse
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit


class ShareProfileBottomSheet : BaseMVVMBottomSheet<ShareProfileViewModel>(),
    Injectable {

    private lateinit var shortProfileUrl: ShortProfileUrlResponse

    override val viewModelClass: Class<ShareProfileViewModel>
        get() = ShareProfileViewModel::class.java

    override val contentLayout: Int = R.layout.fragment_share_profile_dialog

    override val fixedHeight: Int = ViewGroup.LayoutParams.WRAP_CONTENT

    val binding by viewBinding(FragmentShareProfileDialogBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setupViews()
    }

    private fun FragmentShareProfileDialogBinding.setupViews() {
        setupObserver()

        copyLink.setOnClickListener {
            context?.let {
                viewModel.logAnalyticsActions(AnalyticsManager.Action.ConnectShareProfileCopyLink)
                val clipboard = getSystemService(it, ClipboardManager::class.java)
                val clip = ClipData.newPlainText("Short profile URL label", getShortProfileUrl())
                clipboard?.setPrimaryClip(clip)

                copyLinkText.text = resources.getString(R.string.link_copied)
                copyLinkIcon.setImageResource(R.drawable.ic_checkmark_gray)
                resetCopyLinkUI()
            }
        }

        more.setOnClickListener {
            viewModel.logAnalyticsActions(AnalyticsManager.Action.ConnectShareProfileMoreOptions)
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getShortProfileUrl())
                putExtra(Intent.EXTRA_TITLE, resources.getString(R.string.connect_with_me))
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

    }

    private fun resetCopyLinkUI() {
        lifecycleScope.launch {
            delay(TimeUnit.SECONDS.toMillis(2))
            with(binding) {
                copyLinkText.text = resources.getString(R.string.copy_link)
                copyLinkIcon.setImageResource(R.drawable.ic_copy_link)
            }
        }
    }

    private fun getShortProfileUrl(): String =
        if (this::shortProfileUrl.isInitialized) shortProfileUrl.shortUrl else ""

    private fun setupObserver() {
        viewModel.shortProfileUrl.collectLatest(viewLifecycleOwner) {
            shortProfileUrl = it
            generateQRCode()
        }
    }

    private fun generateQRCode() {
        if (shortProfileUrl.shortUrl.isEmpty()) return
        val dim = binding.qrCode.layoutParams.height

        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            FirebaseCrashlytics.getInstance().recordException(throwable)
        }

        lifecycleScope.launch(Dispatchers.Default + coroutineExceptionHandler) {
            val hints = HashMap<EncodeHintType, Int>()
            hints[EncodeHintType.MARGIN] = 2
            val bitmap = BarcodeEncoder().encodeBitmap(
                shortProfileUrl.shortUrl,
                BarcodeFormat.QR_CODE,
                dim,
                dim,
                hints
            )

            withContext(Dispatchers.Main) {
                binding.qrCode.setImageBitmap(bitmap)
            }
        }
    }

    companion object {
        const val TAG = "share_profile_dialog"
    }

}