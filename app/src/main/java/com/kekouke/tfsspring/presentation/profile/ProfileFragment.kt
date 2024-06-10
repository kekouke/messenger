package com.kekouke.tfsspring.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.BundleCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.kekouke.tfsspring.appComponent
import com.kekouke.tfsspring.databinding.FragmentProfileBinding
import com.kekouke.tfsspring.di.components.DaggerProfileComponent
import com.kekouke.tfsspring.domain.model.Presence
import com.kekouke.tfsspring.domain.model.User
import com.kekouke.tfsspring.hide
import com.kekouke.tfsspring.presentation.profile.tea.ProfileEvent.UI.DisplayUser
import com.kekouke.tfsspring.presentation.profile.tea.ProfileEvent.UI.NavigateBack
import com.kekouke.tfsspring.presentation.profile.tea.ProfileStoreFactory
import com.kekouke.tfsspring.show
import kotlinx.coroutines.launch
import ru.tinkoff.kotea.android.storeViaViewModel
import javax.inject.Inject
import com.kekouke.tfsspring.R as TfSpringR

class ProfileFragment : Fragment() {

    @Inject
    lateinit var storeFactory: ProfileStoreFactory

    private val store by storeViaViewModel {
        storeFactory.create()
    }

    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding
        get() = _binding ?: throw RuntimeException("FragmentProfileBinding can't be null")

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerProfileComponent.factory().create(requireContext().appComponent()).inject(this)

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addListeners()
        observeStore()
    }

    private fun addListeners() {
        binding.layoutNetworkError.btnRetry.setOnClickListener {
            store.dispatch(DisplayUser())
        }

        binding.toolbar.setNavigationOnClickListener {
            store.dispatch(NavigateBack)
        }
    }

    private fun observeStore() {
        lifecycleScope.launch {
            store.state
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect(::render)
        }
    }

    private fun render(state: ProfileState) = when (state) {
        is ProfileState.Initial -> {
            val user = getUserFromArgs()
            store.dispatch(DisplayUser(user))
        }

        is ProfileState.Loading -> {
            binding.layoutNetworkError.root.isVisible = false
            binding.shimmer.show()
        }

        is ProfileState.Content -> {
            binding.shimmer.hide()
            fillUiWithUser(state.user, state.isOwnUser)
        }

        is ProfileState.Error -> {
            binding.layoutNetworkError.root.isVisible = true
            binding.shimmer.hide()
        }
    }

    private fun getUserFromArgs() = BundleCompat.getParcelable(
        requireArguments(),
        KEY_USER,
        User::class.java
    )

    private fun fillUiWithUser(user: User, isOwnUser: Boolean) {
        binding.avatar.isVisible = true
        binding.toolbar.isVisible = !isOwnUser

        Glide.with(requireContext())
            .load(user.avatarUrl)
            .placeholder(TfSpringR.drawable.placeholder_avatar)
            .error(TfSpringR.drawable.placeholder_avatar)
            .into(binding.ivAvatar)

        binding.tvUsername.text = user.name
        binding.tvAction.text = user.presence.toString()
        binding.tvEmail.text = user.email
        binding.tvEmail.isSelected = true

        val colorResId = when (user.presence) {
            Presence.Active -> TfSpringR.color.online_status
            Presence.Idle -> android.R.color.holo_orange_light
            Presence.Offline -> android.R.color.darker_gray
        }
        val color = ContextCompat.getColor(requireContext(), colorResId)
        binding.tvAction.setTextColor(color)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val KEY_USER = "key_user"

        fun newInstance(user: User?) = ProfileFragment().apply {
            arguments = Bundle().apply {
                user?.let { putParcelable(KEY_USER, user) }
            }
        }
    }

}