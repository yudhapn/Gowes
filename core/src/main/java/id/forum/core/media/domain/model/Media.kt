package id.forum.core.media.domain.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Media(
    val id: Long,
    val absolutePath: String,
    val duration: String = "",
    var isSelected: Boolean = false,
    var fileName: String = "",
    var extension: String = "",
    var bitmapData: ByteArray?
) : Parcelable

object MediaDiffCallback : DiffUtil.ItemCallback<Media>() {
    override fun areItemsTheSame(oldItem: Media, newItem: Media) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Media, newItem: Media) =
        oldItem == newItem
}

@Parcelize
data class MediaList(var mediaList: List<Image> = emptyList()) : Parcelable
