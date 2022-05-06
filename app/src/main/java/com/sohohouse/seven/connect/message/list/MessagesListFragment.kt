package com.sohohouse.seven.connect.message.list

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.extensions.setFragmentResult
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.common.views.CustomDialogFactory
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.connect.ConnectTabFragment
import com.sohohouse.seven.connect.message.chat.ChatActivity
import com.sohohouse.seven.connect.message.model.MessagesListItem
import com.sohohouse.seven.databinding.FragmentMessagesListBinding
import com.sohohouse.seven.main.MainNavigationController
import com.sohohouse.seven.network.chat.model.channel.OneToOneChatChannel

class MessagesListFragment : BaseMVVMFragment<MessagesListViewModel>(), Loadable.View {

    val binding by viewBinding(FragmentMessagesListBinding::bind)
    private var sendAMessage: Button? = null
    override val contentLayoutId get() = R.layout.fragment_messages_list

    override val viewModelClass: Class<MessagesListViewModel>
        get() = MessagesListViewModel::class.java

    override val loadingView: LoadingView
        get() = binding.listLoading

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = MessagesListAdapter()

        initView(adapter)
        viewModel.channelsLiveData.observe(lifecycleOwner) { list ->
            binding.emptyStates.isVisible = list.isEmpty()
            adapter.submitList(list.map {
                it.copy(onClick = { navigateToChatScreen(it) })
            })
            binding.messagesRv.requestLayout()
        }

        viewModel.removeMessageDialog.observe(viewLifecycleOwner, ::initiateMessageDeletion)

        createSendAMessageButtonAtTheTopOfTheBottomBar()
        observeLoadingState(viewLifecycleOwner) {
            (requireActivity() as? MainNavigationController)?.setLoadingState(it)
            sendAMessage?.isVisible = when (it) {
                LoadingState.Idle -> isResumed
                LoadingState.Loading -> false
            }
        }
    }

    private fun createSendAMessageButtonAtTheTopOfTheBottomBar() {
        val topOfBottomBar =
            requireActivity().findViewById<FrameLayout?>(R.id.topOfBottomNavigationView)
                ?: return
        sendAMessage =
            Button(ContextThemeWrapper(requireContext(), R.style.Button_Primary), null, 0).apply {
                text = getString(R.string.send_message)
                tag = R.string.send_message
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginStart = resources.getDimensionPixelOffset(R.dimen.dp_16)
                    topMargin = resources.getDimensionPixelOffset(R.dimen.dp_16)
                    marginEnd = resources.getDimensionPixelOffset(R.dimen.dp_16)
                    bottomMargin = resources.getDimensionPixelOffset(R.dimen.dp_24)
                }
                isVisible = false
                topOfBottomBar.addView(this)

                setOnClickListener {
                    parentFragment?.setFragmentResult(ConnectTabFragment.NAVIGATE_TO_CONNECTIONS)
                }
            }
    }

    override fun onPause() {
        sendAMessage?.setGone()
        super.onPause()
    }

    override fun onResume() {
        sendAMessage?.isVisible = viewModel.isMessagesListFetched
        super.onResume()
    }

    override fun onDestroyView() {
        val topOfBottomBar =
            requireActivity().findViewById<FrameLayout?>(R.id.topOfBottomNavigationView)
                ?: return
        topOfBottomBar.children.find { it.tag == sendAMessage?.tag }
            ?.let(topOfBottomBar::removeView)
        super.onDestroyView()
    }

    private fun initiateMessageDeletion(channel: OneToOneChatChannel) {
        CustomDialogFactory.createThemedAlertDialog(
            context = requireContext(),
            title = getString(R.string.messaging_delete_conversation),
            message = getString(R.string.messaging_this_chat_will_be_deleted),
            positiveClickListener = { _, _ ->
                viewModel.removeChannel(channel)
            },
            positiveButtonText = getString(R.string.messaging_delete_messages),
            negativeButtonText = getString(R.string.messaging_cancel),
        ).show()
    }

    private fun navigateToChatScreen(item: MessagesListItem) {
        startActivity(ChatActivity.newIntentViaURL(requireContext(), item.chatUrl, item.memberId))
    }

    private fun initView(adapter: MessagesListAdapter) {
        with(binding) {
            messagesRv.adapter = adapter
            messagesRv.setHasFixedSize(true)
        }
    }

}