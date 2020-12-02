package id.forum.chat.domain.usecase

import id.forum.chat.domain.repository.ChatRepository
import id.forum.core.user.domain.model.User
import kotlinx.coroutines.flow.Flow

class GetMembersCommunitiesUseCase(private val chatRepository: ChatRepository) {
    fun execute(isRefresh: Boolean): Flow<List<User>> = chatRepository.getMembersCommunities(isRefresh)
}