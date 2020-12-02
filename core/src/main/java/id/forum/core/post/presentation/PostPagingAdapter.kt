package id.forum.core.post.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import id.forum.core.R
import id.forum.core.base.SeparatorViewHolder
import id.forum.core.databinding.ItemListPostPagingBinding
import id.forum.core.post.domain.model.Post
import id.forum.core.post.domain.model.PostUiDiffCallback
import id.forum.core.post.presentation.PostAttachmentAdapter.AttachmentAdapterListener

class PostPagingAdapter(
    private val listener: PostAdapterListener,
    private val attachmentListener: AttachmentAdapterListener,
    private val currentUserId: String,
    private val isAdmin: Boolean = false
) : PagingDataAdapter<PostUiModel, ViewHolder>(PostUiDiffCallback) {

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

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is PostUiModel.PostItem -> R.layout.item_list_post_paging
        is PostUiModel.SeparatorItem -> R.layout.post_separator_item
        null -> throw UnsupportedOperationException("Unknown view")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        when (viewType) {
            R.layout.item_list_post_paging -> PostPagingViewHolder(
                ItemListPostPagingBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ), listener, attachmentListener, currentUserId
            )
            R.layout.post_separator_item -> SeparatorViewHolder.create(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uiModel = getItem(position)
        uiModel.let {
            when (holder) {
                is PostPagingViewHolder -> holder.bind(
                    (it as PostUiModel.PostItem).post,
                    isAdmin
                )
                is SeparatorViewHolder -> holder.bind((it as PostUiModel.SeparatorItem).description)
            }
        }

    }
}


sealed class PostUiModel {
    data class PostItem(val post: Post) : PostUiModel() {
        override val id = post.id
    }

    data class SeparatorItem(val description: String) : PostUiModel() {
        override val id = "separator"
    }

    abstract val id: String
}
