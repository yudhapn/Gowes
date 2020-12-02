package id.forum.profile.domain.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import id.forum.core.data.Resource
import id.forum.core.user.domain.model.User
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getUserProfile(userId: String, currentUserId: String, isRefresh: Boolean): Flow<User>
    fun updateUserProfile(user: User, imageUri: Uri?): LiveData<Resource<User>>
}