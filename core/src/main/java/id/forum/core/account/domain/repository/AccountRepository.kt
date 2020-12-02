package id.forum.core.account.domain.repository

import androidx.lifecycle.LiveData
import id.forum.core.data.Resource
import id.forum.core.data.Token
import id.forum.core.data.UserData
import id.forum.core.user.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun checkIsLoggedIn(): Boolean
    fun updateAccountCache(user: User): User
    fun updateTokenCache(token: Token)
    fun logout(): LiveData<Resource<UserData>>
    suspend fun getUserAccount(email: String, password: String): Resource<User>
}