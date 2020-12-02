package id.forum.login.domain.usecase

import androidx.lifecycle.LiveData
import id.forum.core.data.Resource
import id.forum.core.data.UserData
import id.forum.login.domain.repository.LoginRepository

class ResetPasswordUseCase(private val loginRepository: LoginRepository) {
    fun execute(email: String, newPassword: String): LiveData<Resource<UserData>> =
        loginRepository.resetPassword(email, newPassword)
}