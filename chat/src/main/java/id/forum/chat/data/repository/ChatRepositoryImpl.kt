package id.forum.chat.data.repository

import id.forum.chat.data.service.ChatApolloService
import id.forum.chat.domain.model.Chat
import id.forum.chat.domain.model.Message
import id.forum.chat.domain.repository.ChatRepository
import id.forum.core.data.Resource
import id.forum.core.user.domain.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import org.koin.core.KoinComponent
import org.koin.core.inject


@ExperimentalCoroutinesApi
class ChatRepositoryImpl : KoinComponent, ChatRepository {

    override fun getChats(isRefresh: Boolean): Flow<List<Chat>> {
        val chatService: ChatApolloService by inject()
        return chatService.userChats(isRefresh)
    }

    override fun getMembersCommunities(isRefresh: Boolean): Flow<List<User>> {
        val chatService: ChatApolloService by inject()
        return chatService.membersCommunities(isRefresh)
    }

    override fun getMessages(chatId: String, isRefresh: Boolean): Flow<List<Message>> {
        val chatService: ChatApolloService by inject()
        return chatService.messages(chatId, isRefresh)
    }

    override suspend fun createMessage(chatId: String, message: Message): Resource<Message> {
        val chatService: ChatApolloService by inject()
        return chatService.createMessage(chatId, message)
    }

    override suspend fun createChat(message: Message, receiver: User): Resource<Message> {
        val chatService: ChatApolloService by inject()
        return chatService.createChat(message, receiver)
    }
}