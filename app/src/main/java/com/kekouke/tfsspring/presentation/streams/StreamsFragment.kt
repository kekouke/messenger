package com.kekouke.tfsspring.presentation.streams

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.github.terrakok.cicerone.Router
import com.google.android.material.tabs.TabLayoutMediator
import com.kekouke.tfsspring.appComponent
import com.kekouke.tfsspring.databinding.FragmentStreamsBinding
import com.kekouke.tfsspring.di.components.DaggerStreamsComponent
import com.kekouke.tfsspring.doOnQueryTextChange
import com.kekouke.tfsspring.navigation.Screens
import com.kekouke.tfsspring.presentation.streams.tab.StreamsTabFragment
import com.kekouke.tfsspring.presentation.streams.tab.StreamsType
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.kekouke.tfsspring.R as TfSpringR

class StreamsFragment : Fragment() {

    private var _binding: FragmentStreamsBinding? = null
    private val binding: FragmentStreamsBinding
        get() = _binding ?: throw RuntimeException("FragmentChannelsBinding can't be null")

    @Inject
    lateinit var mainRouter: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerStreamsComponent.factory().create(requireContext().appComponent()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStreamsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        addListeners()
    }

    private fun setupViewPager() {
        val viewPagerAdapter = object :
            FragmentStateAdapter(
                childFragmentManager,
                viewLifecycleOwner.lifecycle
            ) {

            override fun getItemCount(): Int = STREAMS_TAB_COUNT

            override fun createFragment(position: Int): Fragment = when (position) {
                0 -> StreamsTabFragment.newInstance(StreamsType.SUBSCRIBED)
                1 -> StreamsTabFragment.newInstance(StreamsType.ALL_STREAMS)
                else -> error("Can't create fragment for position: $position")
            }
        }

        with(binding) {
            pager.adapter = viewPagerAdapter

            TabLayoutMediator(tlChannels, pager) { tab, position ->
                tab.text = resources.getStringArray(TfSpringR.array.channels_tab_titles)[position]
            }.attach()
        }
    }

    private fun addListeners() {
        val searchBar = binding.toolbar.menu.findItem(TfSpringR.id.search_bar)
        (searchBar.actionView as? SearchView)?.doOnQueryTextChange { newQuery ->
            lifecycleScope.launch {
                SearchQueryHandler.push(newQuery)
            }
        }

        binding.toolbar.setOnClickListener {
            if (searchBar.isActionViewExpanded.not()) {
                searchBar.expandActionView()
            }
        }

        binding.toolbar.menu.findItem(TfSpringR.id.item_create_stream).run {
            setOnMenuItemClickListener {
                mainRouter.navigateTo(Screens.NewStreamForm())

                true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val STREAMS_TAB_COUNT = 2

        fun newInstance() = StreamsFragment()
    }
}