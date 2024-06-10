package com.kekouke.tfsspring.presentation.streams.tab

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
import com.kekouke.tfsspring.CompositeAdapter
import com.kekouke.tfsspring.appComponent
import com.kekouke.tfsspring.concatenateWithTopics
import com.kekouke.tfsspring.databinding.FragmentStreamsTabBinding
import com.kekouke.tfsspring.di.components.DaggerStreamsTabComponent
import com.kekouke.tfsspring.domain.model.Stream
import com.kekouke.tfsspring.domain.model.Topic
import com.kekouke.tfsspring.hide
import com.kekouke.tfsspring.show
import com.kekouke.tfsspring.showShortToast
import com.kekouke.tfsspring.presentation.streams.SearchQueryHandler
import com.kekouke.tfsspring.presentation.streams.tab.recycler.StreamAdapterDelegate
import com.kekouke.tfsspring.presentation.streams.tab.recycler.TopicAdapterDelegate
import com.kekouke.tfsspring.presentation.streams.tab.tea.StreamsTabEvent.UI.DisplayStreams
import com.kekouke.tfsspring.presentation.streams.tab.tea.StreamsTabEvent.UI.HideTopics
import com.kekouke.tfsspring.presentation.streams.tab.tea.StreamsTabEvent.UI.NavigateToChat
import com.kekouke.tfsspring.presentation.streams.tab.tea.StreamsTabEvent.UI.Search
import com.kekouke.tfsspring.presentation.streams.tab.tea.StreamsTabEvent.UI.ShowTopics
import com.kekouke.tfsspring.presentation.streams.tab.tea.StreamsTabNews
import com.kekouke.tfsspring.presentation.streams.tab.tea.StreamsTabStoreFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tinkoff.kotea.android.storeViaViewModel
import javax.inject.Inject
import com.kekouke.tfsspring.R as TfSpringR

class StreamsTabFragment : Fragment(), StreamsListener {

    private var _binding: FragmentStreamsTabBinding? = null
    private val binding: FragmentStreamsTabBinding
        get() = _binding ?: throw IllegalStateException("FragmentStreamsListBinding can't be null")

    private val streamsType: StreamsType by lazy {
        BundleCompat.getParcelable(
            requireArguments(),
            ARG_STREAMS_TYPE,
            StreamsType::class.java
        ) ?: throw IllegalStateException("StreamsType not found in arguments")
    }

    @Inject
    lateinit var storeFactory: StreamsTabStoreFactory

    private val store by storeViaViewModel {
        storeFactory.create(streamsType)
    }

    private val adapter = CompositeAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerStreamsTabComponent.factory().create(requireContext().appComponent()).inject(this)

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStreamsTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addListeners()
        setupRecyclerView()
        observeStore()
        listenToSearchQuery()
    }

    private fun addListeners() {
        binding.layoutNetworkError.btnRetry.setOnClickListener {
            store.dispatch(DisplayStreams)
        }
    }

    private fun setupRecyclerView() {
        binding.rvStreams.adapter = adapter.apply {
            addDelegate(StreamAdapterDelegate(this@StreamsTabFragment))
            addDelegate(TopicAdapterDelegate(this@StreamsTabFragment))
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

    private fun render(state: StreamsTabState) {
        when (state) {
            is StreamsTabState.Initial -> {
                store.dispatch(DisplayStreams)
            }

            is StreamsTabState.Loading -> {
                binding.shimmer.show()
                binding.layoutNetworkError.root.isVisible = false
            }

            is StreamsTabState.Content -> {
                binding.shimmer.hide()

                binding.layoutNetworkError.root.isVisible = false
                binding.progress.isVisible = state.process
                binding.rvStreams.isVisible = true

                lifecycleScope.launch {
                    val delegates = withContext(Dispatchers.Default) {
                        state.content.concatenateWithTopics()
                    }

                    adapter.submitList(delegates)
                }
            }

            is StreamsTabState.LoadStreamsError -> {
                binding.shimmer.hide()
                binding.progress.isVisible = false
                binding.layoutNetworkError.root.isVisible = true
            }

        }
    }

    private fun handleNews(news: StreamsTabNews) = when (news) {
        StreamsTabNews.LoadTopicsError -> showShortToast(
            getString(TfSpringR.string.error_load_topics)
        )

        StreamsTabNews.LoadStreamsError -> showShortToast(
            getString(TfSpringR.string.error_load_streams)
        )

        StreamsTabNews.RegisterQueueError -> showShortToast(
            getString(TfSpringR.string.error_event_queue)
        )
    }

    @OptIn(FlowPreview::class)
    private fun listenToSearchQuery() {
        lifecycleScope.launch {
            SearchQueryHandler.searchQueryFlow
                .debounce(500)
                .distinctUntilChanged()
                .collect { query ->
                    store.dispatch(Search(query))
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStreamClick(stream: Stream) {
        store.dispatch(
            if (stream.expanded) {
                HideTopics(stream)
            } else {
                ShowTopics(stream)
            }
        )
    }

    override fun onTopicClick(topic: Topic) {
        store.dispatch(NavigateToChat(topic))
    }

    companion object {

        private const val ARG_STREAMS_TYPE = "arg_stream_type"

        fun newInstance(type: StreamsType) = StreamsTabFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_STREAMS_TYPE, type)
            }
        }
    }
}