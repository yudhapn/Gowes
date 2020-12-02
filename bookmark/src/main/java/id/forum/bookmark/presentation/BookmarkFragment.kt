package id.forum.bookmark.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import id.forum.bookmark.databinding.FragmentBookmarkBinding
import id.forum.bookmark.injectFeature
import id.forum.core.base.BaseFragment
import id.forum.core.data.Status.*
import id.forum.core.post.domain.model.AttachmentList
import id.forum.core.post.domain.model.Post
import id.forum.core.post.presentation.PostAdapter
import id.forum.core.post.presentation.PostAttachmentAdapter.AttachmentAdapterListener
import id.forum.gowes.AppNavGraphDirections
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
class BookmarkFragment : BaseFragment(), AttachmentAdapterListener {
    private lateinit var binding: FragmentBookmarkBinding
    private val bookmarkViewModel: BookmarkViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectFeature()
    }

    override fun provideFragmentView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setProfileClickDirections(post: Post): NavDirections =
        BookmarkFragmentDirections.actionToProfile(post.user)

    override fun setCommunityClickDirections(post: Post) =
        BookmarkFragmentDirections.actionToCommunity(post.community)

    override fun setPostClickDirections(post: Post) =
        BookmarkFragmentDirections.actionToPost(post)

    override fun updatePost(post: Post) = Unit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = this@BookmarkFragment
            viewModel = bookmarkViewModel
            recyclerView.apply {
                setItemViewCacheSize(20)
                adapter = PostAdapter(this@BookmarkFragment, this@BookmarkFragment, currentUid)
            }
            binding.refreshLayout.setOnRefreshListener {
                bookmarkViewModel.refreshBookmark()
            }
            bookmarkViewModel.posts.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    SUCCESS -> refreshLayout.isRefreshing = false
                    ERROR -> {
                        refreshLayout.isRefreshing = false
                        accountViewModel.showSnackBar(it.message.toString())
                    }
                    else -> Unit
                }
            })
        }
    }

    override fun onImageClicked(imageView: View, attachments: AttachmentList, position: Int) {
        val action = AppNavGraphDirections.actionGlobalMediaPreview(attachments, position)
        findNavController().navigate(action)
    }

    override fun onBookmarkClicked(imageView: ImageView, post: Post, isBookmarked: Boolean): Float {
        // can only delete bookmark because when it's deleted it can't bookmarked again
        bookmarkViewModel.deleteBookmarkPost(post)
        return super.onBookmarkClicked(imageView, post, isBookmarked)
    }
}
