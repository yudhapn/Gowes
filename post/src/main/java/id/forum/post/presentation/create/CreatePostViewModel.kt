package id.forum.post.presentation.create

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.forum.core.base.BaseViewModel
import id.forum.core.community.domain.model.Community
import id.forum.core.data.Resource
import id.forum.core.media.domain.model.Image
import id.forum.core.post.domain.model.Post
import id.forum.core.user.domain.model.User
import id.forum.post.domain.usecase.CompressMediaUseCase
import id.forum.post.domain.usecase.CreatePostUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent

@ExperimentalCoroutinesApi
class CreatePostViewModel(
    private val currentUser: User,
    private val createPostUseCase: CreatePostUseCase,
    private val compressMediaUseCase: CompressMediaUseCase
) : BaseViewModel(), KoinComponent {
    private var supervisorJob = SupervisorJob()

    fun createPost(
        user: User, community: Community?, subject: String, body: String, mediaList: List<Image>
    ) =
        createPostUseCase.execute(
            Post("", user, community ?: Community(), subject, body), mediaList
        )


    fun prepareMedia(images: List<Image>): LiveData<Resource<List<Image>>> {
        val mediaCompressed = MutableLiveData<Resource<List<Image>>>()
        mediaCompressed.postValue(Resource.loading(null))
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            mediaCompressed.postValue(compressMediaUseCase.execute(images))
        }
        return mediaCompressed
    }

    fun getCurrentUser() = currentUser

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Log.e(CreatePostViewModel::class.java.simpleName, "An error happened: $e")
    }
}