package id.forum.gallery.data.repository

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import id.forum.core.data.Resource
import id.forum.core.media.domain.model.Image
import id.forum.core.media.domain.model.Video
import id.forum.core.util.compressBitmap
import id.forum.gallery.domain.repository.GalleryRepository
import java.io.File
import java.util.concurrent.TimeUnit

class GalleryRepositoryImpl(private val context: Context): GalleryRepository {
    private var _mediaList = mutableListOf<Image>()

    @Suppress("DEPRECATION")
    @SuppressLint("Recycle", "InlinedApi")
    override fun getImages(imageList: List<Image>): MutableList<Image> {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.ORIENTATION,
            MediaStore.Images.Media.SIZE
        )


        val query = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection, null, null, null
        )

        _mediaList.clear()
        query?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val orientationColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val path = it.getString(dataColumn)
                val orientation = it.getInt(orientationColumn)
                val name = it.getString(nameColumn)
                val size = it.getInt(sizeColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
                )
                _mediaList.add(Image(id, path, orientation, contentUri, name, size, null))
            }
        }
        imageList.forEach { setCheckedMedia(it) }
        return _mediaList
    }

    fun loadVideos(): MutableList<Video> {
        val videoList = mutableListOf<Video>()

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE
        )

        val selection = "${MediaStore.Video.Media.DURATION} >= ?"
        val selectionArgs = arrayOf(TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES).toString())

        val query = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            "${MediaStore.Video.Media.DISPLAY_NAME} ASC"
        )

        query?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val durationColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val name = it.getString(nameColumn)
                val duration = it.getInt(durationColumn)
                val size = it.getInt(sizeColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id
                )

                videoList += Video(id, contentUri, name, duration, size)
            }
        }
        return videoList
    }

    private fun setCheckedMedia(media: Image) {
        val mediaList = _mediaList
        mediaList.forEachIndexed { index, it ->
            val shouldCheck = it.path == media.path
            if (shouldCheck) {
                mediaList[index] = it.copy(isSelected = shouldCheck)
                _mediaList = mediaList
                return@forEachIndexed
            }
        }
    }

    private fun getFileExtension(uri: Uri) =
        if (uri.scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
        } else {
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path ?: "")).toString())
        }
}
