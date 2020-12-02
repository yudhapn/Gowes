package id.forum.core.user.domain.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.google.gson.annotations.SerializedName
import id.forum.core.community.domain.model.Community
import id.forum.core.post.domain.model.Post
import id.forum.core.vo.Profile
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("uid")
    val accountId: String = "",
    @SerializedName("profile")
    var profile: Profile = Profile(),
    @SerializedName("userName")
    val userName: String = "",
    @SerializedName("communities")
    var communities: List<Community> = emptyList(),
    var posts: List<Post> = emptyList(),
    var chats: List<String> = emptyList(),
    var bookmarkedPosts: List<String> = emptyList(),
    var upVotePosts: List<String> = emptyList(),
    var downVotePosts: List<String> = emptyList(),
    var upVoteComments: List<String> = emptyList(),
    var downVoteComments: List<String> = emptyList(),
    val isCurrentAccount: Boolean = false
) : Parcelable

object UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: User, newItem: User) =
        oldItem.profile.name == newItem.profile.name
                && oldItem.userName == newItem.userName
                && oldItem.isCurrentAccount == newItem.isCurrentAccount
}
