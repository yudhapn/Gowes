package id.forum.post.presentation

import androidx.recyclerview.widget.RecyclerView
import id.forum.core.post.domain.model.Comment
import id.forum.post.databinding.ItemListCommentLayoutBinding
import id.forum.post.presentation.CommentAdapter.CommentAdapterListener

class CommentViewHolder(
    private val binding: ItemListCommentLayoutBinding,
    private val _listener: CommentAdapterListener,
    private val _currentUid: String
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.apply {
            listener = _listener
            currentUserId = _currentUid
        }
    }

    fun bind(comment: Comment) {
        binding.apply {
            this.comment = comment
            upvoteTextView.setOnClickListener {
                _listener.onUpVote(upvoteTextView, downvoteTextView, voteAmountTextView, comment)
            }
            downvoteTextView.setOnClickListener {
                _listener.onDownVote(downvoteTextView, upvoteTextView, voteAmountTextView, comment)
            }
            executePendingBindings()
        }
    }
}
