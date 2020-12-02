package id.forum.chat.presentation.room

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.forum.chat.domain.model.Message
import id.forum.chat.domain.usecase.CreateChatUseCase
import id.forum.chat.domain.usecase.CreateMessageUseCase
import id.forum.chat.domain.usecase.GetMessagesUseCase
import id.forum.core.base.BaseViewModel
import id.forum.core.data.Resource
import id.forum.core.data.Status
import id.forum.core.user.domain.model.User
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class ChatRoomViewModel(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val createMessageUseCase: CreateMessageUseCase,
    private val createChatUseCase: CreateChatUseCase,
    private val chatId: String,
    private val receiver: User
) : BaseViewModel() {
    private var supervisorJob = SupervisorJob()

    private val _messages = MutableLiveData<Resource<List<Message>>>()
    val messages: LiveData<Resource<List<Message>>>
        get() = _messages

    init {
        if (chatId != "new_chat") {
            getMessages()
        }
    }

    private fun getMessages(isRefresh: Boolean = false) {
        if (chatId == "new_chat") {
            _messages.postValue(Resource.success(_messages.value?.data))
        } else {
            ioScope.launch(getJobErrorHandler() + supervisorJob) {
                getMessagesUseCase.execute(chatId, isRefresh)
                    .onStart {
                        _messages.postValue(Resource.loading(_messages.value?.data))
                    }
                    .catch { cause ->
                        _messages.postValue(
                            Resource.error(
                                cause.message.toString(),
                                _messages.value?.data
                            )
                        )
                    }
                    .collect {
                        _messages.postValue(Resource.success(it))
                    }
            }
        }
    }

    fun sendMessage(content: String, currentUserId: String) {
        val message = Message(
            senderId = currentUserId,
            content = content
        )
        val messageList = _messages.value?.data?.toMutableList() ?: mutableListOf()
        messageList.add(message)
        _messages.value = Resource.loading(messageList)
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            val messageData = if (chatId == "new_chat") {
                createChatUseCase.execute(message, receiver)
            } else {
                createMessageUseCase.execute(chatId, message)
            }
            when (messageData.status) {
                Status.SUCCESS -> _messages.postValue(Resource.success(messageList))
                else -> _messages.postValue(
                    Resource.error(
                        messageData.message.toString(),
                        messageList
                    )
                )
            }

        }
    }

    fun refreshMessages(){
        getMessages()
    }

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Log.e(ChatRoomViewModel::class.java.simpleName, "An error happened: $e")
    }
}
