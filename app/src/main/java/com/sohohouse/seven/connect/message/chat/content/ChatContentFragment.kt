package com.sohohouse.seven.connect.message.chat.content

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.sohohouse.seven.R
import com.sohohouse.seven.base.error.ErrorDialogHelper
import com.sohohouse.seven.base.error.ErrorHelper.FILE_CREATE_ERROR
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.base.mvvm.fragmentViewModel
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.common.views.showSafe
import com.sohohouse.seven.connect.message.chat.ChatViewModel
import com.sohohouse.seven.connect.message.chat.content.menu.MenuBottomSheet
import com.sohohouse.seven.connect.message.chat.content.menu.accept.AcceptRequestBottomSheet
import com.sohohouse.seven.connect.message.chat.content.menu.attach.AttachFileBottomSheet
import com.sohohouse.seven.connect.message.chat.content.menu.attach.AttachFileBottomSheet.Companion.ATTACH_TYPE_CAMERA_IMAGE
import com.sohohouse.seven.connect.message.chat.content.menu.attach.AttachFileBottomSheet.Companion.ATTACH_TYPE_CAMERA_VIDEO
import com.sohohouse.seven.connect.message.chat.content.menu.attach.AttachFileBottomSheet.Companion.ATTACH_TYPE_MEDIA
import com.sohohouse.seven.connect.message.chat.content.menu.attach.AttachFileBottomSheet.Companion.REQUEST_KEY_ATTACH_FILE
import com.sohohouse.seven.connect.message.chat.content.menu.attach.TakeImageManager
import com.sohohouse.seven.databinding.ChatContentFragmentBinding
import com.sohohouse.seven.network.chat.ChannelId
import com.sohohouse.seven.profile.view.ProfileViewerFragment
import javax.inject.Inject
import kotlin.text.isNotEmpty

class ChatContentFragment : BaseMVVMFragment<ChatContentViewModel>() {

    @Inject
    lateinit var factory: ChatContentViewModel.Factory

    @Inject
    lateinit var cacheDataSourceFactory: CacheDataSource.Factory

    override val viewModelClass: Class<ChatContentViewModel>
        get() = ChatContentViewModel::class.java

    override val viewModel: ChatContentViewModel by fragmentViewModel {
        factory.create(
            channelId = requireArguments().getString(CHANNEL_ID, ""),
            channelUrl = requireArguments().getString(CHANNEL_URL, ""),
            memberProfileId = requireArguments().getString(MEMBER_ID, "")
        )
    }

    private val actViewModel: ChatViewModel by activityViewModels()

    private var boundView: ChatContentFragmentBinding? = null

    var imageManager: TakeImageManager? = null

