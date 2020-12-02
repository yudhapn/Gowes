package id.forum.gallery.domain.usecase

import id.forum.core.media.domain.model.Image
import id.forum.gallery.domain.repository.GalleryRepository

class GetImagesUseCase(private val galleryRepository: GalleryRepository) {
    fun execute(imageList: List<Image>): MutableList<Image> = galleryRepository.getImages(imageList)
}