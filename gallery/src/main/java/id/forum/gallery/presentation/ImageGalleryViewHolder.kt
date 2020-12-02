package id.forum.gallery.presentation

import androidx.recyclerview.widget.RecyclerView
import id.forum.gallery.databinding.ItemListImageGalleryLayoutBinding
import id.forum.core.media.domain.model.Image
import id.forum.gallery.presentation.GalleryAdapter.GalleryAdapterListener

class ImageGalleryViewHolder(
    private val binding: ItemListImageGalleryLayoutBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(media: Image, listener: GalleryAdapterListener) {
        binding.apply {
            this.media = media
            this.listener = listener
            binding.executePendingBindings()
        }
    }
}
