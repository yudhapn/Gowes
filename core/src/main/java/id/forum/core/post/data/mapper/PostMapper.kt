package id.forum.core.post.data.mapper

import com.apollographql.apollo.api.Input
import id.forum.core.community.domain.model.Community
import id.forum.core.fragment.PostDetails
import id.forum.core.post.domain.model.Attachment
import id.forum.core.post.domain.model.Post
import id.forum.core.type.PostAttachmentsInsertInput
import id.forum.core.type.PostCommunityInsertInput
import id.forum.core.type.PostInsertInput
import id.forum.core.type.PostUserInsertInput
import id.forum.core.user.domain.model.User
import id.forum.core.vo.Profile
import java.util.*

fun PostDetails.mapToDomain(currentUser: User): Post =
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
        isBookmarked = isPostExist(_id, currentUser.bookmarkedPosts),
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

private fun PostDetails.User.mapToDomain(): User =
    User(
        id = _id ?: "",
        profile = Profile(
            avatar = avatar ?: ""
        ),
        userName = username ?: ""
    )

private fun PostDetails.Community.mapToDomain() =
    Community(
        id = _id ?: "",
        profile = Profile(
            avatar = avatar ?: "",
            name = name ?: ""
        )
    )

private fun PostDetails.Attachment.mapToDomain(): Attachment =
    Attachment(
        url = downloadUrl ?: "",
        contentDesc = contentDesc ?: ""
    )


fun bindInputPost(post: Post) = PostInsertInput(
    _id = Input.absent(),
    title = Input.fromNullable(post.title),
    content = Input.fromNullable(post.content),
    user = Input.fromNullable(
        PostUserInsertInput(
            _id = Input.fromNullable(post.user.id),
            username = Input.fromNullable(post.user.userName),
            avatar = Input.fromNullable(post.user.profile.avatar)
        )
    ),
    community = Input.fromNullable(
        PostCommunityInsertInput(
            _id = Input.fromNullable(post.community.id),
            avatar = Input.fromNullable(post.community.profile.avatar)
        )
    ),
    attachments = Input.fromNullable(post.attachments.map { bindInputAttachments(it) }),
    commentCount = Input.fromNullable(0),
    voteCount = Input.fromNullable(0),
    createdOn = Input.fromNullable(Calendar.getInstance().time),
    updatedOn = Input.fromNullable(Calendar.getInstance().time)
)

fun bindInputAttachments(attachment: Attachment) = PostAttachmentsInsertInput(
    contentDesc = Input.fromNullable(attachment.contentDesc),
    downloadUrl = Input.fromNullable(attachment.url)
)
