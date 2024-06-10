package com.kekouke.tfsspring.presentation.streams.form.newstream

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.kekouke.tfsspring.appComponent
import com.kekouke.tfsspring.databinding.FragmentCreateStreamBinding
import com.kekouke.tfsspring.di.components.DaggerAddNewStreamComponent
import com.kekouke.tfsspring.presentation.streams.form.newstream.tea.CreateStreamEvent.UI.CreateStream
import com.kekouke.tfsspring.presentation.streams.form.newstream.tea.CreateStreamEvent.UI.NavigateBack
import com.kekouke.tfsspring.presentation.streams.form.newstream.tea.CreateStreamNews
import com.kekouke.tfsspring.presentation.streams.form.newstream.tea.CreateStreamStoreFactory
import com.kekouke.tfsspring.showShortToast
import kotlinx.coroutines.launch
import ru.tinkoff.kotea.android.storeViaViewModel
import javax.inject.Inject
import com.kekouke.tfsspring.R as TfSpringR

class CreateStreamFragment : Fragment() {

    private var _binding: FragmentCreateStreamBinding? = null
    private val binding: FragmentCreateStreamBinding
        get() = _binding ?: throw IllegalStateException("FragmentCreateStreamBinding can't be null")

    @Inject
    lateinit var storeFactory: CreateStreamStoreFactory

    private val store by storeViaViewModel {
        storeFactory.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerAddNewStreamComponent.factory().create(requireContext().appComponent()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateStreamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        addListeners()
        observeStore()
    }

    private fun addListeners() {
        binding.toolbar.setNavigationOnClickListener {
            store.dispatch(NavigateBack)
        }

        binding.btnCreateStream.setOnClickListener {
            val streamName = binding.etStreamName.text.toString()
            store.dispatch(CreateStream(streamName))
        }

        binding.etStreamName.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus.not()) {
                val inputService = requireActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE
                ) as InputMethodManager

                inputService.hideSoftInputFromWindow(view.windowToken, 0)
            }
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

    private fun render(state: CreateStreamState) {
        binding.progress.isVisible = state.isLoading
        binding.btnCreateStream.isVisible = state.isLoading.not()
    }

    private fun handleNews(news: CreateStreamNews) = when (news) {
        CreateStreamNews.AlreadyExistsError -> showShortToast(
            getString(TfSpringR.string.error_stream_already_exists)
        )

        CreateStreamNews.EmptyStreamNameError -> showShortToast(
            getString(TfSpringR.string.error_channel_name_cannot_be_empty)
        )

        CreateStreamNews.NetworkError -> showShortToast(
            getString(TfSpringR.string.error_occurred)
        )

        CreateStreamNews.RefreshStreamsError -> showShortToast(
            getString(TfSpringR.string.error_load_streams)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance() = CreateStreamFragment()
    }
}