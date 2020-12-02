package id.forum.login.data.repository

import androidx.lifecycle.LiveData
import id.forum.core.data.Resource
import id.forum.core.data.UserData
import id.forum.login.data.service.LoginRealmService
import id.forum.login.domain.repository.LoginRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
class LoginRepositoryImpl(
    private val loginRealmService: LoginRealmService
) : LoginRepository {

    override fun loginByEmail(email: String, password: String): LiveData<Resource<UserData>> {
        return loginRealmService.loginByEmail(email, password)
    }

    override fun resetPassword(email: String, newPassword: String): LiveData<Resource<UserData>> {
        return loginRealmService.resetPassword(email, newPassword)
    }
}
