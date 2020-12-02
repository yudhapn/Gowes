package id.forum.feed.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import id.forum.core.base.BaseFragment
import id.forum.core.data.Status
import id.forum.core.post.domain.model.AttachmentList
import id.forum.core.post.domain.model.Post
import id.forum.core.post.presentation.PostAdapter
import id.forum.core.post.presentation.PostAttachmentAdapter.AttachmentAdapterListener
import id.forum.feed.databinding.FragmentHomeBinding
import id.forum.feed.injectFeature
import id.forum.gowes.AppNavGraphDirections
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.viewModel

@FlowPreview
@ExperimentalCoroutinesApi
class HomeFragment : BaseFragment(), AttachmentAdapterListener {
    private lateinit var binding: FragmentHomeBinding
    private val _viewModel: HomeViewModel by viewModel()
    override fun provideFragmentView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@HomeFragment
            viewModel = _viewModel
        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectFeature()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViewAdapter()
        updateUi()
    }

    private fun setupRecyclerViewAdapter() {
        binding.recyclerView.apply {
            setItemViewCacheSize(20)
            adapter = PostAdapter(this@HomeFragment, this@HomeFragment, currentUid)
        }
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun updateUi() {
        binding.apply {
            accountViewModel.userAccount.observe(requireActivity(), {
                it.data?.let { user ->
                    Log.d("HomeFragment", "current user is: $user")
                    if (user.id.isNotBlank()) {
                        if (_viewModel.currentUser.id.isBlank()) {
                            Log.d("HomeFragment", "current user in homeViewModel is: $user")
                            _viewModel.getPostsByCommunity(user)
                        }
                        refreshLayout.setOnRefreshListener { _viewModel.refreshHome(user) }
                        communitySize = user.communities.size
                    }
                }
            })

            btnJoin.joinButton.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.actionToCommunityList())
            }
            _viewModel.posts.observe(viewLifecycleOwner, {
                when (it.status) {
                    Status.SUCCESS -> refreshLayout.isRefreshing = false
                    Status.ERROR -> {
                        refreshLayout.isRefreshing = false
                        accountViewModel.showSnackBar(it.message.toString())
                    }
                    else -> Unit
                }
            })
        }
    }

    override fun setProfileClickDirections(post: Post): NavDirections =
        HomeFragmentDirections.actionToProfile(post.user)

    override fun setCommunityClickDirections(post: Post) =
        HomeFragmentDirections.actionToCommunity(post.community)

    override fun setPostClickDirections(post: Post) =
        HomeFragmentDirections.actionToPost(post)

    override fun updatePost(post: Post) = _viewModel.deletePost(post)

    override fun onImageClicked(imageView: View, attachments: AttachmentList, position: Int) {
        val action = AppNavGraphDirections.actionGlobalMediaPreview(attachments, position)
        findNavController().navigate(action)
    }
}