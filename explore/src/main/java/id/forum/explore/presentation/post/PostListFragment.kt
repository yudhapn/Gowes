package id.forum.explore.presentation.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import id.forum.core.R
import id.forum.core.base.BaseLoadStateAdapter
import id.forum.core.base.BasePagingFragment
import id.forum.core.post.domain.model.AttachmentList
import id.forum.core.post.domain.model.Post
import id.forum.core.post.presentation.PostAttachmentAdapter.AttachmentAdapterListener
import id.forum.core.post.presentation.PostPagingAdapter
import id.forum.core.post.presentation.PostUiModel
import id.forum.explore.databinding.FragmentPostsBinding
import id.forum.explore.presentation.ExploreFragmentDirections
import id.forum.explore.presentation.ExploreViewModel
import id.forum.gowes.AppNavGraphDirections
import id.forum.gowes.MenuBottomSheetDialogFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@ExperimentalCoroutinesApi
class PostListFragment : BasePagingFragment(), AttachmentAdapterListener {
    private lateinit var binding: FragmentPostsBinding
    private val viewModel: ExploreViewModel by viewModel { parametersOf(0) }
    private var job: Job? = null

    override fun provideFragmentView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostsBinding.inflate(inflater, container, false)
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
            lifecycleOwner = this@PostListFragment
            exploreViewModel = viewModel
            initAdapter()
//            refreshLayout.setOnRefreshListener { viewModel.refreshPostData() }
            refreshLayout.isEnabled = false
            categoryButton.setOnClickListener {
                MenuBottomSheetDialogFragment(R.menu.category_bottom_sheet_menu) {
                    var category = ""
                    var iconStart = 0
                    val iconEnd = R.drawable.ic_arrow_down
                    when (it.itemId) {
                        R.id.menu_top_post -> {
                            category = getString(R.string.menu_item_top_post)
                            iconStart = R.drawable.ic_hot
                        }
                        else -> {
                            category = getString(R.string.menu_item_newest_post)
                            iconStart = R.drawable.ic_new
                        }
                    }
                    categoryButton.setCompoundDrawablesWithIntrinsicBounds(iconStart, 0, iconEnd, 0)
                    categoryButton.text = category
                    true
                }.show(requireFragmentManager(), null)
            }
        }
    }

    override fun onImageClicked(imageView: View, attachments: AttachmentList, position: Int) {
        val action = AppNavGraphDirections.actionGlobalMediaPreview(attachments, position)
        findNavController().navigate(action)
    }

    private fun initAdapter() {
        binding.apply {

            recyclerView.apply {
                val postAdapter =
                    PostPagingAdapter(this@PostListFragment, this@PostListFragment, currentUid)

                adapter = postAdapter.withLoadStateHeaderAndFooter(
                    header = BaseLoadStateAdapter { postAdapter.retry() },
                    footer = BaseLoadStateAdapter { postAdapter.retry() }
                )

                job?.cancel()
                job = lifecycleScope.launch {
                    viewModel.postsPaging?.collectLatest {
//                        val list = it.insertHeaderItem(PostUiModel.SeparatorItem("THIS MONTH"))
                        postAdapter.submitData(it)
                    }
                    postAdapter.loadStateFlow
                        .distinctUntilChangedBy { it.refresh }
                        .filter { it.refresh is LoadState.NotLoading }
                        .collect { binding.recyclerView.scrollToPosition(0) }
                }
                postAdapter.addLoadStateListener { loadState ->
                    // Only show the list if refresh succeeds.
                    binding.recyclerView.isVisible =
                        loadState.source.refresh is LoadState.NotLoading
                    // Show loading spinner during initial load or refresh.
                    binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                    // Show the retry state if initial load or refresh fails.
                    binding.retryButton.isVisible = loadState.source.refresh is LoadState.Error

                    // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
                    val errorState = loadState.source.append as? LoadState.Error
                        ?: loadState.source.prepend as? LoadState.Error
                        ?: loadState.append as? LoadState.Error
                        ?: loadState.prepend as? LoadState.Error
                    errorState?.let {
                        Toast.makeText(
                            requireContext(),
                            "\uD83D\uDE28 Wooops ${it.error}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                retryButton.setOnClickListener { postAdapter.retry() }
            }
        }
    }
}
