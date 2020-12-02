package id.forum.core.media.domain.model

import android.net.Uri
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Video(
    val id: Long,
    val uri: Uri,
    val name: String,
    val duration: Int,
    val size: Int
    ) : Parcelable

object VideoDiffCallback : DiffUtil.ItemCallback<Video>() {
    override fun areItemsTheSame(oldItem: Video, newItem: Video) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Video, newItem: Video) =
        oldItem == newItem
}
