package id.forum.core.media.data.repository

import android.content.Context
import android.util.Log
import id.forum.core.data.Resource
import id.forum.core.media.domain.model.Image
import id.forum.core.media.domain.repository.GalleryRepository
import id.forum.core.util.compressBitmap

class GalleryRepositoryImpl(private val context: Context) : GalleryRepository {

    override fun compressMedia(imageList: List<Image>): Resource<List<Image>> {
        val mediaCompressedList = imageList.map {
            if (it.bitmapData == null) {
                val data = compressBitmap(it, context)
                Log.d("galleryrepo", "compress media, data: ${data.size}")
                it.copy(bitmapData = data)
            } else {
                Log.d("galleryRepo", "not compress media")
                it
            }
        }
        return Resource.success(mediaCompressedList)
    }
}
