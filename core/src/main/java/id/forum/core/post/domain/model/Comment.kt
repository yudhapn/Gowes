package id.forum.core.post.domain.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import id.forum.core.user.domain.model.User
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Comment(
    val id: String = "",
    val user: User = User(),
    val content: String = "",
    var voteCount: Int = 0,
    var isUpVoted: Boolean = false,
    var isDownVoted: Boolean = false,
    var createdOn: Date = Calendar.getInstance().time
) : Parcelable {
    @IgnoredOnParcel
    var hasBody: Boolean = content.isNotBlank()
}

object CommentDiffCallback : DiffUtil.ItemCallback<Comment>() {
    override fun areItemsTheSame(oldItem: Comment, newItem: Comment) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Comment, newItem: Comment) =
        oldItem == newItem
}
