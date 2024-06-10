package com.kekouke.tfsspring

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.facebook.shimmer.ShimmerFrameLayout
import com.kekouke.tfsspring.presentation.chat.adapters.delegates.DateDelegateItem
import com.kekouke.tfsspring.presentation.chat.adapters.delegates.DelegateItem
import com.kekouke.tfsspring.presentation.chat.adapters.delegates.MessageDelegateItem
import com.kekouke.tfsspring.domain.model.Date
import com.kekouke.tfsspring.domain.model.Message
import com.kekouke.tfsspring.domain.model.Stream
import com.kekouke.tfsspring.presentation.streams.tab.recycler.StreamDelegateItem
import com.kekouke.tfsspring.presentation.streams.tab.recycler.TopicDelegateItem
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.CancellationException

fun EditText.doOnTextChanged(action: (String) -> Unit) =
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable) {
            action.invoke(s.toString())
        }
    })

fun SearchView.doOnQueryTextChange(action: (String) -> Unit) =
    setOnQueryTextListener(object : SearchView.OnQueryTextListener {

        // This flag is necessary as the SearchView calls "onQueryTextChange" method
        // upon gaining focus first time.
        // We don't want to consider this call as getting a new query.
        private var initialized = false

        override fun onQueryTextSubmit(query: String) = false

        override fun onQueryTextChange(newText: String): Boolean {
            if (initialized) {
                action(newText)
            }

            initialized = true

            return true
        }
    })

private val dateFormatter = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())

fun List<Message>.concatenateWithDate(): List<DelegateItem> {
    val result = mutableListOf<DelegateItem>()
    var lastDate = ""

    this.map { message ->
        val currentDate = dateFormatter.format(java.util.Date(message.timestampMilliseconds))
        if (currentDate != lastDate) {
            lastDate = currentDate
            result.add(DateDelegateItem(Date(message.id, currentDate)))
        }
        result.add(MessageDelegateItem(message))
    }

    return result
}

fun List<Stream>.concatenateWithTopics(): List<DelegateItem> = flatMap { stream ->
    mutableListOf<DelegateItem>(StreamDelegateItem(stream)).apply {
        if (stream.expanded) {
            stream.topics?.mapTo(this) { TopicDelegateItem(it) }
        }
    }
}

inline fun <T> runCatchingNonCancellation(block: () -> T): Result<T> = try {
    Result.success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    Result.failure(e)
}

fun ShimmerFrameLayout.show() {
    isVisible = true
    startShimmer()
}

fun ShimmerFrameLayout.hide() {
    stopShimmer()
    isVisible = false
}

fun Context.appComponent() = (applicationContext as App).appComponent

fun Fragment.showShortToast(message: String) = Toast.makeText(
    requireContext(),
    message,
    Toast.LENGTH_SHORT
).show()