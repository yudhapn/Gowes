package id.forum.gallery.presentation

import androidx.recyclerview.widget.RecyclerView
import id.forum.gallery.databinding.ItemListImageCreateLayoutBinding
import id.forum.core.media.domain.model.Image
import id.forum.gallery.presentation.GalleryAdapter.GalleryAdapterListener

class ImageCreateViewHolder(
    private val binding: ItemListImageCreateLayoutBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(media: Image, listener: GalleryAdapterListener) {
        binding.apply {
            this.media = media
            this.listener = listener
            binding.executePendingBindings()
        }
    }
}
