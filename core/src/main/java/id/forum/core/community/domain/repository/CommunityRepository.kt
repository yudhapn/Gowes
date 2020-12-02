package id.forum.core.community.domain.repository

import id.forum.core.community.domain.model.Community
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    fun getCommunities(): Flow<List<Community>>
    fun getCommunity(communityId: String): Flow<Community>
    //    fun getUserCommunities(userId: String): Flow<List<Community>>
}