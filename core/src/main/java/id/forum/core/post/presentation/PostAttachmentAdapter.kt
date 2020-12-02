package id.forum.core.post.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import id.forum.core.post.domain.model.Attachment
import id.forum.core.post.domain.model.AttachmentList

abstract class PostAttachmentAdapter(private val listener: AttachmentAdapterListener) :
    RecyclerView.Adapter<PostAttachmentViewHolder>() {
    interface AttachmentAdapterListener {
        fun onImageClicked(imageView: View, attachments: AttachmentList, position: Int)
    }

    private var list: List<Attachment> = emptyList()

    override fun getItemCount() = list.size

    override fun getItemViewType(position: Int) = getLayoutIdForPosition(position, list.size)

    fun submitList(attachments: List<Attachment>) {
        list = attachments
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAttachmentViewHolder {
        return PostAttachmentViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                viewType,
                parent,
                false
            ),
            listener
        )
    }

    override fun onBindViewHolder(holder: PostAttachmentViewHolder, position: Int) {
        holder.bind(AttachmentList(list), position)
    }

    /**
     * Clients should implement this method to determine what layout is inflated for a given
     * position. The layout must include a data parameter named 'emailAttachment' with a type
     * of [PostAttachment].
     */
    abstract fun getLayoutIdForPosition(position: Int, size: Int): Int
}
