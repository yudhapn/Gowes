package id.forum.login.presentation

import androidx.lifecycle.LiveData
import id.forum.core.base.BaseViewModel
import id.forum.core.data.Resource
import id.forum.core.data.UserData
import id.forum.login.domain.usecase.LoginByEmailUseCase

class LoginViewModel(private val loginByEmailUseCase: LoginByEmailUseCase) : BaseViewModel() {
    fun loginByEmail(email: String, password: String): LiveData<Resource<UserData>> {
        return loginByEmailUseCase.execute(email, password)
    }
}
