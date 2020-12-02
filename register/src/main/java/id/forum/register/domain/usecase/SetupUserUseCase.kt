package id.forum.register.domain.usecase

import android.net.Uri
import androidx.lifecycle.LiveData
import id.forum.core.data.Resource
import id.forum.core.user.domain.model.User
import id.forum.register.domain.repository.RegisterRepository

class SetupUserUseCase(private val registerRepository: RegisterRepository) {
    fun invoke(user: User, imageUri: Uri?): LiveData<Resource<User>> =
        registerRepository.setupUser(user, imageUri)
}