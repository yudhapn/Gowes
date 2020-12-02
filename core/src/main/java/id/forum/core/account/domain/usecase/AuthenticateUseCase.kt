package id.forum.core.account.domain.usecase

import androidx.lifecycle.LiveData
import id.forum.core.account.domain.repository.AccountRepository
import id.forum.core.data.Resource
import id.forum.core.data.UserData

class AuthenticateUseCase(private val accountRepository: AccountRepository) {
    fun isLoggedIn(): Boolean = accountRepository.checkIsLoggedIn()

    fun logout(): LiveData<Resource<UserData>> = accountRepository.logout()

    suspend fun getUserAccount(email: String, password: String) =
        accountRepository.getUserAccount(email, password)
}