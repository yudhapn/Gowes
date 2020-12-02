package id.forum.core.community.data.repository

import id.forum.core.community.data.service.CommunityApolloService
import id.forum.core.community.domain.model.Community
import id.forum.core.community.domain.repository.CommunityRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import org.koin.core.KoinComponent
import org.koin.core.inject


@ExperimentalCoroutinesApi
class CommunityRepositoryImpl : CommunityRepository, KoinComponent {

    override fun getCommunities(): Flow<List<Community>> {
        val communityService: CommunityApolloService by inject()
        return communityService.getCommunities()
    }

    override fun getCommunity(communityId: String): Flow<Community> {
        val communityService: CommunityApolloService by inject()
        return communityService.getCommunity(communityId)
    }
}