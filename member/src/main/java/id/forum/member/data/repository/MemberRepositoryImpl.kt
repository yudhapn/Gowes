package id.forum.member.data.repository

import id.forum.core.community.domain.model.Community
import id.forum.core.data.Resource
import id.forum.member.data.service.MemberApolloService
import id.forum.member.domain.repository.MemberRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class MemberRepositoryImpl : KoinComponent, MemberRepository {
    override suspend fun acceptRequestMember(
        communityId: String,
        userId: String
    ): Resource<Community> {
        val memberService: MemberApolloService by inject()
        return memberService.updateMember(communityId, userId, isJoin = true, isRequest = false)
    }

    override suspend fun rejectRequestMember(
        communityId: String,
        userId: String
    ): Resource<Community> {
        val memberService: MemberApolloService by inject()
        return memberService.updateMember(communityId, userId, isJoin = false, isRequest = false)
    }

    override suspend fun appointMemberAsAdmin(
        communityId: String,
        userId: String
    ): Resource<Community> {
        val memberService: MemberApolloService by inject()
        return memberService.updateMember(
            communityId,
            userId,
            isAdmin = true,
            isJoin = true,
            isRequest = false
        )
    }

    override suspend fun expelMember(communityId: String, userId: String): Resource<Community> {
        val memberService: MemberApolloService by inject()
        return memberService.updateMember(communityId, userId, isJoin = false, isRequest = false)
    }
}