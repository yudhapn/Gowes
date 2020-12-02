package id.forum.community.domain.usecase

import id.forum.community.domain.repository.CommunityRepository
import id.forum.core.community.domain.model.Community
import kotlinx.coroutines.flow.Flow

class GetCommunityProfileUseCase(private val communityRepository: CommunityRepository) {
    fun execute(communityId: String, isRefresh: Boolean): Flow<Community> =
        communityRepository.getCommunityProfile(communityId, isRefresh)
}