package id.forum.post.presentation

import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import id.forum.core.base.BaseFragment
import id.forum.core.data.Status.ERROR
import id.forum.core.post.domain.model.AttachmentList
import id.forum.core.post.domain.model.Comment
import id.forum.core.post.domain.model.Post
import id.forum.core.post.presentation.PostAttachmentAdapter.AttachmentAdapterListener
import id.forum.core.post.presentation.PostAttachmentGridAdapter
import id.forum.core.util.FROM_FRAGMENT_COMMENT
import id.forum.feed.presentation.HomeFragmentDirections
import id.forum.gowes.MainActivity
import id.forum.gowes.MenuBottomSheetDialogFragment
import id.forum.gowes.R
import id.forum.post.COMMENT_SORT_BY_UPDATED_ON
import id.forum.post.COMMENT_SORT_BY_VOTE_COUNT
import id.forum.post.databinding.FragmentPostBinding
import id.forum.post.injectFeature
import id.forum.post.presentation.CommentAdapter.CommentAdapterListener
import id.forum.post.presentation.comment.CreateCommentFragmentDirections
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@ExperimentalCoroutinesApi
class PostFragment : BaseFragment(), CommentAdapterListener,
    AttachmentAdapterListener {
    private val args: PostFragmentArgs by navArgs()
    private lateinit var binding: FragmentPostBinding
    private val attachmentAdapter = PostAttachmentGridAdapter(this)
    private val postViewModel: PostViewModel by viewModel { parametersOf(args.post) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectFeature()
    }

    override fun provideFragmentView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@PostFragment
            this.viewModel = postViewModel
            listener = this@PostFragment
            currentUserId = currentUid
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            postViewModel.post.observe(viewLifecycleOwner, Observer {
                post = it.data
            })

            setupFab(args.post, postViewModel)
            val isBookmarked = args.post.isBookmarked

            bookmarkImageView.setImageResource(
                if (isBookmarked) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark
            )

            val interpolation = if (isBookmarked) 1F else 0F
            emailCardView.progress = interpolation
            setupClickListener()
            attachmentRecyclerView.adapter = attachmentAdapter
            attachmentAdapter.submitList(args.post.attachments)
            replyRecyclerView.adapter = CommentAdapter(
                this@PostFragment,
                currentUid
            )
            refreshLayout.setOnRefreshListener { postViewModel.refreshData() }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                bodyTextView.justificationMode = JUSTIFICATION_MODE_INTER_WORD
            }
        }
    }

    private fun setupClickListener() {
        binding.apply {
            upvoteTextView.setOnClickListener {
                this@PostFragment.onUpVoteClicked(
                    upvoteTextView, downvoteTextView, voteAmountTextView, args.post
                )
            }
            downvoteTextView.setOnClickListener {
                this@PostFragment.onDownVoteClicked(
                    downvoteTextView, upvoteTextView, voteAmountTextView, args.post
                )
            }
            bookmarkImageView.setOnClickListener {
                this@PostFragment.onBookmarkClicked(
                    bookmarkImageView,
                    args.post,
                    args.post.isBookmarked
                )

            }
            categoryButton.setOnClickListener {
                MenuBottomSheetDialogFragment(R.menu.category_comment_bottom_sheet_menu) {
                    val iconEnd = R.drawable.ic_arrow_down
                    val iconStart = when (it.itemId) {
                        R.id.menu_top_post -> R.drawable.ic_hot
                        else -> R.drawable.ic_new
                    }
                    // Do nothing.
                    categoryButton.text = it.title
                    categoryButton.setCompoundDrawablesWithIntrinsicBounds(iconStart, 0, iconEnd, 0)
                    val sortBy = when (it.title) {
                        getString(R.string.menu_item_top_post) -> COMMENT_SORT_BY_VOTE_COUNT
                        else -> COMMENT_SORT_BY_UPDATED_ON
                    }
                    postViewModel.getComments(sortBy = sortBy)
                    true
                }.show(requireFragmentManager(), null)
            }
            navigationIcon.setOnClickListener { findNavController().navigateUp() }
        }

    }

    override fun setProfileClickDirections(post: Post): NavDirections =
        PostFragmentDirections.actionToProfile(post.user)

    override fun setCommunityClickDirections(post: Post) =
        PostFragmentDirections.actionToCommunity(post.community)

    override fun setPostClickDirections(post: Post) =
        HomeFragmentDirections.actionToPost(post)

    override fun updatePost(post: Post) {
        findNavController().navigateUp()
    }

    private fun setupFab(post: Post, viewModel: PostViewModel) {
        (requireActivity() as MainActivity).setFabListener(FROM_FRAGMENT_COMMENT, post)
        (requireActivity() as MainActivity).findViewById<FloatingActionButton>(R.id.fab)
            .setOnClickListener {
                post.comments = viewModel.comments.value?.data ?: emptyList()
                findNavController().navigate(
                    CreateCommentFragmentDirections.actionGlobalCreateComment(post)
                )
            }
    }

    private fun upVotes(
        upTV: ImageView,
        downTV: ImageView,
        amountTV: TextView,
        comment: Comment,
        fromDown: Boolean
    ) {
        val isDownVote = comment.isDownVoted //true -1
        val isUpVote = comment.isUpVoted //false
        var shouldUpdate = !fromDown
        if (isDownVote) {
            shouldUpdate = true
            downVotes(downTV, upTV, amountTV, comment, true)
        } //true -1
        val drawable = if (!isUpVote) R.drawable.ic_upvote_filled else R.drawable.ic_upvote
        // set state after clicked
        val newComment = postViewModel.updateUpVotesAmount(comment, isUpVote) //true 1
        // if new up vote state is false, means it undo doing up vote and remove document vote in db
        val isDelete = !newComment.isUpVoted //false 1
        if (shouldUpdate) {
            val user =
                postViewModel.updateCommentVotesCurrentUser(
                    accountViewModel.getCurrentUser(),
                    isUpVote,
                    isDownVote,
                    newComment
                )

            accountViewModel.updateCurrentUser(user)
        }
        changeTextViewState(upTV, amountTV, drawable, newComment, true, isDelete, shouldUpdate)
    }

    private fun downVotes(
        downTV: ImageView,
        upTV: ImageView,
        amountTV: TextView,
        comment: Comment,
        fromUp: Boolean
    ) {
        // check state before clicked
        val isUpVote = comment.isUpVoted
        val isDownVote = comment.isDownVoted
        var shouldUpdate = !fromUp
        if (isUpVote) {
            shouldUpdate = true
            upVotes(upTV, downTV, amountTV, comment, true)
        }
        val drawable = if (!isDownVote) R.drawable.ic_downvote_filled else R.drawable.ic_downvote
        // set state after clicked
        val newComment = postViewModel.updateDownVotesAmount(comment, isDownVote) //false 0
        // if new down vote state is false, means it undo doing down vote and remove document vote in db
        val isDelete = !newComment.isDownVoted //true 0
        if (shouldUpdate) {
            val user =
                postViewModel.updateCommentVotesCurrentUser(
                    accountViewModel.getCurrentUser(),
                    isUpVote,
                    isDownVote,
                    newComment
                )
            accountViewModel.updateCurrentUser(user)
        }
        changeTextViewState(downTV, amountTV, drawable, newComment, false, isDelete, shouldUpdate)
    }


    override fun onUpVote(
        upVoteTV: ImageView,
        downVoteTV: ImageView,
        amountTV: TextView,
        comment: Comment,
        fromDown: Boolean
    ) = upVotes(upVoteTV, downVoteTV, amountTV, comment, fromDown)

    override fun onDownVote(
        downVoteTV: ImageView,
        upVoteTV: ImageView,
        amountTV: TextView,
        comment: Comment,
        fromUp: Boolean
    ) = downVotes(downVoteTV, upVoteTV, amountTV, comment, fromUp)

    override fun onCommentDelete(comment: Comment) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(resources.getString(R.string.delete_post_message))
            .setNegativeButton(resources.getString(R.string.cancel_button)) { _, _ ->
            }
            .setPositiveButton(resources.getString(R.string.delete_button)) { _, _ ->
                postViewModel.deleteComment(comment).observe(viewLifecycleOwner, Observer {
                    when (it.status) {
                        ERROR -> accountViewModel.showSnackBar(it.message.toString())
                        else -> Unit
                    }
                })
            }
            .show()

    }

    override fun onProfileClicked(comment: Comment) =
        findNavController().navigate(
            PostFragmentDirections.actionToProfile(comment.user)
        )

    private fun changeTextViewState(
        imageView: ImageView,
        amountTv: TextView,
        icon: Int,
        comment: Comment,
        isUpVote: Boolean,
        isDelete: Boolean,
        shouldUpdate: Boolean
    ) {
        imageView.setImageResource(icon)
        amountTv.text = comment.voteCount.toString()
        if (shouldUpdate) {
            val message = when {
                isUpVote && !isDelete -> getString(R.string.mark_up_vote_post_snack_bar_message)
                !isUpVote && isDelete -> getString(R.string.mark_down_vote_post_snack_bar_message)
                isUpVote && isDelete -> getString(R.string.un_mark_up_vote_post_snack_bar_message)
                else -> getString(R.string.un_mark_down_vote_post_snack_bar_message)
            }
            accountViewModel.showSnackBar(message)
            postViewModel.voteComment(comment, isUpVote, isDelete)
        }
    }

    override fun onImageClicked(imageView: View, attachments: AttachmentList, position: Int) =
        findNavController().navigate(
            PostFragmentDirections.actionToMediaPreview(attachments, position)
        )
}
