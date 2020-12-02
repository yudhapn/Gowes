package id.forum.community.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import id.forum.community.databinding.FragmentCommunityBinding
import id.forum.community.injectFeature
import id.forum.community.presentation.member.MemberOverviewAdapter
import id.forum.community.presentation.member.MemberOverviewAdapter.MemberAdapterListener
import id.forum.core.base.BaseFragment
import id.forum.core.community.domain.model.Community
import id.forum.core.data.Status
import id.forum.core.post.domain.model.AttachmentList
import id.forum.core.post.domain.model.Post
import id.forum.core.post.presentation.PostAdapter
import id.forum.core.post.presentation.PostAttachmentAdapter.AttachmentAdapterListener
import id.forum.core.user.domain.model.User
import id.forum.feed.presentation.HomeFragmentDirections
import id.forum.gowes.AppNavGraphDirections
import id.forum.gowes.MainActivity
import id.forum.gowes.R
import kotlinx.android.synthetic.main.survey_layout.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import id.forum.community.R as communityResource
import id.forum.core.R as coreResource

@ExperimentalCoroutinesApi
class CommunityFragment : BaseFragment(), MemberAdapterListener, AttachmentAdapterListener {
    private lateinit var binding: FragmentCommunityBinding
    private val memberAdapter = MemberOverviewAdapter(this)
    private val args: CommunityFragmentArgs by navArgs()
    val viewModel: CommunityViewModel by viewModel { parametersOf(args.community) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectFeature()
    }

    override fun provideFragmentView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommunityBinding.inflate(inflater, container, false).apply {
            community = args.community
            lifecycleOwner = viewLifecycleOwner
            currentUserId = currentUid
            postIcon =
                ContextCompat.getDrawable(requireContext(), coreResource.drawable.ic_post)
            communityIcon =
                ContextCompat.getDrawable(requireContext(), coreResource.drawable.ic_community)
            privateIcon =
                ContextCompat.getDrawable(requireContext(), coreResource.drawable.ic_private)

        }
        return binding.root
    }

    override fun setProfileClickDirections(post: Post): NavDirections =
        CommunityFragmentDirections.actionToProfile(post.user)

    override fun setCommunityClickDirections(post: Post) =
        HomeFragmentDirections.actionToCommunity(Community(profile = post.community.profile))

    override fun setPostClickDirections(post: Post) =
        CommunityFragmentDirections.actionToPost(post)

    override fun updatePost(post: Post) = viewModel.deletePost(post)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            communityViewModel = viewModel
            memberRecyclerView.apply { adapter = memberAdapter }
            initPostsRecyclerView()
            (requireActivity() as MainActivity).apply {
                findViewById<TextView>(R.id.bottom_app_bar_title).text = args.community.profile.name
            }
            setupViewListener(viewModel)
            refreshLayout.setOnRefreshListener { viewModel.refreshData() }
            viewModel.community.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        refreshLayout.isRefreshing = false
                        community = it.data
                    }
                    Status.ERROR -> {
                        refreshLayout.isRefreshing = false
                        accountViewModel.showSnackBar(it.message.toString())
                    }
                    else -> refreshLayout.isRefreshing = true
                }
            })

        }
    }

    private fun setupViewListener(viewModel: CommunityViewModel) {
        binding.apply {
            btnEdit.setOnClickListener {
                findNavController().navigate(
                    CommunityFragmentDirections.actionToEditCommunity(
                        viewModel.community.value?.data ?: args.community
                    )
                )
            }
            btnCommunity.joinButton.setOnClickListener {
                it.isEnabled = false
                viewModel.joinCommunity()
                accountViewModel.updateCurrentUser(viewModel.currentUser)

            }
            btnCommunity.requestButton.setOnClickListener {
                val dialogView = LayoutInflater.from(requireContext())
                    .inflate(communityResource.layout.survey_layout, null)
                MaterialAlertDialogBuilder(requireContext())
                    .setView(dialogView)
                    .setMessage(community?.surveyQuestion ?: "")
                    .setNegativeButton(resources.getString(R.string.cancel_button)) { _, _ ->
                    }
                    .setPositiveButton(resources.getString(R.string.request_button)) { _, _ ->
                        val answer = dialogView.et_answer.text.toString()
                        accountViewModel.showSnackBar(answer)
                        viewModel.joinCommunity(answer)
                    }
                    .show()
            }
            btnCommunity.cancelButton.setOnClickListener { viewModel.cancelRequestJoinCommunity() }
            btnCommunity.leaveButton.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(resources.getString(R.string.leave_community))
                    .setNegativeButton(resources.getString(R.string.cancel_button)) { _, _ ->
                    }
                    .setPositiveButton(resources.getString(R.string.leave_button)) { _, _ ->
                        it.isEnabled = false
                        val result = viewModel.leaveCommunity()
                        if (!result) {
                            accountViewModel.showSnackBar(getString(coreResource.string.transfer_leader_position))
                            it.isEnabled = true
                        }
                    }
                    .show()
            }
        }
    }

    override fun onMemberClicked(cardView: View, user: User) {
        val action = CommunityFragmentDirections.actionToProfile(user)
        findNavController().navigate(action)
    }

    private fun initPostsRecyclerView() {
        viewModel.isAdmin.observe(viewLifecycleOwner, Observer { isAdmin ->
            val adapter = PostAdapter(this, this, super.currentUid, isAdmin)
            binding.recyclerView.adapter = adapter
            viewModel.community.observe(viewLifecycleOwner, {
                adapter.submitList(it.data?.posts)
            })
            binding.btnShowAllMember.showTextView.setOnClickListener {
                val action =
                    if (isAdmin) {
                        CommunityFragmentDirections.actionToMember(
                            viewModel.community.value?.data ?: args.community
                        )
                    } else {
                        CommunityFragmentDirections.actionToMemberList(
                            viewModel.community.value?.data ?: args.community
                        )
                    }
                findNavController().navigate(action)
            }
        })
    }

    override fun onImageClicked(imageView: View, attachments: AttachmentList, position: Int) {
        val action = AppNavGraphDirections.actionGlobalMediaPreview(attachments, position)
        findNavController().navigate(action)
    }
}
