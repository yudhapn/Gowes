package id.forum.post.presentation.comment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import id.forum.core.base.BaseViewModel
import id.forum.core.data.Resource
import id.forum.core.post.domain.model.Comment
import id.forum.core.user.domain.model.User
import id.forum.post.domain.usecase.CreateCommentsPostUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import org.koin.core.KoinComponent

@ExperimentalCoroutinesApi
class CreateCommentViewModel(
    private val createCommentsPostUseCase: CreateCommentsPostUseCase,
    private val currentUser: User
) : BaseViewModel(), KoinComponent {
    private var supervisorJob = SupervisorJob()

    fun insertCommentsPost(postId: String, content: String): MutableLiveData<Resource<Comment>> {
        val comment = Comment(
            id = ObjectId().toString(),
            user = currentUser,
            content = content
        )
        val commentLiveData = MutableLiveData<Resource<Comment>>(Resource.loading(comment))
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            val comment = Comment(
                id = ObjectId().toString(),
                user = currentUser,
                content = content
            )
            commentLiveData.postValue(createCommentsPostUseCase.execute(postId, comment))
        }
        return commentLiveData
    }

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Log.e(CreateCommentViewModel::class.java.simpleName, "An error happened: $e")
    }
}