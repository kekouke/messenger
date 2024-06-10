package com.kekouke.tfsspring

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kekouke.tfsspring.MockServer.Companion.stubWith
import com.kekouke.tfsspring.domain.model.Topic
import com.kekouke.tfsspring.presentation.chat.ChatFragment
import com.kekouke.tfsspring.screen.ChatFragmentScreen
import com.kekouke.tfsspring.screen.ReactionsBottomSheetScreen
import io.github.kakaocup.kakao.common.utilities.getResourceString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.kekouke.tfsspring.R as TfSpringR

private const val EXTRA_TOPIC = "extra_topic"

@RunWith(AndroidJUnit4::class)
class ChatFragmentTest : TestCase() {

    private val topic = Topic("test topic", 1010, "general")

    @get:Rule
    val wireMockRule = WireMockRule()

    @Test
    fun stream_and_topic_names_are_displayed() = run {
        wireMockRule.stubWith { withEmptyMessagesList() }

        launchFragmentInContainer<ChatFragment>(
            bundleOf(EXTRA_TOPIC to topic),
            themeResId = TfSpringR.style.Theme_Tfs_spring_2024
        )

        ChatFragmentScreen {
            val topicName = String.format(
                getResourceString(TfSpringR.string.topic_name_placeholder),
                topic.name
            )

            toolbar.isVisible()
            tvTopicName.isVisible()
            toolbar.hasTitle(topic.streamName)
            tvTopicName.hasText(topicName)
        }
    }

    @Test
    fun show_the_correct_buttons_depending_on_the_message_input_field() = run {
        wireMockRule.stubWith { withEmptyMessagesList() }

        launchFragmentInContainer<ChatFragment>(
            bundleOf(EXTRA_TOPIC to topic),
            themeResId = TfSpringR.style.Theme_Tfs_spring_2024
        )

        ChatFragmentScreen {
            step("Show attach content button on empty input") {
                etMessage.replaceText("")
                btnSend.isGone()
                btnAttachContent.isVisible()
            }

            step("Show send message button when input is not empty") {
                etMessage.replaceText("some text")
                btnSend.isVisible()
                btnAttachContent.isGone()
            }
        }
    }

    @Test
    fun recycler_for_messages_is_visible() = run {
        wireMockRule.stubWith { withEmptyMessagesList() }

        launchFragmentInContainer<ChatFragment>(
            bundleOf(EXTRA_TOPIC to topic),
            themeResId = TfSpringR.style.Theme_Tfs_spring_2024
        )

        ChatFragmentScreen.rvMessages.isDisplayed()
    }

    @Test
    fun display_emoji_picker_on_long_click() = run {
        wireMockRule.stubWith { withOneMessageList() }

        launchFragmentInContainer<ChatFragment>(
            bundleOf("extra_topic" to topic),
            themeResId = TfSpringR.style.Theme_Tfs_spring_2024
        )

        step("Perform long click on message") {
            ChatFragmentScreen {
                rvMessages.lastChild<ChatFragmentScreen.KMessageItem> { longClick() }
            }
        }

        step("Checking that emoji picker is displayed") {
            ReactionsBottomSheetScreen {
                rvEmojiPicker.isDisplayed()
            }
        }
    }

    @Test
    fun display_layout_network_error() = run {
        wireMockRule.stubWith { withErrorOnLoadMessages() }

        launchFragmentInContainer<ChatFragment>(
            bundleOf("extra_topic" to topic),
            themeResId = TfSpringR.style.Theme_Tfs_spring_2024
        )

        ChatFragmentScreen {
            rvMessages.isGone()
            networkErrorLayout.isVisible()
        }
    }
}