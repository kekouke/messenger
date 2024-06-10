package com.kekouke.tfsspring.presentation.chat

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BundleCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.github.terrakok.cicerone.Router
import com.kekouke.tfsspring.CompositeAdapter
import com.kekouke.tfsspring.appComponent
import com.kekouke.tfsspring.presentation.chat.adapters.delegates.DateAdapterDelegate
import com.kekouke.tfsspring.presentation.chat.adapters.delegates.ReceivedMessageAdapterDelegate
import com.kekouke.tfsspring.presentation.chat.adapters.delegates.SendMessageAdapterDelegate
import com.kekouke.tfsspring.presentation.chat.tea.ChatEvent.UI.ChangeReactions
import com.kekouke.tfsspring.presentation.chat.tea.ChatEvent.UI.LoadNextPage
import com.kekouke.tfsspring.presentation.chat.tea.ChatEvent.UI.NavigateBack
import com.kekouke.tfsspring.presentation.chat.tea.ChatEvent.UI.SendMessage
import com.kekouke.tfsspring.presentation.chat.tea.ChatEvent.UI.ShowChat
import com.kekouke.tfsspring.presentation.chat.tea.ChatNews
import com.kekouke.tfsspring.presentation.chat.tea.ChatStoreFactory
import com.kekouke.tfsspring.concatenateWithDate
import com.kekouke.tfsspring.databinding.FragmentChatBinding
import com.kekouke.tfsspring.di.components.DaggerChatComponent
import com.kekouke.tfsspring.doOnTextChanged
import com.kekouke.tfsspring.domain.model.Reaction
import com.kekouke.tfsspring.domain.model.Topic
import com.kekouke.tfsspring.hide
import com.kekouke.tfsspring.show
import com.kekouke.tfsspring.showShortToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tinkoff.kotea.android.storeViaViewModel
import javax.inject.Inject
import com.kekouke.tfsspring.R as TfSpringR

class ChatFragment : Fragment(), ChatListener {

    @Inject
    lateinit var storeFactory: ChatStoreFactory

    @Inject
    lateinit var router: Router

    private val store by storeViaViewModel {
        storeFactory.create(currentTopic)
    }

    private val currentTopic by lazy {
        BundleCompat.getParcelable(requireArguments(), KEY_TOPIC, Topic::class.java)
            ?: throw IllegalStateException("No topic was passed as argument")
    }

    private var _binding: FragmentChatBinding? = null
    val binding: FragmentChatBinding
        get() = _binding
            ?: throw IllegalStateException("Binding is not initialized before use in ChatFragment")

    private val adapter = CompositeAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerChatComponent.factory().create(requireContext().appComponent()).inject(this)

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        addListeners()
        observeStore()
        fillActivityWithTopic(currentTopic)
    }

    private fun setupRecyclerView() {
        binding.rvMessages.adapter = adapter.apply {
            addDelegate(SendMessageAdapterDelegate(this@ChatFragment))
            addDelegate(ReceivedMessageAdapterDelegate(this@ChatFragment))
            addDelegate(DateAdapterDelegate())
        }

        binding.rvMessages.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy >= 0) return

                val layoutManager = (recyclerView.layoutManager as LinearLayoutManager)
                if (layoutManager.findFirstVisibleItemPosition() <= LOAD_NEXT_PAGE_THRESHOLD) {
                    store.dispatch(LoadNextPage)
                }
            }
        })

        binding.rvMessages.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view)
                    if (position == 0) {
                        outRect.top = resources.getDimension(
                            TfSpringR.dimen.chat_item_horizontal_offset
                        ).toInt()
                    }
                    outRect.bottom = resources.getDimension(
                        TfSpringR.dimen.chat_item_horizontal_offset
                    ).toInt()
                }
            }
        )
    }

    private fun addListeners() {
        binding.etMessage.doOnTextChanged { text ->
            val canSend = text.isNotBlank()
            binding.btnSend.isVisible = canSend.also {
                binding.btnAttachContent.isVisible = !canSend
            }
        }

        binding.btnSend.setOnClickListener {
            store.dispatch(SendMessage(binding.etMessage.text.toString()))
        }

        binding.toolbar.setNavigationOnClickListener {
            store.dispatch(NavigateBack)
        }

        binding.layoutNetworkError.btnRetry.setOnClickListener {
            store.dispatch(ShowChat)
        }
    }

    private fun observeStore() {
        lifecycleScope.launch {
            store.state
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect(::render)
        }

        lifecycleScope.launch {
            store.news
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect(::handleNews)
        }
    }

    private fun render(state: ChatState) {
        when (state) {
            is ChatState.Initial -> {
                store.dispatch(ShowChat)
            }

            is ChatState.Content -> {
                binding.shimmer.hide()

                binding.progress.isVisible = state.process
                binding.rvMessages.isVisible = true

                onContentReceived(state)
            }

            is ChatState.Loading -> {
                binding.shimmer.show()
                binding.rvMessages.isVisible = false
                binding.layoutNetworkError.root.isVisible = false
            }

            is ChatState.MessagesLoadError -> {
                binding.shimmer.hide()
                binding.progress.isVisible = false
                binding.layoutNetworkError.root.isVisible = true
            }
        }
    }

    private fun onContentReceived(state: ChatState.Content) {
        lifecycleScope.launch(Dispatchers.Default) {
            val delegateItems = state.messages.concatenateWithDate()

            withContext(Dispatchers.Main) {
                adapter.submitList(delegateItems) {
                    if (state.needToScroll) {
                        binding.rvMessages.scrollToPosition(adapter.itemCount - 1)
                    }
                }
            }
        }
    }

    private fun handleNews(news: ChatNews) = when (news) {
        ChatNews.SendMessageError -> {
            showShortToast(getString(TfSpringR.string.error_send_message))
        }

        ChatNews.MessageSent -> {
            binding.etMessage.text.clear()
            binding.progress.isVisible = false
        }

        ChatNews.ChangeReactionError -> {
            showShortToast(getString(TfSpringR.string.error_change_emoji))
        }

        ChatNews.RegisterQueueError -> {
            showShortToast(getString(TfSpringR.string.error_event_queue))
        }

        ChatNews.LoadMessagesError -> {
            showShortToast(getString(TfSpringR.string.error_load_messages))
        }

        ChatNews.NavigateBack -> router.exit()
    }

    private fun fillActivityWithTopic(topic: Topic) {
        binding.toolbar.title = topic.streamName
        binding.tvTopicName.text = getString(TfSpringR.string.topic_name_placeholder, topic.name)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAddEmojiRequest(messageId: Int) {
        val modalBottomSheet = EmojiPickerBottomSheet.newInstance(messageId)
        modalBottomSheet.show(childFragmentManager, EmojiPickerBottomSheet.TAG)
    }

    override fun onEmojiClick(messageId: Int, reaction: Reaction) {
        store.dispatch(ChangeReactions(messageId, reaction))
    }

    companion object {

        private const val KEY_TOPIC = "extra_topic"

        private const val LOAD_NEXT_PAGE_THRESHOLD = 5

        fun newInstance(topic: Topic) = ChatFragment().apply {
            arguments = Bundle().apply {
                putParcelable(KEY_TOPIC, topic)
            }
        }
    }
}