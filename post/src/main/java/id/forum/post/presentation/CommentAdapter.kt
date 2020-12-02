package id.forum.post.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import id.forum.core.post.domain.model.Comment
import id.forum.core.post.domain.model.CommentDiffCallback
import id.forum.post.databinding.ItemListCommentLayoutBinding

class CommentAdapter(private val listener: CommentAdapterListener, private val currentUid: String) :
    ListAdapter<Comment, CommentViewHolder>(CommentDiffCallback) {

    interface CommentAdapterListener {
        fun onProfileClicked(comment: Comment)
        fun onUpVote(
            upVoteTV: ImageView,
            downVoteTV: ImageView,
            amountTV: TextView,
            comment: Comment,
            fromDown: Boolean = false
        )

        fun onDownVote(
            downVoteTV: ImageView,
            upVoteTV: ImageView,
            amountTV: TextView,
            comment: Comment,
            fromUp: Boolean = false
        )

        fun onCommentDelete(comment: Comment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            ItemListCommentLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), listener, currentUid
        )
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}