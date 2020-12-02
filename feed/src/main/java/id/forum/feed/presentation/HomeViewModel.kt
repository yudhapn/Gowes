package id.forum.feed.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.forum.core.base.BaseViewModel
import id.forum.core.data.Resource
import id.forum.core.post.domain.model.Post
import id.forum.core.user.domain.model.User
import id.forum.feed.domain.usecase.GetFeedPostsUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
internal class HomeViewModel(
    private val getFeedPostsUseCase: GetFeedPostsUseCase,
    val currentUser: User
) : BaseViewModel() {
    private var supervisorJob = SupervisorJob()

    private val _posts = MutableLiveData<Resource<List<Post>>>()
    val posts: LiveData<Resource<List<Post>>>
        get() = _posts

    init {
        if (currentUser.id.isNotBlank()) {
            getPostsByCommunity(currentUser)
        }
    }

    fun getPostsByCommunity(currentUser: User, isRefresh: Boolean = false) {
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            getFeedPostsUseCase.execute(currentUser, isRefresh)
                .onStart {
                    _posts.postValue(Resource.loading(_posts.value?.data))
                }
                .catch { cause ->
                    _posts.postValue(Resource.error(cause.message.toString(), _posts.value?.data))
                }
                .collect { posts ->
                    _posts.postValue(Resource.success(posts))
                }
        }
    }

    fun deletePost(post: Post) {
        val list = _posts.value?.data?.toMutableList()
        list?.remove(post)
        _posts.value = Resource.success(list)
    }

    fun refreshHome(currentUser: User = User()) {
        Log.d("HomeViewModel", "refresh home")
        getPostsByCommunity(currentUser, true)
    }

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Log.e(HomeViewModel::class.java.simpleName, "An error happened: $e")
    }
}