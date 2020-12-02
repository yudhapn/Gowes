package id.forum.core.community.domain.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import id.forum.core.post.domain.model.Post
import id.forum.core.user.domain.model.User
import id.forum.core.vo.Address
import id.forum.core.vo.Profile
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Community(
    val id: String = "",
    var profile: Profile = Profile(),
    var address: Address = Address(),
    var isPrivate: Boolean = false,
    var members: List<User> = emptyList(),
    var admins: List<User> = emptyList(),
    var memberRequest: List<User> = emptyList(),
    var posts: List<Post> = emptyList(),
    var surveyQuestion: String = "Why do you want to join to this community?",
    var type: String = "",
    var memberCount: Int = 0
) : Parcelable

object CommunityDiffCallback : DiffUtil.ItemCallback<Community>() {
    override fun areItemsTheSame(oldItem: Community, newItem: Community) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Community, newItem: Community) =
        oldItem == newItem
}