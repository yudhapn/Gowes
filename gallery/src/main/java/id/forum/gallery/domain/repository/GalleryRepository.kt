package id.forum.gallery.domain.repository

import id.forum.core.media.domain.model.Image

interface GalleryRepository {
    fun getImages(imageList: List<Image>): MutableList<Image>
}