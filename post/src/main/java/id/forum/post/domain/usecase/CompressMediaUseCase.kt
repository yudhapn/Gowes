package id.forum.post.domain.usecase

import id.forum.core.data.Resource
import id.forum.core.media.domain.model.Image
import id.forum.core.media.domain.repository.GalleryRepository

class CompressMediaUseCase(private val galleryRepository: GalleryRepository) {
    fun execute(images: List<Image>): Resource<List<Image>> = galleryRepository.compressMedia(images)
}