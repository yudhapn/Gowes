package id.forum.core.post.presentation

import id.forum.core.R

class PostAttachmentGridAdapter(
    listener: AttachmentAdapterListener
) : PostAttachmentAdapter(listener) {

    override fun getLayoutIdForPosition(position: Int, size: Int): Int =
        R.layout.email_attachment_grid_item_layout
}