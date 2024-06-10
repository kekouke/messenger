package com.kekouke.tfsspring.navigation

import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.kekouke.tfsspring.presentation.chat.ChatFragment
import com.kekouke.tfsspring.domain.model.Topic
import com.kekouke.tfsspring.domain.model.User
import com.kekouke.tfsspring.presentation.people.PeopleFragment
import com.kekouke.tfsspring.presentation.profile.ProfileFragment
import com.kekouke.tfsspring.presentation.streams.StreamsFragment
import com.kekouke.tfsspring.presentation.streams.form.newstream.CreateStreamFragment

const val KEY_NAVIGATION_CHANNELS = "navigation_channels"
const val KEY_NAVIGATION_PEOPLE = "navigation_people"
const val KEY_NAVIGATION_PROFILE = "navigation_profile"

object Screens {
    fun Channels() = FragmentScreen(key = KEY_NAVIGATION_CHANNELS) { StreamsFragment.newInstance() }

    fun People() = FragmentScreen(key = KEY_NAVIGATION_PEOPLE) { PeopleFragment.newInstance() }

    fun Profile(user: User? = null) = FragmentScreen(
        key = KEY_NAVIGATION_PROFILE,
        clearContainer = false
    ) { ProfileFragment.newInstance(user) }

    fun Chat(topic: Topic) = FragmentScreen { ChatFragment.newInstance(topic) }

    fun NewStreamForm() = FragmentScreen { CreateStreamFragment.newInstance() }
}