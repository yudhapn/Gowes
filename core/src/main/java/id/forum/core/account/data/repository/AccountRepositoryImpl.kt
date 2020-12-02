package id.forum.core.account.data.repository

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import id.forum.core.account.data.service.AccountApolloService
import id.forum.core.account.domain.repository.AccountRepository
import id.forum.core.data.Resource
import id.forum.core.data.Token
import id.forum.core.data.UserData
import id.forum.core.user.domain.model.User
import io.realm.mongodb.App
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.core.KoinComponent
import org.koin.core.inject

@ExperimentalCoroutinesApi
class AccountRepositoryImpl(
    private val realmAppClient: App,
    private val auth: FirebaseAuth
) : KoinComponent, AccountRepository {

    override fun checkIsLoggedIn(): Boolean {
        if (auth.currentUser == null) auth.signInAnonymously()
        return realmAppClient.currentUser() != null
    }

    override fun updateAccountCache(user: User): User {
        val accountService: AccountApolloService by inject()
        return accountService.updateAccountCache(user)
    }

    override fun updateTokenCache(token: Token) {
        val accountService: AccountApolloService by inject()
        accountService.saveTokenCache(token)
    }

    override fun logout(): LiveData<Resource<UserData>> {
        val accountService: AccountApolloService by inject()
        return accountService.logout()
    }
    override suspend fun getUserAccount(email: String, password: String): Resource<User> {
        val accountService: AccountApolloService by inject()
        return accountService.getUserAccount(email, password)
    }
}
