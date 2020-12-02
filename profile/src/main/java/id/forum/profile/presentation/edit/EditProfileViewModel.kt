package id.forum.profile.presentation.edit

import android.net.Uri
import androidx.lifecycle.LiveData
import id.forum.core.base.BaseViewModel
import id.forum.core.data.Resource
import id.forum.core.user.domain.model.User
import id.forum.core.vo.Profile
import id.forum.profile.domain.usecase.UpdateUserProfileUseCase

class EditProfileViewModel(
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val currentUser: User
) :
    BaseViewModel() {

    fun updateUser(
        newName: String, newBio: String, imageUri: Uri?
    ): LiveData<Resource<User>> {
        currentUser.profile =
            Profile(name = newName, biodata = newBio, avatar = currentUser.profile.avatar)
        return updateUserProfileUseCase.execute(currentUser, imageUri)
    }
}
