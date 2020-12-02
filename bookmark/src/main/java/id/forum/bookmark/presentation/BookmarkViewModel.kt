package id.forum.bookmark.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.forum.bookmark.domain.usecase.GetBookmarkPostsUseCase
import id.forum.core.base.BaseViewModel
import id.forum.core.data.Resource
import id.forum.core.post.domain.model.Post
import id.forum.core.user.domain.model.User
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
internal class BookmarkViewModel(
    private val getBookmarkPostsUseCase: GetBookmarkPostsUseCase
) : BaseViewModel() {
    private var supervisorJob = SupervisorJob()
    private val _posts = MutableLiveData<Resource<List<Post>>>()
    val posts: LiveData<Resource<List<Post>>>
        get() = _posts

    init {
        getBookmarkPosts(false)
    }


    private fun getBookmarkPosts(isRefresh: Boolean) {
        Log.d("BookmarkViewModel", "get bookmark isRefresh: $isRefresh")
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            getBookmarkPostsUseCase.execute(isRefresh)
                .onStart {
                    _posts.postValue(Resource.loading(_posts.value?.data))
                }
                .catch { cause ->
                    _posts.postValue(Resource.error(cause.message.toString(), _posts.value?.data))
                }
                .collect {
                    _posts.postValue(Resource.success(it))
                    Log.d("BookmarkViewModel", "bookmark size: $${it.size}")
                }
        }
    }

    fun deleteBookmarkPost(post: Post) {
        val list = _posts.value?.data?.toMutableList()
        list?.remove(post)
        _posts.value = Resource.success(list)
    }

    fun refreshBookmark() {
        Log.d("BookmarkViewModel", "refresh bookmark")
        getBookmarkPosts(true)
    }

//   fun updateBookmarkedPost(post: Post, newValue: Boolean) {
//      post.bookmarkedBy.toMutableList().let { users ->
//         // if new value true then add bookmarked else remove bookmark
//         if (newValue) {
//            users.add(User(currentUser.id))
//         } else {
//            users.find { it.id == currentUser.id }?.let { user -> users.remove(user) }
//         }
//         post.bookmarkedBy = users
//      }
//      ioScope.launch(getJobErrorHandler() + supervisorJob) {
//         val user = updateCurrentUser(post.id)
//         repository.updateBookmarkedPost(post, user)
//      }
//      _posts.value?.data?.toMutableList()?.let {
//         it.forEachIndexed { index, _post ->
//            if (_post.id == post.id) {
//               it[index] = post
//               _posts.value = Resource.success(it)
//               return@forEachIndexed
//            }
//         }
//      }
//   }
//
//   private fun updateCurrentUser(postId: String): User {
//      val list = currentUser.bookmarkedPosts.toMutableList()
//      // if not null remove post else add post
//      list.find { it.id == postId }?.let { list.remove(it) } ?: list.add(Post(postId))
//      currentUser.bookmarkedPosts = list
//      return currentUser
//   }

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Log.e(BookmarkViewModel::class.java.simpleName, "An error happened: $e")
    }
}