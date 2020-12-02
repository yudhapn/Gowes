package id.forum.core.post.domain.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import id.forum.core.community.domain.model.Community
import id.forum.core.post.presentation.PostUiModel
import id.forum.core.user.domain.model.User
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Post(
    val id: String = "",
    val user: User = User(),
    val community: Community = Community(),
    val title: String = "",
    val content: String = "",
    var attachments: List<Attachment> = emptyList(),
    val commentsCount: Int = 0,
    var voteCount: Int = 0,
    var comments: List<Comment> = emptyList(),
    var isBookmarked: Boolean = false,
    var isUpVoted: Boolean = false,
    var isDownVoted: Boolean = false,
    val createdOn: Date = Calendar.getInstance().time,
    val updatedOn: Date = Calendar.getInstance().time
) : Parcelable {
    @IgnoredOnParcel
    var senderPreview: String = user.userName

    @IgnoredOnParcel
    var hasContent: Boolean = content.isNotBlank()

    @IgnoredOnParcel
    var hasAttachments: Boolean = attachments.isNotEmpty()

    @IgnoredOnParcel
    var hasComments: Boolean = comments.isNotEmpty()
}

object PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post) =
        oldItem.id == newItem.id && oldItem.user.id == newItem.user.id

    override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem
}

object PostUiDiffCallback : DiffUtil.ItemCallback<PostUiModel>() {
    override fun areItemsTheSame(oldItem: PostUiModel, newItem: PostUiModel) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: PostUiModel, newItem: PostUiModel) = oldItem == newItem
}
