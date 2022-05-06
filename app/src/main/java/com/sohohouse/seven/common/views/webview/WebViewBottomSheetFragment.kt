package com.sohohouse.seven.common.views.webview

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.Keep
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.branding.ThemeManager
import com.sohohouse.seven.common.BundleKeys.SOHO_WEBVIEW_KICKOUT_ID
import com.sohohouse.seven.common.BundleKeys.SOHO_WEBVIEW_KICKOUT_TYPE
import com.sohohouse.seven.common.BundleKeys.SOHO_WEBVIEW_SHOW_HEADER
import com.sohohouse.seven.common.BundleKeys.SOHO_WEBVIEW_URL
import com.sohohouse.seven.common.BundleKeys.SOHO_WEBVIEW_USE_BEARER_TOKEN
import com.sohohouse.seven.common.apihelpers.SohoWebHelper
import com.sohohouse.seven.common.dagger.Injectable
import com.sohohouse.seven.common.extensions.baseUrl
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.common.securewebview.SecureWebViewListener
import com.sohohouse.seven.common.user.UserSessionManager
import com.sohohouse.seven.common.views.RoundRectOutlineProvider
import com.sohohouse.seven.common.views.SecureWebView
import com.sohohouse.seven.databinding.FragmentBottomSheetWebviewBinding
import com.sohohouse.seven.network.common.HeaderInterceptor
import javax.inject.Inject


@Keep
open class WebViewBottomSheetFragment : BottomSheetDialogFragment(), SecureWebViewListener,
    Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var userSessionManager: UserSessionManager

    @Inject
    lateinit var themeManager: ThemeManager

    protected open val kickoutType: SohoWebHelper.KickoutType
        get() = arguments?.get(
            SOHO_WEBVIEW_KICKOUT_TYPE
        ) as? SohoWebHelper.KickoutType ?: SohoWebHelper.KickoutType.OTHERS
    protected open val id: String? get() = arguments?.getString(SOHO_WEBVIEW_KICKOUT_ID)
    protected open val url: String? get() = arguments?.getString(SOHO_WEBVIEW_URL)
    protected open val showHeader: Boolean?
        get() = arguments?.getBoolean(
            SOHO_WEBVIEW_SHOW_HEADER,
            false
        )

    private val viewModel: SohoWebViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(SohoWebViewModel::class.java)
    }

    override fun getTheme(): Int = themeManager.bottomSheetDarkTheme

    private val binding by viewBinding(FragmentBottomSheetWebviewBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet_webview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setupViews()
        setupViewModel()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener { setupBottomSheet(it as BottomSheetDialog) }
        }
    }

    private fun setupBottomSheet(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet: View = bottomSheetDialog.findViewById(R.id.design_bottom_sheet) ?: return
        bottomSheet.layoutParams =
            bottomSheet.layoutParams.apply { height = WindowManager.LayoutParams.MATCH_PARENT }

        BottomSheetBehavior.from<View?>(bottomSheet).apply {
            this.state = BottomSheetBehavior.STATE_EXPANDED
            this.isDraggable = false
            this.isHideable = false
            this.skipCollapsed = true
        }
    }

    override fun onDestroyView() {
        binding.webView.onDestroy()
        super.onDestroyView()
    }

    private fun FragmentBottomSheetWebviewBinding.setupViews() {
        requireView().outlineProvider = RoundRectOutlineProvider()
        requireView().clipToOutline = true

        close.setOnClickListener { dismiss() }
        backward.setOnClickListener { if (webView.canGoBack()) webView.goBack() }
        forward.setOnClickListener { if (webView.canGoBack()) webView.goBack() }
        reload.setOnClickListener { webView.reload() }

        invalidateMenu()
        reload.isEnabled = false

        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.colorLayer2, typedValue, true)
        webView.setBackgroundColor(typedValue.data)

        errorState.reloadClicks {
            showErrorState(false)
            loadUrl()
        }
        fragmentBottomSheetWebviewLoadingView.setVisible()
    }

    private fun invalidateMenu() = with(binding) {
        backward.isEnabled = webView.canGoBack()
        forward.isEnabled = webView.canGoForward()
    }

    private fun setupViewModel() {
        viewModel.loadingState.observe(
            viewLifecycleOwner,
            Observer { showLoadingState(it is LoadingState.Loading) })
        viewModel.errorViewState.observe(viewLifecycleOwner, Observer { showErrorState(true) })
        viewModel.uri.observe(viewLifecycleOwner, Observer { showWebView(it) })
        loadUrl()
    }

    private fun loadUrl() {
        viewModel.loadUrl(type = kickoutType, id = id, url = url)
    }

    private fun showWebView(uri: Uri) {
        binding.webView.setup(listOf(uri.baseUrl()), this)
        val extraHeaders = HashMap<String, String>()
        if (viewModel.isSecureUrl(kickoutType) || arguments?.getBoolean(
                SOHO_WEBVIEW_USE_BEARER_TOKEN
            ) == true
        ) {
            extraHeaders[HeaderInterceptor.AUTHORIZATION_KEY] =
                "${HeaderInterceptor.BEARER_IDENTIFIER_KEY} " + userSessionManager.token
        }
        binding.webView.loadUrl(uri.toString(), extraHeaders)
    }

    override fun onLoadingStarted() {
        showErrorState(false)
        showLoadingState(true)
        binding.reload.isEnabled = false
    }

    override fun onLoadingFinished() {
        showLoadingState(false)
        binding.reload.isEnabled = true
    }

    override fun onVisitedHistoryUpdated() {
        invalidateMenu()
    }

    override fun getWebView(): SecureWebView {
        return binding.webView
    }

    private fun showLoadingState(visible: Boolean) {
        binding.fragmentBottomSheetWebviewLoadingView.toggleSpinner(visible)
    }

    private fun showErrorState(visible: Boolean) {
        binding.errorState.isVisible = visible
    }

    companion object {
        const val TAG = "web_view_bottom_sheet"

        fun withKickoutType(
            type: SohoWebHelper.KickoutType,
            id: String? = null,
            showHeader: Boolean = false
        ): WebViewBottomSheetFragment {
            return WebViewBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(SOHO_WEBVIEW_KICKOUT_TYPE, type)
                    putString(SOHO_WEBVIEW_KICKOUT_ID, id)
                    putBoolean(SOHO_WEBVIEW_SHOW_HEADER, showHeader)
                }
            }
        }

        fun withUrl(
            url: String,
            useBearerToken: Boolean = false,
            titleRes: Int = -1
        ): WebViewBottomSheetFragment {
            return WebViewBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString(SOHO_WEBVIEW_URL, url)
                    putBoolean(SOHO_WEBVIEW_USE_BEARER_TOKEN, useBearerToken)
                }
            }
        }
    }

}