package id.forum.profile.data.repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import id.forum.core.data.Resource
import id.forum.core.user.domain.model.User
import id.forum.profile.data.service.ProfileApolloService
import id.forum.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import org.koin.core.KoinComponent
import org.koin.core.inject

@ExperimentalCoroutinesApi
class ProfileRepositoryImpl : ProfileRepository, KoinComponent {
    override fun getUserProfile(userId: String, currentUserId: String, isRefresh: Boolean): Flow<User> {
        Log.d("ProfileRepositoryImpl", "getUserProfile called")
        val profileService: ProfileApolloService by inject()
        return profileService.getUserProfile(userId, currentUserId, isRefresh)
    }

    override fun updateUserProfile(user: User, imageUri: Uri?): LiveData<Resource<User>> {
        val profileService: ProfileApolloService by inject()
        return profileService.updateProfile(user, imageUri)
    }
}