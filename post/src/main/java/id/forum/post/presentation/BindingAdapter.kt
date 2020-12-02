package id.forum.post.presentation

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import id.forum.core.post.domain.model.Comment

@BindingAdapter("commentsData")
fun RecyclerView.bindRvComment(data: List<Comment>?) {
    val adapter = adapter as CommentAdapter
    adapter.submitList(data)
}