package com.kekouke.tfsspring.presentation.people

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.kekouke.tfsspring.appComponent
import com.kekouke.tfsspring.databinding.FragmentPeopleBinding
import com.kekouke.tfsspring.di.components.DaggerPeopleComponent
import com.kekouke.tfsspring.doOnQueryTextChange
import com.kekouke.tfsspring.hide
import com.kekouke.tfsspring.presentation.people.tea.PeopleEvent.UI.DisplayPeopleEvent
import com.kekouke.tfsspring.presentation.people.tea.PeopleEvent.UI.NavigateToProfile
import com.kekouke.tfsspring.presentation.people.tea.PeopleEvent.UI.Search
import com.kekouke.tfsspring.presentation.people.tea.PeopleNews
import com.kekouke.tfsspring.presentation.people.tea.PeopleStoreFactory
import com.kekouke.tfsspring.show
import com.kekouke.tfsspring.showShortToast
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.tinkoff.kotea.android.storeViaViewModel
import javax.inject.Inject
import com.kekouke.tfsspring.R as TfSpringR

class PeopleFragment : Fragment() {

    @Inject
    lateinit var storeFactory: PeopleStoreFactory

    private val store by storeViaViewModel {
        storeFactory.create()
    }

    private var _binding: FragmentPeopleBinding? = null
    private val binding: FragmentPeopleBinding
        get() = _binding ?: throw RuntimeException("FragmentPeopleBinding can't be null")

    private val adapter = UserAdapter().apply {
        onUserClick = { user ->
            store.dispatch(NavigateToProfile(user))
        }
    }

    private val searchQueryFlow by lazy {
        MutableSharedFlow<String>(replay = 1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerPeopleComponent.factory().create(requireContext().appComponent()).inject(this)

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPeopleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        addListeners()
        observeStore()
        listenToSearchQuery()
    }

    private fun setupRecyclerView() {
        binding.rvPeople.adapter = adapter

        binding.rvPeople.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.bottom = resources.getDimension(
                        TfSpringR.dimen.user_item_horizontal_offset
                    ).toInt()
                }
            }
        )
    }

    private fun addListeners() {
        binding.layoutNetworkError.btnRetry.setOnClickListener {
            store.dispatch(DisplayPeopleEvent)
        }

        val searchBar = binding.toolbar.menu.findItem(TfSpringR.id.search_bar)
        (searchBar.actionView as? SearchView)?.doOnQueryTextChange { newQuery ->
            lifecycleScope.launch {
                searchQueryFlow.emit(newQuery)
            }
        }

        binding.toolbar.setOnClickListener {
            if (searchBar.isActionViewExpanded.not()) {
                searchBar.expandActionView()
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

    private fun render(state: PeopleState) {
        when (state) {
            is PeopleState.Initial -> {
                store.dispatch(DisplayPeopleEvent)
            }

            is PeopleState.Loading -> {
                binding.shimmer.show()
                binding.rvPeople.isVisible = false
                binding.layoutNetworkError.root.isVisible = false
            }

            is PeopleState.Content -> {
                binding.shimmer.hide()
                binding.rvPeople.isVisible = true
                adapter.submitList(state.users)
            }

            is PeopleState.NetworkError -> {
                binding.shimmer.hide()
                binding.rvPeople.isVisible = false
                binding.layoutNetworkError.root.isVisible = true
            }
        }
    }

    private fun handleNews(news: PeopleNews) = when (news) {
        is PeopleNews.ShowErrorToast -> {
            showShortToast(getString(TfSpringR.string.error_update_users))
        }
    }

    @OptIn(FlowPreview::class)
    private fun listenToSearchQuery() {
        lifecycleScope.launch {
            searchQueryFlow
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

    companion object {
        fun newInstance() = PeopleFragment()
    }

}