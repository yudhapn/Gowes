package id.forum.core.account.data.mapper

import id.forum.core.community.domain.model.Community
import id.forum.core.fragment.UserCommunityDetails
import id.forum.core.fragment.UserDetails
import id.forum.core.user.domain.model.User
import id.forum.core.vo.Profile

fun UserDetails.mapToDomain(): User =
    User(
        id = _id ?: "",
        accountId = accountId ?: "",
        profile = Profile(
            name = name ?: "",
            avatar = avatar ?: "",
            biodata = biodata ?: ""
        ),
        userName = username ?: ""
    )

fun UserCommunityDetails.mapToDomain(): Community =
    Community(
        id = community?._id ?: "",
        profile = Profile(
            name = community?.name ?: "",
            avatar = community?.avatar ?: ""
        )
    )
