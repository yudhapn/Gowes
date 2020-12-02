package id.forum.chat.domain.usecase

import id.forum.chat.domain.model.Chat
import id.forum.chat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class GetChatsUseCase(private val chatRepository: ChatRepository) {
    fun execute(isRefresh: Boolean): Flow<List<Chat>> = chatRepository.getChats(isRefresh)
}