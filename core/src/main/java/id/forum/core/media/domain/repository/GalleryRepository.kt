package id.forum.core.media.domain.repository

import id.forum.core.data.Resource
import id.forum.core.media.domain.model.Image

interface GalleryRepository {
    fun compressMedia(imageList: List<Image>): Resource<List<Image>>
}