package id.forum.core.user.domain.repository

import id.forum.core.user.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUser(userId: String): Flow<User>
    fun getCommunityMembers(): Flow<User>
}