package id.forum.profile.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.forum.core.base.BaseViewModel
import id.forum.core.data.Resource
import id.forum.core.post.domain.model.Post
import id.forum.core.user.domain.model.User
import id.forum.profile.domain.usecase.GetUserProfileUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class ProfileViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val profileUser: User,
    private val currentUser: User
) : BaseViewModel() {
    private var supervisorJob = SupervisorJob()

    private val _user = MutableLiveData<Resource<User>>()
    val usr: LiveData<Resource<User>>
        get() = _user

    private val _posts = MutableLiveData<Resource<List<Post>>>()
    val posts: LiveData<Resource<List<Post>>>
        get() = _posts

    private val _chatId = MutableLiveData<Resource<String>>()
    val chatId: LiveData<Resource<String>>
        get() = _chatId

    init {
        _user.postValue(Resource.success(profileUser))
        getUserProfile()
    }

    private fun getUserProfile(isRefresh: Boolean = false) {
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            getUserProfileUseCase.execute(profileUser.id, currentUser.id, isRefresh)
                .onStart {
                    _user.postValue(Resource.loading(_user.value?.data))
                    _posts.postValue(Resource.loading(_posts.value?.data))
                    _chatId.postValue(Resource.loading(_chatId.value?.data))
                }
                .catch { cause ->
                    _user.postValue(Resource.error(cause.message.toString(), _user.value?.data))
                    _posts.postValue(Resource.error(cause.message.toString(), _posts.value?.data))
                    _chatId.postValue(Resource.error(cause.message.toString(), _chatId.value?.data))
                }
                .collect {
                    _user.postValue(Resource.success(it))
                    _posts.postValue(Resource.success(it.posts))
                    if (it.chats.isNotEmpty()) {
                        _chatId.postValue(Resource.success(it.chats[0]))
                    } else {
                        _chatId.postValue(Resource.success(""))
                    }
                }
        }
    }

    fun refreshData() {
        getUserProfile(true)
    }

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        _user.postValue(Resource.error("$e", null))
        Log.e(ProfileViewModel::class.java.simpleName, "An error happened: $e")
    }
}