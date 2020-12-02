package id.forum.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import id.forum.core.R
import id.forum.core.account.presentation.UserAccountViewModel
import id.forum.core.post.domain.model.Post
import id.forum.core.post.presentation.PostAdapter.PostAdapterListener
import id.forum.core.post.presentation.PostAttachmentAdapter.AttachmentAdapterListener
import id.forum.core.post.presentation.PostViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
abstract class BaseFragment : Fragment(),
    PostAdapterListener, AttachmentAdapterListener {
    private val viewModel: PostViewModel by viewModel()
    protected val accountViewModel: UserAccountViewModel by sharedViewModel()
    protected lateinit var currentUid: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        currentUid = accountViewModel.getCurrentUser().id
        return provideFragmentView(inflater, container, savedInstanceState)
    }

    abstract fun provideFragmentView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View

    abstract fun setProfileClickDirections(post: Post): NavDirections
    abstract fun setCommunityClickDirections(post: Post): NavDirections
    abstract fun setPostClickDirections(post: Post): NavDirections
    abstract fun updatePost(post: Post)

    override fun onProfileClicked(post: Post) {
        findNavController().navigate(setProfileClickDirections(post))
//      NavigationModel.setNavigationMenuItemChecked(4)
    }

    override fun onCommunityClicked(post: Post) =
        findNavController().navigate(setCommunityClickDirections(post))

    override fun onPostClicked(post: Post) =
        findNavController().navigate(setPostClickDirections(post))

    @Suppress("DEPRECATION")
    override fun onPostLongPressed(post: Post): Boolean {
//        MenuBottomSheetDialogFragment(R.menu.email_bottom_sheet_menu) {
//            // Do nothing.
//            true
//        }.show(requireFragmentManager(), null)

        return true
    }

    override fun onPostDelete(post: Post) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(resources.getString(R.string.delete_post_message))
            .setNegativeButton(resources.getString(R.string.cancel_button)) { _, _ ->
            }
            .setPositiveButton(resources.getString(R.string.delete_button)) { _, _ ->
                updatePost(post)
                viewModel.deletePost(post)
            }
            .show()
    }

    override fun onBookmarkClicked(imageView: ImageView, post: Post, isBookmarked: Boolean): Float {
        /*
        changing the corner to rounded icon if post has not been bookmarked change and vice versa
        and changing the icon to filled icon if post has not been bookmarked change and vice versa
        cause of button clicked effect
        */
        val message =
            if (!isBookmarked)
                getString(R.string.mark_bookmark_post_snack_bar_message)
            else
                getString(R.string.un_mark_bookmark_post_snack_bar_message)
        accountViewModel.showSnackBar(message)
        val interpolation = if (!isBookmarked) 1F else 0F
        imageView.setImageResource(
            if (!isBookmarked) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark
        )
        val currentUser =
            viewModel.updateBookmarkedPost(post, isBookmarked, accountViewModel.getCurrentUser())
        accountViewModel.updateCurrentUser(currentUser)
        return interpolation
    }

    override fun onUpVoteClicked(
        upVoteTV: ImageView,
        downVoteTV: ImageView,
        amountTV: TextView,
        post: Post,
        fromDown: Boolean
    ) {
        // check state before clicked
        val isDownVote = viewModel.checkDownVotes(post) //true -1
        val isUpVote = viewModel.checkUpVotes(post) //false
        var shouldUpdate = !fromDown
        if (isDownVote) {
            shouldUpdate = true
            onDownVoteClicked(downVoteTV, upVoteTV, amountTV, post, true)
        } //true -1
        val drawable = if (!isUpVote) R.drawable.ic_upvote_filled else R.drawable.ic_upvote
        // set state after clicked
        val newPost = viewModel.updateUpVotesAmount(post, isUpVote) //true 1
        // if new up vote state is false, means it undo doing up vote and remove document vote in db
        val isDelete = !newPost.isUpVoted //false 1
        if (shouldUpdate) {
            val user =
                viewModel.updatePostVotesCurrentUser(
                    accountViewModel.getCurrentUser(),
                    isUpVote,
                    isDownVote,
                    newPost
                )

            accountViewModel.updateCurrentUser(user)
        }
        changeTextViewState(upVoteTV, amountTV, drawable, newPost, true, isDelete, shouldUpdate)
    }

    override fun onDownVoteClicked(
        downVoteTV: ImageView,
        upVoteTV: ImageView,
        amountTV: TextView,
        post: Post,
        fromUp: Boolean
    ) {
        // check state before clicked
        val isUpVote = viewModel.checkUpVotes(post) //false -1
        val isDownVote = viewModel.checkDownVotes(post) //true
        var shouldUpdate = !fromUp
        if (isUpVote) {
            shouldUpdate = true
            onUpVoteClicked(upVoteTV, downVoteTV, amountTV, post, true)
        } //false skip
        val drawable = if (!isDownVote) R.drawable.ic_downvote_filled else R.drawable.ic_downvote
        // set state after clicked
        val newPost = viewModel.updateDownVotesAmount(post, isDownVote) //false 0
        // if new down vote state is false, means it undo doing down vote and remove document vote in db
        val isDelete = !newPost.isDownVoted //true 0
        if (shouldUpdate) {
            val user =
                viewModel.updatePostVotesCurrentUser(
                    accountViewModel.getCurrentUser(),
                    isUpVote,
                    isDownVote,
                    newPost
                )
            accountViewModel.updateCurrentUser(user)
        }
        changeTextViewState(downVoteTV, amountTV, drawable, newPost, false, isDelete, shouldUpdate)
    }

    private fun changeTextViewState(
        imageView: ImageView,
        amountTv: TextView,
        icon: Int,
        post: Post,
        isUpVote: Boolean,
        isDelete: Boolean,
        shouldUpdate: Boolean
    ) {
        imageView.setImageResource(icon)
        amountTv.text = post.voteCount.toString()
        if (shouldUpdate) {
            val message = when {
                isUpVote && !isDelete -> getString(R.string.mark_up_vote_post_snack_bar_message)
                !isUpVote && isDelete -> getString(R.string.mark_down_vote_post_snack_bar_message)
                isUpVote && isDelete -> getString(R.string.un_mark_up_vote_post_snack_bar_message)
                else -> getString(R.string.un_mark_down_vote_post_snack_bar_message)
            }
            accountViewModel.showSnackBar(message)
            viewModel.votePost(post, isUpVote, isDelete)
        }
    }
}
