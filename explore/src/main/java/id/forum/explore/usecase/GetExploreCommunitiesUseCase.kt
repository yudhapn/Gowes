package id.forum.explore.usecase

import id.forum.core.community.domain.model.Community
import id.forum.core.community.domain.repository.CommunityRepository
import id.forum.core.user.domain.model.User
import kotlinx.coroutines.flow.Flow

internal class GetExploreCommunitiesUseCase(private val communityRepository: CommunityRepository) {

    fun execute(): Flow<List<Community>> =
        communityRepository.getCommunities()
}
