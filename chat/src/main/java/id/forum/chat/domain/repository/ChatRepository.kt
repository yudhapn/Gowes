package id.forum.chat.domain.repository

import id.forum.chat.domain.model.Chat
import id.forum.chat.domain.model.Message
import id.forum.core.data.Resource
import id.forum.core.user.domain.model.User
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChats(isRefresh: Boolean): Flow<List<Chat>>
    fun getMessages(chatId: String, isRefresh: Boolean): Flow<List<Message>>
    fun getMembersCommunities(isRefresh: Boolean): Flow<List<User>>
    suspend fun createMessage(chatId: String, message: Message): Resource<Message>
    suspend fun createChat(message: Message, receiver: User): Resource<Message>
}