package id.forum.core.post.data.mapper

import id.forum.core.community.domain.model.Community
import id.forum.core.fragment.BookmarkDetails
import id.forum.core.post.domain.model.Attachment
import id.forum.core.post.domain.model.Post
import id.forum.core.user.domain.model.User
import id.forum.core.vo.Profile
import java.util.*

fun BookmarkDetails.Post.mapToDomain(currentUser: User): Post =
    Post(
        id = _id ?: "",
        user = user?.mapToDomain() ?: User(),
        community = community?.mapToDomain() ?: Community(),
        title = title ?: "",
        content = content ?: "",
        attachments = attachments?.map { it?.mapToDomain() ?: Attachment() } ?: emptyList(),
        commentsCount = commentCount ?: 0,
        voteCount = voteCount ?: 0,
        comments = emptyList(),
        isUpVoted = isPostExist(_id, currentUser.upVotePosts),
        isDownVoted = isPostExist(_id, currentUser.downVotePosts),
        isBookmarked = true,
        createdOn = createdOn ?: Calendar.getInstance().time,
        updatedOn = updatedOn ?: Calendar.getInstance().time
    )

private fun isPostExist(postId: String?, list: List<String>): Boolean {
    var result: String? = null
    postId?.let { currentPostId ->
        // if current post id is exist in list then return true else false
        result = list.find { postId -> currentPostId.equals(postId, true) }
    }
    return result != null
}

fun BookmarkDetails.User.mapToDomain(): User =
    User(
        id = _id ?: "",
        profile = Profile(
            avatar = avatar ?: ""
        ),
        userName = username ?: ""
    )

private fun BookmarkDetails.Community.mapToDomain(): Community =
    Community(
        id = _id ?: "",
        profile = Profile(
            avatar = avatar ?: ""
        )
    )

private fun BookmarkDetails.Attachment.mapToDomain(): Attachment =
    Attachment(
        url = downloadUrl ?: "",
        contentDesc = contentDesc ?: ""
    )
