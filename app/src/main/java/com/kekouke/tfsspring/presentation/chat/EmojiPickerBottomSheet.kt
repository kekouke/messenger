package com.kekouke.tfsspring.presentation.chat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kekouke.tfsspring.presentation.chat.adapters.EmojiAdapter
import com.kekouke.tfsspring.databinding.BottomSheetEmojiPickerBinding
import com.kekouke.tfsspring.getSupportedReactions

class EmojiPickerBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetEmojiPickerBinding? = null
    private val binding: BottomSheetEmojiPickerBinding
        get() = _binding ?: throw RuntimeException("BottomSheetEmojiPickerBinding can't be null")

    private lateinit var chatListener: ChatListener
    private var messageId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        messageId = requireArguments().getInt(KEY_MESSAGE_ID)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val parent = parentFragment
        if (parent !is ChatListener) {
            throw RuntimeException("Parent should implement ChatListener interface")
        }

        chatListener = parent
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetEmojiPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvEmojiPicker.adapter = EmojiAdapter(getSupportedReactions()).apply {
            onEmojiClick = {
                chatListener.onEmojiClick(messageId, it)
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        const val TAG = "EmojiPickerBottomSheet"

        private const val KEY_MESSAGE_ID = "KEY_MESSAGE_ID"

        fun newInstance(messageId: Int) = EmojiPickerBottomSheet().apply {
            arguments = Bundle().apply {
                putInt(KEY_MESSAGE_ID, messageId)
            }
        }
    }
}