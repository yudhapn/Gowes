package id.forum.community.data.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import id.forum.community.data.service.CommunityApolloService
import id.forum.community.domain.repository.CommunityRepository
import id.forum.core.community.domain.model.Community
import id.forum.core.data.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import org.koin.core.KoinComponent
import org.koin.core.inject


@ExperimentalCoroutinesApi
class CommunityRepositoryImpl : CommunityRepository, KoinComponent {

    override fun getCommunityProfile(communityId: String, isRefresh: Boolean): Flow<Community> {
        val communityService: CommunityApolloService by inject()
        return communityService.getCommunity(communityId, isRefresh)
    }

    override fun updateCommunityProfile(
        community: Community,
        imageUri: Uri?
    ): LiveData<Resource<Community>> {
        val communityService: CommunityApolloService by inject()
        return communityService.isCommunityAvatarExist(community, imageUri, isCreate = false)
    }

    override fun createCommunity(
        community: Community,
        imageUri: Uri?
    ): LiveData<Resource<Community>> {
        val communityService: CommunityApolloService by inject()
        return communityService.isCommunityAvatarExist(community, imageUri, isCreate = true)

    }

    override suspend fun joinCommunity(communityId: String, userId: String): Resource<Community> {
        val communityService: CommunityApolloService by inject()
        return communityService.updateMember(communityId, userId, isJoin = true, isRequest = false)
    }

    override suspend fun requestJoinCommunity(communityId: String, userId: String, answer: String): Resource<Community> {
        val communityService: CommunityApolloService by inject()
        return communityService.updateMember(communityId, userId, isJoin = false, isRequest = true, answer = answer)
    }

    override suspend fun cancelRequestJoinCommunity(communityId: String, userId: String): Resource<Community> {
        val communityService: CommunityApolloService by inject()
        return communityService.updateMember(communityId, userId, isJoin = false, isRequest = false, isCancel = true)
    }

    override suspend fun leaveCommunity(communityId: String, userId: String): Resource<Community> {
        val communityService: CommunityApolloService by inject()
        return communityService.updateMember(communityId, userId, isJoin = false, isRequest = false)
    }
}