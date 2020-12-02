package id.forum.member.domain.repository

import id.forum.core.community.domain.model.Community
import id.forum.core.data.Resource

interface MemberRepository {
    suspend fun acceptRequestMember(communityId: String, userId: String): Resource<Community>
    suspend fun rejectRequestMember(communityId: String, userId: String): Resource<Community>
    suspend fun appointMemberAsAdmin(communityId: String, userId: String): Resource<Community>
    suspend fun expelMember(communityId: String, userId: String): Resource<Community>
}