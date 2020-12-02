package id.forum.community.domain.usecase

import id.forum.community.domain.repository.CommunityRepository
import id.forum.core.community.domain.model.Community
import id.forum.core.data.Resource

class CancelRequestJoinCommunityUseCase(private val communityRepository: CommunityRepository) {
    suspend fun execute(communityId: String, userId: String): Resource<Community> =
        communityRepository.cancelRequestJoinCommunity(communityId, userId)
}