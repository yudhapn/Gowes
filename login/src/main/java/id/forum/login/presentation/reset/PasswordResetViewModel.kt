package id.forum.login.presentation.reset

import androidx.lifecycle.LiveData
import id.forum.core.base.BaseViewModel
import id.forum.core.data.Resource
import id.forum.core.data.UserData
import id.forum.login.domain.usecase.ResetPasswordUseCase

class PasswordResetViewModel(private val resetPasswordUseCase: ResetPasswordUseCase) :
    BaseViewModel() {
    fun resetPassword(email: String, newPassword: String): LiveData<Resource<UserData>> {
        return resetPasswordUseCase.execute(email, newPassword)
    }
}
