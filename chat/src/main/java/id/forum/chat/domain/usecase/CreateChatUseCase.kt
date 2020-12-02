package id.forum.chat.domain.usecase

import id.forum.chat.domain.model.Chat
import id.forum.chat.domain.model.Message
import id.forum.chat.domain.repository.ChatRepository
import id.forum.core.data.Resource
import id.forum.core.user.domain.model.User

class CreateChatUseCase(private val chatRepository: ChatRepository) {
    suspend fun execute(message: Message, receiver: User): Resource<Message> =
        chatRepository.createChat(message, receiver)
}