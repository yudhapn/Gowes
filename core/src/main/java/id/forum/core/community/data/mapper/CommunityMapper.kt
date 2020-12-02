package id.forum.core.community.data.mapper

import id.forum.core.CommunityQuery.Member
import id.forum.core.community.domain.model.Community
import id.forum.core.fragment.CommunityDetails
import id.forum.core.fragment.CommunityMemberDetails
import id.forum.core.post.data.mapper.mapToDomain
import id.forum.core.post.domain.model.Post
import id.forum.core.user.domain.model.User
import id.forum.core.vo.Address
import id.forum.core.vo.Profile
import java.util.*
import id.forum.core.CommunityQuery.Post as PostData

fun CommunityDetails.mapToDomain(): Community =
    Community(
        id = _id ?: "",
        profile = Profile(
            name = name ?: "",
            avatar = avatar ?: "",
            biodata = biodata ?: "",
            createdOn = createdOn ?: Calendar.getInstance().time
        ),
        memberCount = memberCount ?: 0,
        address = Address(
            city = city ?: "",
            province = province ?: ""
        ),
        isPrivate = isPrivate ?: false,
        surveyQuestion = surveyQuestion ?: "",
        type = type ?: ""
    )

fun CommunityMemberDetails.mapToDomain(): User = User(
    id = user?._id ?: "",
    userName = user?.username ?: "",
    profile = Profile(
        name = user?.name ?: "",
        avatar = user?.avatar ?: ""
    )
)

fun Community.bindMembers(membersData: List<Member?>?): Community {
    val admins = mutableListOf<User>()
    val members = mutableListOf<User>()
    val memberRequest = mutableListOf<User>()
    membersData?.forEach { userData ->
        userData?.fragments?.communityMemberDetails?.let { member ->
            val isAdmin = member.isAdmin ?: false
            val isJoin = member.isJoin ?: false
            val user = member.mapToDomain()
            // check if user has joined into community
            if (isJoin) {
                if (isAdmin) {
                    admins.add(user)
                } else {
                    members.add(user)
                }
            } else {
                // if user doest not join, it means user request to join
                memberRequest.add(user)
            }
        }
    }
    this.admins = admins
    this.members = members
    this.memberRequest = memberRequest
    this.memberCount = admins.size + members.size
    return this
}

fun Community.bindPosts(postsData: List<PostData?>?, currentUser: User): Community {
    this.posts = postsData?.map { postData ->
        postData?.fragments?.postDetails?.mapToDomain(currentUser)
            ?: Post()
    } ?: emptyList()
    return this
}