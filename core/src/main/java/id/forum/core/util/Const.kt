package id.forum.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Log
import com.apollographql.apollo.fetcher.ApolloResponseFetchers.*
import com.apollographql.apollo.fetcher.ResponseFetcher
import id.forum.core.media.domain.model.Image
import java.io.ByteArrayOutputStream


const val FROM_FRAGMENT_COMMENT = 11
const val VH_TYPE_HOME = -1
const val VH_TYPE_EXPLORE = 0
const val VH_TYPE_PROFILE = 1
const val VH_TYPE_COMMUNITY = 2
const val VH_TYPE_COMPOSE = 0
const val VH_TYPE_GALLERY = 1
const val FRAGMENT_TYPE_MEMBER = 0
const val FRAGMENT_TYPE_REQUEST = 1
const val POST_SUBJECT = "post_subject"
const val POST_BODY = "post_body"
const val SHARED_TKN = "shared_token"
const val SHARED_TKN_FB = "shared_token_fb"
const val SHARED_USR = "shared_user_data"


fun compressBitmap(image: Image, context: Context): ByteArray {
    Log.d("galleryrepo", "image path: ${image.path}")
    var input = context.contentResolver.openInputStream(image.uri)
    val onlyBoundsOptions = BitmapFactory.Options()
    onlyBoundsOptions.inJustDecodeBounds = true
    onlyBoundsOptions.inDither = true
    onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888
    BitmapFactory.decodeStream(input, null, onlyBoundsOptions)
    input?.close()
    val originalWidth = onlyBoundsOptions.outWidth
    val originalHeight = onlyBoundsOptions.outHeight
    if ((originalWidth == -1) || (originalHeight == -1)) return byteArrayOf()
    val hh = 1000f
    val ww = 600f
    val be =
        when {
            originalWidth > originalHeight && originalWidth > ww -> (originalWidth / ww).toInt()
            originalWidth < originalHeight && originalHeight > hh -> (originalHeight / hh).toInt()
            else -> 1
        }
    val bitmapOptions = BitmapFactory.Options()
    bitmapOptions.inSampleSize = be
    bitmapOptions.inDither = true
    bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888
    input = context.contentResolver.openInputStream(image.uri)
    var bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions)
    bitmap?.let {
        bitmap = rotateToOriginal(image, it)
    }
    input?.close()

    return compressImage(bitmap)
}

fun compressImage(image: Bitmap?): ByteArray {
    Log.d("compress", "compressimage function executed")
    val baos = ByteArrayOutputStream()
    image?.compress(Bitmap.CompressFormat.PNG, 100, baos)
    var options = 100
    while (baos.toByteArray().size / 1024 > 150) {
        Log.d("compress", "executed loop size: ${baos.toByteArray().size}")
        baos.reset()
        image?.compress(Bitmap.CompressFormat.JPEG, options, baos)
        options -= 5
    }
    return baos.toByteArray()
}

fun rotateToOriginal(image: Image, bitmap: Bitmap): Bitmap {
    Log.d("compres", "orientation: ${image.orientation}")
    val exifInterface = ExifInterface(image.path)
    val orientation = exifInterface.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_UNDEFINED
    )

    val rotatedBitmap = when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270F)
        else -> rotateImage(bitmap, 90F)
    }
    return rotatedBitmap
}

fun rotateImage(source: Bitmap, angle: Float): Bitmap {
    Log.d("compress", "angle: $angle")
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(
        source, 0, 0, source.width, source.height,
        matrix, true
    )
}

fun Boolean.apolloResponseFetchers(): ResponseFetcher = if (this) NETWORK_ONLY else CACHE_FIRST
