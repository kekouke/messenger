package com.kekouke.tfsspring

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.terrakok.cicerone.Cicerone
import com.kekouke.tfsspring.databinding.FragmentMainBinding
import com.kekouke.tfsspring.navigation.KEY_NAVIGATION_CHANNELS
import com.kekouke.tfsspring.navigation.OnBackPressedListener
import com.kekouke.tfsspring.navigation.Screens
import com.kekouke.tfsspring.navigation.TabNavigator
import com.kekouke.tfsspring.navigation.TabRouter
import javax.inject.Inject
import com.kekouke.tfsspring.R as TfSpringR

class MainFragment : Fragment(), OnBackPressedListener {

    private var _binding: FragmentMainBinding? = null
    val binding: FragmentMainBinding
        get() = _binding
            ?: throw IllegalStateException("Binding is not initialized before use in MainFragment")

    private val navigator: TabNavigator by lazy {
        TabNavigator(requireActivity(), TfSpringR.id.tab_fragment_container, childFragmentManager)
    }

    @Inject lateinit var navigation: Cicerone<TabRouter>

    private val router: TabRouter get() = navigation.router

    private val navigationHolder get() = navigation.getNavigatorHolder()

    override fun onCreate(savedInstanceState: Bundle?) {
        requireContext().appComponent().inject(this)

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupNavigationBar()

        if (savedInstanceState == null) {
            router.selectTab(Screens.Channels())
        }
    }

    private fun setupNavigationBar() {
        binding.bottomNavBar.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                binding.bottomNavBar.selectedItemId -> Unit
                TfSpringR.id.navigation_channels -> router.selectTab(Screens.Channels())
                TfSpringR.id.navigation_people -> router.selectTab(Screens.People())
                TfSpringR.id.navigation_profile -> router.selectTab(Screens.Profile())
            }

            true
        }
    }

    override fun onResume() {
        super.onResume()
        navigationHolder.setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        navigationHolder.removeNavigator()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressed(): Boolean {
        val fragment = childFragmentManager.findFragmentByTag(KEY_NAVIGATION_CHANNELS)

        if (fragment == null || fragment.isVisible) return false

        binding.bottomNavBar.selectedItemId = TfSpringR.id.navigation_channels

        return true
    }
}