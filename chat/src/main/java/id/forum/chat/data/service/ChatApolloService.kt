package id.forum.chat.data.service

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.exception.ApolloException
import id.forum.chat.data.mapper.mapToDomain
import id.forum.chat.domain.model.Chat
import id.forum.chat.domain.model.Message
import id.forum.core.*
import id.forum.core.community.data.mapper.mapToDomain
import id.forum.core.data.Resource
import id.forum.core.data.Token
import id.forum.core.type.ChatInsertInput
import id.forum.core.type.ChatUsersRelationInput
import id.forum.core.type.MessageInsertInput
import id.forum.core.type.MessageUserRelationInput
import id.forum.core.user.domain.model.User
import id.forum.core.util.apolloResponseFetchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

@ExperimentalCoroutinesApi
class ChatApolloService(private val token: Token, private val currentUser: User) : KoinComponent {
    private val apolloClient: ApolloClient by inject { parametersOf(token) }

    fun userChats(isRefresh: Boolean): Flow<List<Chat>> = apolloClient
        .query(ChatQuery(Input.fromNullable(currentUser.id)))
        .responseFetcher(isRefresh.apolloResponseFetchers())
        .watcher()
        .toFlow()
        .map { response ->
            response.data?.chats?.map {
                it?.fragments?.chatDetails?.mapToDomain() ?: Chat()
            } ?: emptyList()
        }

    fun membersCommunities(isRefresh: Boolean): Flow<List<User>> = apolloClient
        .query(MembersByCommunityIdsQuery(Input.fromNullable(currentUser.communities.map { it.id })))
        .responseFetcher(isRefresh.apolloResponseFetchers())
        .watcher()
        .toFlow()
        .map { response ->
            response.data?.getUserListChatQuery?.let { list ->
                Log.d("ChatApolloService", "communities response size: ${list.size}")
                list.map { it?.fragments?.communityMemberDetails?.mapToDomain() ?: User() }
            } ?: emptyList()
        }

    fun messages(idChat: String, isRefresh: Boolean): Flow<List<Message>> = apolloClient
        .query(MessageQuery(Input.fromNullable(idChat)))
        .responseFetcher(isRefresh.apolloResponseFetchers())
        .watcher()
        .toFlow()
        .map { response ->
            response.data?.messages?.map {
                it?.fragments?.messageDetails?.mapToDomain() ?: Message()
            } ?: emptyList()
        }

    suspend fun createChat(
        message: Message,
        receiver: User
    ): Resource<Message> {
        val users = listOf(currentUser, receiver)
        val createChatMutation = CreateChatMutation(
            ChatInsertInput(
                lastText = Input.fromNullable(message.content),
                users = Input.fromNullable(
                    ChatUsersRelationInput(
                        link = Input.fromNullable(users.map { user -> user.id })
                    )
                )
            )
        )
        try {
            val chatData =
                apolloClient.mutate(createChatMutation)
                    .refetchQueries(ChatQuery(Input.fromNullable(currentUser.id)))
                    .toDeferred()
                    .await()
            if (chatData.hasErrors()) {
                return Resource.error("Failed to create chat", null)
            }
            chatData.data?.insertOneChat.let {
                val newChat = it?.fragments?.chatDetails?.mapToDomain() ?: Chat()
                val createMessageMutation = CreateMessageMutation(
                    chatId = newChat.id,
                    lastText = message.content,
                    message = MessageInsertInput(
                        content = Input.fromNullable(message.content),
                        user = Input.fromNullable(
                            MessageUserRelationInput(
                                link = Input.fromNullable(message.senderId)
                            )
                        ),
                        chat = Input.fromNullable(newChat.id),
                        sentOn = Input.fromNullable(message.sentOn)
                    )
                )
                val messageData = apolloClient.mutate(createMessageMutation).toDeferred().await()
                if (messageData.hasErrors()) {
                    return Resource.error("Failed to create message", null)
                }

                messageData.data?.insertOneMessage.let {
                    val newMessage = it?.fragments?.messageDetails?.mapToDomain() ?: Message()
                    return Resource.success(newMessage)
                }
            }
        } catch (e: ApolloException) {
            throw Exception("ChatApolloService, error happened: ${e.message}")
        }
    }

    suspend fun createMessage(chatId: String, message: Message): Resource<Message> {
        val createMessageMutation = CreateMessageMutation(
            chatId = chatId,
            lastText = message.content,
            message = MessageInsertInput(
                content = Input.fromNullable(message.content),
                user = Input.fromNullable(
                    MessageUserRelationInput(
                        link = Input.fromNullable(message.senderId)
                    )
                ),
                chat = Input.fromNullable(chatId),
                sentOn = Input.fromNullable(message.sentOn)
            )
        )

        try {
            val messageData =
                apolloClient.mutate(createMessageMutation)
                    .refetchQueries(
                        ChatQuery(Input.fromNullable(currentUser.id)),
                        MessageQuery(Input.fromNullable(chatId))
                    )
                    .toDeferred()
                    .await()
            if (messageData.hasErrors()) {
                return Resource.error("Failed to create message", null)
            }
            messageData.data?.insertOneMessage.let {
                val newMessage = it?.fragments?.messageDetails?.mapToDomain() ?: Message()
                return Resource.success(newMessage)
            }
        } catch (e: ApolloException) {
            throw Exception("ChatApolloService, error happened: ${e.message}")
        }
    }
}