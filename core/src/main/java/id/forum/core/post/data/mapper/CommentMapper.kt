package id.forum.core.post.data.mapper

import id.forum.core.fragment.CommentDetails
import id.forum.core.post.domain.model.Comment
import id.forum.core.user.domain.model.User
import id.forum.core.vo.Profile
import java.util.*

fun CommentDetails.mapToDomain(): Comment =
    Comment(
        id = _id ?: "",
        user = bindUser(user),
        content = content ?: "",
        voteCount = voteCount ?: 0,
        createdOn = createdOn ?: Calendar.getInstance().time

    )

private fun bindUser(user: CommentDetails.User?) =
    User(
        id = user?._id ?: "",
        profile = Profile(
            name = user?.name ?: "",
            avatar = user?.avatar ?: ""
        ),
        userName = user?.username ?: ""
    )
