package id.forum.core.media.domain.model

import android.net.Uri
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Image(
    val id: Long,
    val path: String,
    val orientation: Int,
    val uri: Uri,
    val name: String,
    val size: Int,
    var bitmapData: ByteArray?,
    var isSelected: Boolean = false
) : Parcelable

object ImageDiffCallback : DiffUtil.ItemCallback<Image>() {
    override fun areItemsTheSame(oldItem: Image, newItem: Image) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Image, newItem: Image) =
        oldItem == newItem
}
