package id.forum.core.account.domain.usecase

import id.forum.core.account.domain.repository.AccountRepository
import id.forum.core.user.domain.model.User

class UpdateAccountCacheUseCase(private val accountRepository: AccountRepository) {
    fun execute(user: User) {
        accountRepository.updateAccountCache(user)
    }
}