package id.forum.search.presentation.post

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import id.forum.core.base.BaseFragment
import id.forum.core.post.domain.model.AttachmentList
import id.forum.core.post.domain.model.Post
import id.forum.core.post.presentation.PostAdapter
import id.forum.core.post.presentation.PostAttachmentAdapter.AttachmentAdapterListener
import id.forum.explore.presentation.ExploreFragmentDirections
import id.forum.gowes.AppNavGraphDirections
import id.forum.search.databinding.FragmentPostListBinding
import id.forum.search.presentation.SearchViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

@ExperimentalCoroutinesApi
class PostsFragment : BaseFragment(), AttachmentAdapterListener {
    private lateinit var binding: FragmentPostListBinding
    private val viewModel: SearchViewModel by sharedViewModel()

    override fun provideFragmentView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setProfileClickDirections(post: Post): NavDirections =
        ExploreFragmentDirections.actionToProfile(post.user)

    override fun setCommunityClickDirections(post: Post) =
        ExploreFragmentDirections.actionToCommunity(post.community)

    override fun setPostClickDirections(post: Post) =
        ExploreFragmentDirections.actionToPost(post)

    override fun updatePost(post: Post) = viewModel.deletePost(post)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = this@PostsFragment
            searchViewModel = viewModel
            recyclerView.apply {
                setItemViewCacheSize(20)
                adapter = PostAdapter(this@PostsFragment, this@PostsFragment, currentUid)
            }
            accountViewModel.keyword.observe(viewLifecycleOwner, Observer { keyword ->
                if (keyword.isNotBlank()) {
                    viewModel.searchPosts(keyword)
                }
            })
            viewModel.posts.observe(viewLifecycleOwner, Observer {
                accountViewModel.setCurrentState(it.status)
            })
        }
    }

    override fun onImageClicked(imageView: View, attachments: AttachmentList, position: Int) {
        val action = AppNavGraphDirections.actionGlobalMediaPreview(attachments, position)
        findNavController().navigate(action)
    }
}
