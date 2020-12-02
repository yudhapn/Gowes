package id.forum.gallery.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import id.forum.core.util.VH_TYPE_COMPOSE
import id.forum.core.util.VH_TYPE_GALLERY
import id.forum.gallery.databinding.ItemListImageCreateLayoutBinding
import id.forum.gallery.databinding.ItemListImageGalleryLayoutBinding
import id.forum.core.media.domain.model.Image
import id.forum.core.media.domain.model.ImageDiffCallback

class GalleryAdapter(
    private val listener: GalleryAdapterListener,
    val viewHolderType: Int
) : ListAdapter<Image, ViewHolder>(ImageDiffCallback) {
    interface GalleryAdapterListener {
        fun onImageClicked(cardView: View, media: Image)
        fun onRemoveClicked(cardView: View, media: Image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewHolderType) {
            VH_TYPE_COMPOSE -> ImageCreateViewHolder(
                ItemListImageCreateLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            VH_TYPE_GALLERY -> ImageGalleryViewHolder(
                ItemListImageGalleryLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is ImageCreateViewHolder -> holder.bind(getItem(position), listener)
            is ImageGalleryViewHolder -> holder.bind(getItem(position), listener)
        }
    }
}
