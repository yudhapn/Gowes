package id.forum.gallery.presentation

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import id.forum.core.media.domain.model.Image

@BindingAdapter("imagesData")
fun RecyclerView.bindRvThreadImages(data: List<Image>?) {
    val adapter = adapter as GalleryAdapter
    adapter.submitList(data)
}