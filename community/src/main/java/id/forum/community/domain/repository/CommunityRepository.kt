package id.forum.community.domain.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import id.forum.core.community.domain.model.Community
import id.forum.core.data.Resource
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    fun getCommunityProfile(communityId: String, isRefresh: Boolean): Flow<Community>
    fun updateCommunityProfile(community: Community, imageUri: Uri?): LiveData<Resource<Community>>
    fun createCommunity(community: Community, imageUri: Uri?): LiveData<Resource<Community>>
    suspend fun joinCommunity(communityId: String, userId: String): Resource<Community>
    suspend fun requestJoinCommunity(communityId: String, userId: String, answer: String): Resource<Community>
    suspend fun cancelRequestJoinCommunity(communityId: String, userId: String): Resource<Community>
    suspend fun leaveCommunity(communityId: String, userId: String): Resource<Community>
}