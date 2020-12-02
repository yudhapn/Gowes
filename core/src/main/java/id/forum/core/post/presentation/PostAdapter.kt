package id.forum.core.post.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import id.forum.core.databinding.ItemListPostBinding
import id.forum.core.post.domain.model.Post
import id.forum.core.post.domain.model.PostDiffCallback
import id.forum.core.post.presentation.PostAttachmentAdapter.AttachmentAdapterListener

class PostAdapter(
   private val listener: PostAdapterListener,
   private val attachmentListener: AttachmentAdapterListener,
   private val currentUserId: String,
   private val isAdmin: Boolean = false
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback) {

    interface PostAdapterListener {
        fun onProfileClicked(post: Post)
        fun onCommunityClicked(post: Post)
        fun onPostClicked(post: Post)
        fun onPostLongPressed(post: Post): Boolean
        fun onBookmarkClicked(imageView: ImageView, post: Post, isBookmarked: Boolean): Float
        fun onPostDelete(post: Post)
        fun onUpVoteClicked(
           upVoteTV: ImageView,
           downVoteTV: ImageView,
           amountTV: TextView,
           post: Post,
           fromDown: Boolean = false
        )

        fun onDownVoteClicked(
           downVoteTV: ImageView,
           upVoteTV: ImageView,
           amountTV: TextView,
           post: Post,
           fromUp: Boolean = false
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder =
        PostViewHolder(
           ItemListPostBinding.inflate(LayoutInflater.from(parent.context), parent, false),
           listener,
           attachmentListener,
           currentUserId
        )

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) =
        holder.bind(getItem(position), isAdmin)
}
