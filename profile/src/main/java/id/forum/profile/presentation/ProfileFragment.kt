package id.forum.profile.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import id.forum.core.R
import id.forum.core.base.BaseFragment
import id.forum.core.community.domain.model.Community
import id.forum.core.community.presentation.CommunityAdapter
import id.forum.core.community.presentation.CommunityAdapter.CommunityAdapterListener
import id.forum.core.data.Status.ERROR
import id.forum.core.data.Status.SUCCESS
import id.forum.core.media.domain.model.MediaList
import id.forum.core.post.domain.model.AttachmentList
import id.forum.core.post.domain.model.Post
import id.forum.core.post.presentation.PostAdapter
import id.forum.core.post.presentation.PostAttachmentAdapter.AttachmentAdapterListener
import id.forum.core.util.VH_TYPE_PROFILE
import id.forum.gowes.AppNavGraphDirections
import id.forum.gowes.MainActivity
import id.forum.post.presentation.create.CreatePostFragmentDirections
import id.forum.profile.databinding.FragmentProfileBinding
import id.forum.profile.injectFeature
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import id.forum.gowes.R as appResource

@ExperimentalCoroutinesApi
class ProfileFragment : BaseFragment(), CommunityAdapterListener, AttachmentAdapterListener {
    private val args: ProfileFragmentArgs by navArgs()
    private lateinit var binding: FragmentProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectFeature()
    }

    override fun provideFragmentView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val profileViewModel: ProfileViewModel by viewModel { parametersOf(args.user) }
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            user = args.user
            currentUserId = currentUid
            postIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_post)
            communityIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_community)
            this.viewModel = profileViewModel
            initRecyclerView()
            setFab(args.user.id, profileViewModel)
            refreshLayout.setOnRefreshListener { profileViewModel.refreshData() }
            profileViewModel.usr.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    SUCCESS -> {
                        refreshLayout.isRefreshing = false
                        user = it.data
                    }
                    ERROR -> {
                        refreshLayout.isRefreshing = false
                        accountViewModel.showSnackBar(it.message.toString())
                    }
                    else -> refreshLayout.isRefreshing = true
                }
            })

            editButton.setOnClickListener {
                findNavController().navigate(
                    ProfileFragmentDirections.actionToEditProfile(
                        profileViewModel.usr.value?.data ?: args.user
                    )
                )
            }
        }
    }

    private fun setFab(userProfileId: String, viewModel: ProfileViewModel) {
        (requireActivity() as MainActivity).findViewById<FloatingActionButton>(appResource.id.fab)
            .apply {
                if (userProfileId == super.currentUid) {
                    setOnClickListener {
                        findNavController()
                            .navigate(CreatePostFragmentDirections.actionGlobalCompose(MediaList()))
                    }
                    setImageResource(R.drawable.ic_edit)
                } else {
                    viewModel.chatId.observe(viewLifecycleOwner, Observer { chatId ->
                        Log.d("profileFrag", "chatId: ${chatId}")
                        setOnClickListener {
                            chatId.data?.let {
                                val id = if (it.isNotEmpty()) it else "new_chat"
                                findNavController().navigate(
                                    ProfileFragmentDirections.actionToChatRoom(
                                        id, args.user, super.currentUid
                                    )
                                )
                            } ?: run {accountViewModel.showSnackBar("The page is not fully loaded please wait until it loads")}

                        }

                    })
                    setImageResource(R.drawable.ic_message)
                }
            }
    }

    override fun setProfileClickDirections(post: Post): NavDirections =
        ProfileFragmentDirections.actionToProfileSelf(post.user)

    override fun setCommunityClickDirections(post: Post): NavDirections =
        ProfileFragmentDirections.actionToCommunity(post.community)

    override fun setPostClickDirections(post: Post): NavDirections =
        ProfileFragmentDirections.actionToPost(post)

    override fun updatePost(post: Post) = Unit

    //
    override fun onCommunityClicked(cardView: View, community: Community) =
        findNavController().navigate(ProfileFragmentDirections.actionToCommunity(community))

    private fun initRecyclerView() {
        binding.recyclerView.adapter =
            PostAdapter(this@ProfileFragment, this@ProfileFragment, super.currentUid)
        binding.communityRecyclerView.adapter =
            CommunityAdapter(this@ProfileFragment, VH_TYPE_PROFILE)
    }

    override fun onImageClicked(imageView: View, attachments: AttachmentList, position: Int) {
        val action = AppNavGraphDirections.actionGlobalMediaPreview(attachments, position)
        findNavController().navigate(action)
    }
}
