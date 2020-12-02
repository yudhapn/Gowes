package id.forum.profile.domain.usecase

import id.forum.core.user.domain.model.User
import id.forum.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow

class GetUserProfileUseCase(private val profileRepository: ProfileRepository) {
    fun execute(userId: String, currentUserId: String, isRefresh: Boolean): Flow<User> =
        profileRepository.getUserProfile(userId, currentUserId, isRefresh)
}