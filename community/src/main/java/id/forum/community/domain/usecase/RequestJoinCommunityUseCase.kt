package id.forum.community.domain.usecase

import id.forum.community.domain.repository.CommunityRepository
import id.forum.core.community.domain.model.Community
import id.forum.core.data.Resource

class RequestJoinCommunityUseCase(private val communityRepository: CommunityRepository) {
    suspend fun execute(communityId: String, userId: String, answer: String): Resource<Community> =
        communityRepository.requestJoinCommunity(communityId, userId, answer)
}