package id.forum.register.domain.usecase

import id.forum.core.account.domain.repository.AccountRepository
import id.forum.core.data.Token

class UpdateTokenCacheUseCase(private val accountRepository: AccountRepository) {
    fun invoke(token: Token) = accountRepository.updateTokenCache(token)
}