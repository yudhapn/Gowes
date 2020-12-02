package id.forum.core.post.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Attachment(
    val url: String = "",
    val contentDesc: String = ""
) : Parcelable

@Parcelize
data class AttachmentList(val attachments: List<Attachment>) : Parcelable