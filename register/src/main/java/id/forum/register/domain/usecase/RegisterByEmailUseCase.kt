package id.forum.register.domain.usecase

import androidx.lifecycle.LiveData
import id.forum.core.data.Resource
import id.forum.core.data.UserData
import id.forum.register.domain.repository.RegisterRepository

class RegisterByEmailUseCase(private val registerRepository: RegisterRepository) {
    fun invoke(email: String, password: String): LiveData<Resource<UserData>> =
        registerRepository.registerByEmail(email, password)
}