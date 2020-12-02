package id.forum.core.post.presentation

import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import id.forum.core.post.domain.model.AttachmentList

class PostAttachmentViewHolder(
    val binding: ViewDataBinding,
    val listener: PostAttachmentAdapter.AttachmentAdapterListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(attachments: AttachmentList, position: Int) {
        binding.run {
            setVariable(BR.listener, listener)
            setVariable(BR.position, position)
            setVariable(
                BR.postAttachments,
                attachments
            )
            executePendingBindings()
        }
    }
}