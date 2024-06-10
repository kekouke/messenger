package com.kekouke.tfsspring.screen

import android.view.View
import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import org.hamcrest.Matcher
import com.kekouke.tfsspring.R as TfSpringR

object ReactionsBottomSheetScreen : KScreen<ReactionsBottomSheetScreen>() {
    override val layoutId: Int = TfSpringR.layout.bottom_sheet_emoji_picker
    override val viewClass: Class<*>?
        get() = null


    val rvEmojiPicker = KRecyclerView(
        builder = { withId(TfSpringR.id.rv_emoji_picker) },
        itemTypeBuilder = { itemType(::KReactionItem) }
    )

    class KReactionItem(parent: Matcher<View>) : KRecyclerItem<KReactionItem>(parent)
}