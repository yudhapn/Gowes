package id.forum.member.domain.usecase

import id.forum.core.community.domain.model.Community
import id.forum.core.data.Resource
import id.forum.member.domain.repository.MemberRepository

class AcceptRequestMemberUseCase(private val memberRepository: MemberRepository) {
    suspend fun execute(communityId: String, userId: String): Resource<Community> =
        memberRepository.acceptRequestMember(communityId, userId)
}