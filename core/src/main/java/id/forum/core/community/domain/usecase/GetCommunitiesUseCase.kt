package id.forum.core.community.domain.usecase

import id.forum.core.community.domain.repository.CommunityRepository
import id.forum.core.community.domain.model.Community
import kotlinx.coroutines.flow.Flow

internal class GetCommunitiesUseCase(private val communityRepository: CommunityRepository) {

    fun execute(): Flow<List<Community?>> = communityRepository.getCommunities()
}
