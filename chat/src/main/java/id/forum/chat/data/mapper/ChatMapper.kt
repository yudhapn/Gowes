package id.forum.chat.data.mapper

import id.forum.chat.domain.model.Chat
import id.forum.core.fragment.ChatDetails
import id.forum.core.user.domain.model.User
import id.forum.core.vo.Profile

fun ChatDetails.mapToDomain(): Chat =
    Chat(
        _id ?: "",
        users = users?.map { bindUser(it) } ?: emptyList(),
        lastText = lastText ?: ""
    )

private fun bindUser(user: ChatDetails.User?) =
    User(
        id = user?._id ?: "",
        profile = Profile(
            avatar = user?.avatar ?: ""
        ),
        userName = user?.username ?: ""
    )
