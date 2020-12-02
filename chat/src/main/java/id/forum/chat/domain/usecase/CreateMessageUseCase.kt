package id.forum.chat.domain.usecase

import id.forum.chat.domain.model.Message
import id.forum.chat.domain.repository.ChatRepository
import id.forum.core.data.Resource

class CreateMessageUseCase(private val chatRepository: ChatRepository) {
    suspend fun execute(chatId: String, message: Message): Resource<Message> =
        chatRepository.createMessage(chatId, message)
}