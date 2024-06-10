package com.kekouke.tfsspring.navigation

import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.FragmentScreen

class TabRouter : Router() {

    fun selectTab(screen: FragmentScreen) {
        executeCommands(SelectTabCommand(screen))
    }

}