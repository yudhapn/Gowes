package id.forum.post.presentation.create

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import id.forum.core.R
import id.forum.core.account.presentation.UserAccountViewModel
import id.forum.core.data.Status.ERROR
import id.forum.core.data.Status.SUCCESS
import id.forum.core.media.domain.model.Image
import id.forum.core.media.domain.model.MediaList
import id.forum.core.user.domain.model.User
import id.forum.core.util.*
import id.forum.gallery.presentation.GalleryAdapter
import id.forum.gallery.presentation.GalleryAdapter.GalleryAdapterListener
import id.forum.post.databinding.FragmentCreatePostBinding
import id.forum.post.injectFeature
import id.forum.post.presentation.SpinnerAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
class CreatePostFragment : Fragment(), GalleryAdapterListener {
    private lateinit var binding: FragmentCreatePostBinding
    private val viewModel: CreatePostViewModel by viewModel()
    private val accountViewModel: UserAccountViewModel by sharedViewModel()
    private val args: CreatePostFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectFeature()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedPref = requireContext().getSharedPreferences(SHARED_TKN, Context.MODE_PRIVATE)
        val user = viewModel.getCurrentUser()
        haveJoinCommunity(user)
        val mediaList = args.mediaList ?: MediaList()
        val images = mediaList.mediaList
        setupViewListener(mediaList, user)
        setupViewAdapter(images, user)
        binding.run {
            etTitle.requestFocus()
            requireContext().showSoftKeyboard(etTitle)
            etTitle.setText(sharedPref.getString(POST_SUBJECT, ""))
            etBody.setText(sharedPref.getString(POST_BODY, ""))

            viewModel.prepareMedia(images).observe(viewLifecycleOwner, Observer {
                responseType = it
                when (it.status) {
                    SUCCESS -> mediaList.mediaList = it.data ?: emptyList()
                    ERROR -> Unit
                    else -> Unit
                }
            })
        }
    }

    private fun setupViewAdapter(images: List<Image>, user: User) {
        binding.apply {
            senderSpinner.adapter = SpinnerAdapter(
                requireContext(),
                user.communities.map { it.profile.name },
                user.communities.map { it.profile.avatar }
            )
            rvThreadImage.apply {
                setHasFixedSize(true)
                adapter = GalleryAdapter(this@CreatePostFragment, VH_TYPE_COMPOSE).apply {
                    submitList(images)
                }
            }
        }

    }

    private fun setupViewListener(mediaList: MediaList, user: User) {
        binding.apply {
            btnGallery.setOnClickListener {
                requireContext().hideSoftKeyboard(requireActivity().currentFocus?.windowToken)
                requireContext().getSharedPreferences(SHARED_TKN, Context.MODE_PRIVATE).edit {
                    putString(POST_SUBJECT, etTitle.text.toString())
                    putString(POST_BODY, etBody.text.toString())
                }
                findNavController().navigate(
                    CreatePostFragmentDirections.actionToImageGallery(mediaList)
                )
            }

            btnClose.setOnClickListener { findNavController().navigateUp() }
            btnCreate.setOnClickListener { handleCreate(mediaList, user) }
        }
    }

    private fun handleCreate(mediaList: MediaList, user: User) {
        binding.apply {
            requireContext().hideSoftKeyboard(requireActivity().currentFocus?.windowToken)
            val subject = etTitle.text.toString()
            val subjectValid = subject.isTextValid()
                .also { valid ->
                    etTitle.requestFocus()
                    requireContext().showSoftKeyboard(etTitle)
                    etTitle.setErrorMessage(if (!valid) getString(R.string.error_subject) else null)
                }
            if (subjectValid) {
                val body = etBody.text.toString()
                val community =
                    user.communities.find { it.profile.name == senderSpinner.selectedItem.toString() }

                viewModel.createPost(user, community, subject, body, mediaList.mediaList)
                    .observe(viewLifecycleOwner, Observer {
                        accountViewModel.setCurrentState(it.status)
                        when (it.status) {
                            SUCCESS -> {
                                findNavController().navigate(CreatePostFragmentDirections.actionToHome())
                            }
                            ERROR -> {
                                requireContext().getSharedPreferences(
                                    SHARED_TKN, Context.MODE_PRIVATE
                                ).edit {
                                    putString(POST_SUBJECT, "")
                                    putString(POST_BODY, "")
                                }
                                accountViewModel.showSnackBar(it.message.toString())
                            }
                            else -> Unit
                        }
                    })
            }
        }
    }

    private fun haveJoinCommunity(user: User) {
        if (user.communities.isEmpty()) {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(resources.getString(R.string.join_community_yet))
                .setCancelable(false)
                .setNegativeButton(resources.getString(R.string.cancel_button)) { _, _ ->
                    findNavController().navigateUp()
                }
                .setPositiveButton(resources.getString(R.string.join_button)) { _, _ ->
                    findNavController().navigate(CreatePostFragmentDirections.actionToCommunityList())
                }
                .show()
        }
    }

    override fun onImageClicked(cardView: View, media: Image) = Unit
    override fun onRemoveClicked(cardView: View, media: Image) = Unit

    override fun onDestroy() {
        super.onDestroy()
        requireContext().hideSoftKeyboard(requireActivity().currentFocus?.windowToken)
    }
}
