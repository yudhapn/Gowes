package id.forum.core.post.presentation

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import id.forum.core.R
import id.forum.core.databinding.ItemListPostBinding
import id.forum.core.post.domain.model.Post
import id.forum.core.post.presentation.PostAdapter.PostAdapterListener
import id.forum.core.post.presentation.PostAttachmentAdapter.AttachmentAdapterListener

class PostViewHolder(
    private val binding: ItemListPostBinding,
    private val postListener: PostAdapterListener,
    private val attachmentListener: AttachmentAdapterListener,
    private val currentUserId: String
) : ViewHolder(binding.root) {

    private val attachmentAdapter = object : PostAttachmentAdapter(attachmentListener) {
        override fun getLayoutIdForPosition(position: Int, size: Int): Int =
            if (size == 1) {
                R.layout.email_attachment_preview_single_layout
            } else {
                when (position + 1) {
                    1 -> R.layout.email_attachment_preview_item_layout
                    size -> R.layout.email_attachment_preview_end_item_layout
                    else -> R.layout.email_attachment_preview_middle_item_layout
                }
            }
    }

    init {
        binding.run {
            listener = postListener
            attachmentRecyclerView.adapter = attachmentAdapter
            root.background = PostSwipeActionDrawable(root.context)
        }
    }

    fun bind(post: Post, isAdmin: Boolean) {
        binding.apply {
            this.currentUserId = this@PostViewHolder.currentUserId
            this.post = post
            this.isAdmin = isAdmin
            val isBookmarked = post.isBookmarked
            bookmarkImageView.setImageResource(
                if (isBookmarked) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark
            )
            root.isActivated = isBookmarked
            upvoteTextView.setOnClickListener {
                postListener.onUpVoteClicked(
                    upvoteTextView,
                    downvoteTextView,
                    voteAmountTextView,
                    post
                )
            }
            downvoteTextView.setOnClickListener {
                postListener.onDownVoteClicked(
                    downvoteTextView,
                    upvoteTextView,
                    voteAmountTextView,
                    post
                )
            }

            bookmarkImageView.setOnClickListener {
                cardView.progress =
                    postListener.onBookmarkClicked(bookmarkImageView, post, isBookmarked)
                root.isActivated = !isBookmarked
            }

            // Setting interpolation here controls whether or not we draw the top left corner as
            // rounded or squared. Since all other corners are set to 0dp rounded, they are
            // not affected.
            val interpolation = if (isBookmarked) 1F else 0F
            cardView.progress = interpolation
            executePendingBindings()
        }
    }
}