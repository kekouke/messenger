package com.kekouke.tfsspring.navigation

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.github.terrakok.cicerone.Command
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.github.terrakok.cicerone.androidx.FragmentScreen

class TabNavigator(
    activity: FragmentActivity,
    containerId: Int,
    fragmentManager: FragmentManager = activity.supportFragmentManager,
    fragmentFactory: FragmentFactory = fragmentManager.fragmentFactory
) : AppNavigator(activity, containerId, fragmentManager, fragmentFactory) {

    override fun applyCommand(command: Command) {
        when (command) {
            is SelectTabCommand -> selectTab(command.screen)
        }
    }

    private fun selectTab(targetScreen: FragmentScreen) {
        val currentFragment = fragmentManager.fragments.firstOrNull { it.isVisible }
        val targetFragment = fragmentManager.findFragmentByTag(targetScreen.screenKey)

        if (
            currentFragment != null &&
            targetFragment != null &&
            currentFragment === targetFragment
        ) return

        with(fragmentManager.beginTransaction()) {

            currentFragment?.let { fragment ->
                hide(fragment)
                setMaxLifecycle(fragment, Lifecycle.State.STARTED)
            }

            targetFragment?.let { fragment ->
                show(fragment)
                setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
            } ?: add(
                containerId,
                targetScreen.createFragment(fragmentFactory),
                targetScreen.screenKey
            )

            commit()
        }

    }
}