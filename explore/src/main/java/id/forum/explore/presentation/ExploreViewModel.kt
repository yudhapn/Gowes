package id.forum.explore.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import id.forum.core.base.BaseViewModel
import id.forum.core.community.domain.model.Community
import id.forum.core.data.Resource
import id.forum.core.post.domain.model.Post
import id.forum.core.post.domain.usecase.GetExplorePostsUseCase
import id.forum.core.post.presentation.PostUiModel
import id.forum.core.user.domain.model.User
import id.forum.explore.usecase.GetExploreCommunitiesUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import java.util.*

@ExperimentalCoroutinesApi
internal class ExploreViewModel(
    requestDataType: Int,
    private val getExploreCommunitiesUseCase: GetExploreCommunitiesUseCase,
    private val getExplorePostsUseCase: GetExplorePostsUseCase,
    private val currentUser: User
) : BaseViewModel(), KoinComponent {
    private var supervisorJob = SupervisorJob()

    private val _posts = MutableLiveData<Resource<List<Post>>>()
    val postsLiveData: LiveData<Resource<List<Post>>>
        get() = _posts

    var postsPaging: Flow<PagingData<PostUiModel>>? = null

    private val _communities = MutableLiveData<Resource<List<Community>>>()
    val communitiesLiveData: LiveData<Resource<List<Community>>>
        get() = _communities

    init {
        if (requestDataType == 0) {
            getPosts()
        } else {
            getCommunities()
        }
    }

    private fun getPosts(isRefresh: Boolean = false) {
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
//            getExplorePostsUseCase.execute(currentUser, isRefresh)
//                .onStart {
//                    _posts.postValue(Resource.loading(_posts.value?.data))
//                }
//                .catch { cause ->
//                    _posts.postValue(Resource.error(cause.message.toString(), _posts.value?.data))
//                }
//                .collect { posts ->
//                    _posts.postValue(Resource.success(posts))
//                }
            var monthCounter = 1
            postsPaging = getExplorePostsUseCase.execute(isRefresh)
                .map { pagingData -> pagingData.map { PostUiModel.PostItem(it) } }
                .map {
                    it.insertSeparators { before, after ->
                        Log.d("ExploreViewModel", "post before item title: ${before?.post?.title}")
                        Log.d("ExploreViewModel", "post after item title: ${after?.post?.title}")
                        if (after == null) {
                            return@insertSeparators null
                        }
                        if (before == null) {
                            // we're at the beginning of the list
                            return@insertSeparators PostUiModel.SeparatorItem("THIS MONTH")
                        }
                        val calendar = Calendar.getInstance()
                        val dateStart = calendar.time

                        // set date to one month ago
                        calendar.time = dateStart
                        calendar.add(Calendar.MONTH, -monthCounter)
                        val dateEnd = calendar.time

                        if (after.post.createdOn.time < dateStart.time && after.post.createdOn.time > dateEnd.time) {
                            // no separator
                            null
                        } else {
                            Log.d("ExploreViewModel", "Add separator ")
                            PostUiModel.SeparatorItem(if (monthCounter == 1) "$monthCounter MONTH AGO" else "$monthCounter MONTHS AGO")
                                .apply {
                                    monthCounter++
                                }

                        }
                    }
                }
        }
    }

    private fun getCommunities(isRefresh: Boolean = false) {
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            getExploreCommunitiesUseCase.execute()
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

    fun deletePost(post: Post) {
        val list = _posts.value?.data?.toMutableList()
        list?.remove(post)
        _posts.value = Resource.success(list)
    }


    fun refreshCommunityData() = getCommunities(true)

    fun refreshPostData() = getPosts(true)

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Log.e(ExploreViewModel::class.java.simpleName, "An error happened: $e")
        _communities.postValue(Resource.error(e.message.toString(), _communities.value?.data))
    }
}