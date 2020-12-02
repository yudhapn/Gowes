package id.forum.chat.domain.usecase

import id.forum.chat.domain.model.Message
import id.forum.chat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class GetMessagesUseCase(private val chatRepository: ChatRepository) {
    fun execute(chatId: String, isRefresh: Boolean): Flow<List<Message>> =
        chatRepository.getMessages(chatId, isRefresh)
}