    private val permissionsForCamera = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val requestCameraPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it[Manifest.permission.CAMERA] == true)
                imageManager?.pickCamera(this)
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        imageManager = TakeImageManager(activity as AppCompatActivity).apply {
            onFileCreateError = {
                ErrorDialogHelper.showErrorDialogByErrorCode(context, arrayOf(FILE_CREATE_ERROR))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        boundView = ChatContentFragmentBinding.bind(view)
        boundView?.backButton?.setOnClickListener { setFragmentResult(GO_BACK) }
        boundView?.menuDots?.setOnClickListener {
            viewModel.openThreeDotMenu()
        }

        boundView?.attachFile?.setOnClickListener {
            AttachFileBottomSheet.newInstance()
                .withResultListener(REQUEST_KEY_ATTACH_FILE) { requestKey, bundle ->
                    if (requestKey == REQUEST_KEY_ATTACH_FILE)
                        onMessageAttachSelected(bundle.getString(BundleKeys.EVENT))
                }
                .showSafe(requireActivity().supportFragmentManager, AttachFileBottomSheet.TAG)
        }
        boundView?.sendMessage?.setOnClickListener {
            val input = boundView?.input?.text?.toString() ?: ""
            if (input.isNotEmpty()) {
                viewModel.sendMessage(input)
                boundView?.input?.setText("")
            }
        }

        boundView?.chatContentFragmentProfileTapArea?.setOnClickListener {
            viewModel.openRecipientProfile()
        }

        val chatContentAdapter = ChatContentAdapter()
        boundView?.list?.cacheDataSourceFactory = cacheDataSourceFactory
        boundView?.list?.adapter = chatContentAdapter
        val layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
        boundView?.list?.layoutManager = layoutManager

        boundView?.list?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val isTopMost = layoutManager.findFirstCompletelyVisibleItemPosition() <= 1
                if (isTopMost) {
                    viewModel.fetchMoreMessages()
                }
            }
        })

        chatContentAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                if (positionStart != 0) {
                    boundView?.list?.scrollToPosition(positionStart)
                }
            }
        })

        viewModel.onMediaClick = { urlPair ->
            actViewModel.goFullScreen(urlPair)
        }
        boundView?.observeViewModel(chatContentAdapter)
    }

    private fun ChatContentFragmentBinding.observeViewModel(
        chatContentAdapter: ChatContentAdapter
    ) {
        viewModel.messages.observe(viewLifecycleOwner) {
            chatContentAdapter.submitList(it)
            sendRequestHeadline.isVisible = viewModel.shouldShowSendRequest()
            sendRequestDescription.isVisible = viewModel.shouldShowSendRequest()
            list.isVisible = it.isNotEmpty()
        }

        viewModel.recipient.observe(viewLifecycleOwner) { uiModel ->
            name.text = uiModel.name
            profileImage.setImageUrl(uiModel.imageUrl)

            warningSendRequest.isVisible = uiModel.showConnectionBanner
            sendRequestHeadline.isVisible = viewModel.shouldShowSendRequest()
            sendRequestDescription.isVisible = viewModel.shouldShowSendRequest()
            list.isVisible = uiModel.hasMessages
            if (uiModel.showConnectionBanner) {
                sendRequestDescription.text = getString(R.string.send_request_description)
                warningSendRequest.setTitle(
                    getString(R.string.invite_s_to_your_network, uiModel.name)
                )

                warningSendRequest.onButtonClick {
                    uiModel.sendRequest()
                    warningSendRequest.setGone()
                }
            }
        }

        viewModel.isRecipientBlocked.observe(viewLifecycleOwner) {
            val name = viewModel.recipient.value?.name
            if (it) {
                warningBlockedMember.isVisible = true
                warningBlockedMember.setTitle(
                    getString(R.string.you_have_blocked_someone, name)
                )
                warningBlockedMember.setDescription(
                    getString(R.string.you_need_to_unblock_them, name)
                )
                warningBlockedMember.onButtonClick {
                    viewModel.unblockMember()
                }
            } else {
                warningBlockedMember.isVisible = false
            }
        }

        viewModel.threeDotMenuAction.observe(viewLifecycleOwner) {
            MenuBottomSheet.with(it.first, it.second)
                .showSafe(parentFragmentManager, MenuBottomSheet.TAG)
        }
        viewModel.openProfile.observe(viewLifecycleOwner) {
            openProfile(it)
        }
        viewModel.showLoader.observe(viewLifecycleOwner) {
            loader.setVisible(it)
        }
        viewModel.messagingInvitation.observe(viewLifecycleOwner) {
            AcceptRequestBottomSheet.with(it.first, it.second)
                .showSafe(parentFragmentManager, AcceptRequestBottomSheet.TAG)
        }
    }

    private fun onMessageAttachSelected(attachType: String?) {
        when (attachType) {
            ATTACH_TYPE_CAMERA_VIDEO,
            ATTACH_TYPE_CAMERA_IMAGE -> {
                imageManager?.cameraMode = attachType
                if (imageManager?.checkCameraPermission() == true)
                    imageManager?.pickCamera(this)
                else
                    requestCameraPermissions.launch(permissionsForCamera)
            }
            ATTACH_TYPE_MEDIA -> imageManager?.openMediaLibrary(this)
        }
    }

    private fun openProfile(item: ProfileItem) {
        ProfileViewerFragment.withProfile(item)
            .showSafe(parentFragmentManager, ProfileViewerFragment.TAG)
    }

    override val contentLayoutId: Int
        get() {
            return R.layout.chat_content_fragment
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == TakeImageManager.REQUEST_CODE_LIBRARY && data?.data != null)
                data.data?.let {
                    sendFileMessage(it)
                }
            if (requestCode == TakeImageManager.REQUEST_CODE_CAMERA) {
                if (data?.data != null)
                    sendFileMessage(data.data!!)
                else
                    imageManager?.file?.let {
                        viewModel.sendImageMessage(it)
                    }
            }
        } else if (requestCode == TakeImageManager.REQUEST_CODE_CAMERA) {
            imageManager?.deleteFile()
        }
    }

    private fun sendFileMessage(data: Uri) {
        val file = data.getFile(requireContext())
        if (file != null)
            viewModel.sendImageMessage(file)
    }

    override fun onDestroyView() {
        boundView?.list?.releasePlayer()
        boundView = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance(
            channelId: ChannelId,
            channelUrl: String,
            memberId: String
        ) = ChatContentFragment().apply {
            arguments = bundleOf(
                CHANNEL_ID to channelId,
                CHANNEL_URL to channelUrl,
                MEMBER_ID to memberId
            )
        }

        const val CHANNEL_ID = "CHANNEL_NAME"
        const val CHANNEL_URL = "CHANNEL_URL"
        const val MEMBER_ID = "MEMBER_ID"
        const val GO_BACK = "GO_BACK"
        const val TAG = "ChatContentFragment"
    }

}