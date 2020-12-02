package id.forum.profile.domain.usecase

import android.net.Uri
import androidx.lifecycle.LiveData
import id.forum.core.data.Resource
import id.forum.core.user.domain.model.User
import id.forum.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow

class UpdateUserProfileUseCase(private val profileRepository: ProfileRepository) {
    fun execute(user: User, imageUri: Uri?): LiveData<Resource<User>> =
        profileRepository.updateUserProfile(user, imageUri)
}