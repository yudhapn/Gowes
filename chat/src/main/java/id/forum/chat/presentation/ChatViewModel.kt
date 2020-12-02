package id.forum.chat.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.forum.chat.domain.model.Chat
import id.forum.chat.domain.usecase.GetChatsUseCase
import id.forum.core.base.BaseViewModel
import id.forum.core.data.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class ChatViewModel(private val getChatsUseCase: GetChatsUseCase) : BaseViewModel() {
    private var supervisorJob = SupervisorJob()

    private val _chatsLiveData = MutableLiveData<Resource<List<Chat>>>()
    val chatsLiveData: LiveData<Resource<List<Chat>>>
        get() = _chatsLiveData

    init {
        getChats()
    }

    private fun getChats(isRefresh: Boolean = false) {
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            getChatsUseCase.execute(isRefresh)
                .onStart {
                    _chatsLiveData.postValue(Resource.loading(_chatsLiveData.value?.data))
                }
                .catch { cause ->
                    _chatsLiveData.postValue(
                        Resource.error(
                            cause.message.toString(),
                            _chatsLiveData.value?.data
                        )
                    )
                }
                .collect {
                    _chatsLiveData.postValue(Resource.success(it))
                }

        }
    }

    fun refreshChats() = getChats(isRefresh = true)

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Log.e(ChatViewModel::class.java.simpleName, "An error happened: $e")
    }
}
