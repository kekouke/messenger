package com.kekouke.tfsspring.screen

import android.view.View
import com.kaspersky.kaspresso.screens.KScreen
import com.kekouke.tfsspring.presentation.chat.ChatFragment
import io.github.kakaocup.kakao.common.builders.ViewBuilder
import io.github.kakaocup.kakao.common.views.KBaseView
import io.github.kakaocup.kakao.common.views.KView
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.image.KImageView
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import io.github.kakaocup.kakao.text.KTextView
import io.github.kakaocup.kakao.toolbar.KToolbar
import org.hamcrest.Matcher
import com.kekouke.tfsspring.R as TfSpringR

object ChatFragmentScreen : KScreen<ChatFragmentScreen>() {
    override val layoutId: Int = TfSpringR.layout.fragment_chat
    override val viewClass: Class<*> = ChatFragment::class.java

    val tvTopicName = KTextView { withId(TfSpringR.id.tv_topic_name) }
    val toolbar = KToolbar { withId(TfSpringR.id.toolbar) }
    val etMessage = KEditText { withId(TfSpringR.id.et_message) }
    val btnSend = KImageView { withId(TfSpringR.id.btn_send) }
    val btnAttachContent = KImageView { withId(TfSpringR.id.btn_attach_content) }

    val networkErrorLayout = KView { withId(TfSpringR.id.layout_network_error) }

    val rvMessages = KRecyclerView(
        builder = { withId(TfSpringR.id.rv_messages) },
        itemTypeBuilder = {
            itemType(::KMessageItem)
            itemType(::KDateItem)
        }
    )

    class KDateItem(parent: Matcher<View>) : KRecyclerItem<KDateItem>(parent)

    class KMessageItem(parent: Matcher<View>) : KRecyclerItem<KMessageItem>(parent) {
        val fbReactions = KFlexBoxLayout(parent) { withId(TfSpringR.id.emojiContainer) }
    }

    class KFlexBoxLayout : KBaseView<KFlexBoxLayout> {
        constructor(function: ViewBuilder.() -> Unit) : super(function)

        constructor(parent: Matcher<View>, function: ViewBuilder.() -> Unit) : super(
            parent,
            function
        )
    }
}