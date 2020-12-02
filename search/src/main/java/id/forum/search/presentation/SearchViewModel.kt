package id.forum.search.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.forum.core.base.BaseViewModel
import id.forum.core.community.domain.model.Community
import id.forum.core.data.Resource
import id.forum.core.post.domain.model.Post
import id.forum.core.user.domain.model.User
import id.forum.search.domain.usecase.SearchCommunitiesUseCase
import id.forum.search.domain.usecase.SearchPostsUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class SearchViewModel(
    private val searchPostsUseCase: SearchPostsUseCase,
    private val searchCommunitiesUseCase: SearchCommunitiesUseCase,
    private val currentUser: User
) : BaseViewModel() {
    private val supervisorJob = SupervisorJob()
    private var _communities = MutableLiveData<Resource<List<Community>>>()
    val communities: LiveData<Resource<List<Community>>>
        get() = _communities
    private var _posts = MutableLiveData<Resource<List<Post>>>()
    val posts: LiveData<Resource<List<Post>>>
        get() = _posts

    fun searchCommunities(keyword: String) {
        uiScope.launch(getJobErrorHandler() + supervisorJob) {
            searchCommunitiesUseCase.execute(keyword)
                .onStart {
                    _communities.postValue(Resource.loading(_communities.value?.data))
                }
                .catch { cause ->
                    _communities.postValue(
                        Resource.error(
                            cause.message.toString(),
                            _communities.value?.data
                        )
                    )
                }
                .collect {
                    _communities.postValue(Resource.success(it))
                }
        }
    }

    fun searchPosts(keyword: String) {
        uiScope.launch(getJobErrorHandler() + supervisorJob) {
            searchPostsUseCase.execute(keyword)
                .onStart {
                    _posts.postValue(Resource.loading(_posts.value?.data))
                }
                .catch { cause ->
                    _posts.postValue(
                        Resource.error(
                            cause.message.toString(),
                            _posts.value?.data
                        )
                    )
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

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Log.e(SearchViewModel::class.java.simpleName, "An error happened: $e")
    }

//    override fun onCleared() {
//        super.onCleared()
//        _posts.postValue(Resource.success(_posts.value?.data))
//        _communities.postValue(Resource.success(_communities.value?.data))
//    }
}