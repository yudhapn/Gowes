package id.forum.post.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.forum.core.base.BaseViewModel
import id.forum.core.data.Resource
import id.forum.core.data.Status.*
import id.forum.core.post.domain.model.Comment
import id.forum.core.post.domain.model.Post
import id.forum.core.user.domain.model.User
import id.forum.post.COMMENT_SORT_BY_VOTE_COUNT
import id.forum.post.domain.usecase.DeleteCommentUseCase
import id.forum.post.domain.usecase.GetCommentsPostUseCase
import id.forum.post.domain.usecase.VoteCommentUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class PostViewModel(
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val getCommentsPostUseCase: GetCommentsPostUseCase,
    private val voteCommentUseCase: VoteCommentUseCase,
    private val postArg: Post,
    private val currentUser: User
) : BaseViewModel() {
    private var supervisorJob = SupervisorJob()

    private val _comments = MutableLiveData<Resource<List<Comment>>>()
    val comments: LiveData<Resource<List<Comment>>>
        get() = _comments

    private val _post = MutableLiveData<Resource<Post>>()
    val post: LiveData<Resource<Post>>
        get() = _post

    init {
        _post.postValue(Resource.success(postArg))
        getComments(postArg.id)
    }

    fun getComments(
        postId: String = this.postArg.id,
        sortBy: Int = COMMENT_SORT_BY_VOTE_COUNT,
        isRefresh: Boolean = false
    ) {
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            getCommentsPostUseCase.execute(postId, sortBy, isRefresh)
                .onStart {
                    _comments.postValue(Resource.loading(_comments.value?.data))
                }
                .catch { cause ->
                    _comments.postValue(Resource.error(cause.message.toString(), null))
                }
                .collect { post ->
                    Log.d("PostViewModel", "getComments collect")
                    val list = post.comments.map { comment ->
                        currentUser.upVoteComments.forEach { upVoteCommentId ->
                            if (comment.id.equals(upVoteCommentId, true)) {
                                comment.isUpVoted = true
                                return@forEach
                            }
                        }
                        currentUser.downVoteComments.forEach { downVoteCommentId ->
                            if (comment.id.equals(downVoteCommentId, true)) {
                                comment.isDownVoted = true
                                return@forEach
                            }
                        }
                        comment
                    }
                    _post.postValue(Resource.success(post))
                    _comments.postValue(Resource.success(list))
                }

        }
    }

    fun voteComment(comment: Comment, isUpVote: Boolean, isDelete: Boolean) {
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            voteCommentUseCase.execute(
                currentUserId = currentUser.id,
                comment = comment,
                isUpVote = isUpVote,
                isDelete = isDelete
            )
            _comments.postValue(Resource.success(_comments.value?.data))
        }
    }

    fun updateUpVotesAmount(comment: Comment, isUpVote: Boolean): Comment {
        comment.isUpVoted = !isUpVote
        // if have not click upVote then increase vote counter vice versa
        if (!isUpVote) {
            comment.voteCount++
        } else {
            comment.voteCount--
        }
        return comment
    }

    fun updateDownVotesAmount(comment: Comment, isDownVote: Boolean): Comment {
        comment.isDownVoted = !isDownVote
        // if have not click downVote then decrease vote counter vice versa
        if (!isDownVote) {
            comment.voteCount--
        } else {
            comment.voteCount++
        }
        return comment
    }

    fun updateCommentVotesCurrentUser(
        currentUser: User,
        oldUpVote: Boolean,
        oldDownVote: Boolean,
        newComment: Comment
    ): User {
        Log.d(
            "PostViewModel",
            "old upVote: $oldUpVote, old downVote: $oldDownVote, new upVote: ${newComment.isUpVoted}, new downVote: ${newComment.isDownVoted},"
        )
        when {
            oldDownVote && newComment.isUpVoted -> {
                Log.d("PostViewModel", "updateCommentVotes case 5")
                val upVotes = currentUser.upVoteComments.toMutableList()
                val downVotes = currentUser.downVoteComments.toMutableList()
                currentUser.upVoteComments = updateCommentVotes(upVotes, newComment.id)
                currentUser.downVoteComments = updateCommentVotes(downVotes, newComment.id, true)
            }
            oldUpVote && newComment.isDownVoted -> {
                Log.d("PostViewModel", "updateCommentVotes case 6")
                val upVotes = currentUser.upVoteComments.toMutableList()
                val downVotes = currentUser.downVoteComments.toMutableList()
                currentUser.upVoteComments = updateCommentVotes(upVotes, newComment.id, true)
                currentUser.downVoteComments = updateCommentVotes(downVotes, newComment.id)
            }

            !oldUpVote && newComment.isUpVoted -> {
                Log.d("PostViewModel", "updateCommentVotes case 1")
                val upVotes = currentUser.upVoteComments.toMutableList()
                currentUser.upVoteComments = updateCommentVotes(upVotes, newComment.id)
            }
            oldUpVote && !newComment.isUpVoted -> {
                Log.d("PostViewModel", "updateCommentVotes case 2")
                val upVotes = currentUser.upVoteComments.toMutableList()
                currentUser.upVoteComments = updateCommentVotes(upVotes, newComment.id, true)
            }
            !oldDownVote && newComment.isDownVoted -> {
                Log.d("PostViewModel", "updateCommentVotes case 3")
                val downVotes = currentUser.downVoteComments.toMutableList()
                currentUser.downVoteComments = updateCommentVotes(downVotes, newComment.id)
            }
            oldDownVote && !newComment.isDownVoted -> {
                Log.d("PostViewModel", "updateCommentVotes case 4")
                val downVotes = currentUser.downVoteComments.toMutableList()
                currentUser.downVoteComments = updateCommentVotes(downVotes, newComment.id, true)
            }
        }
        return currentUser
    }

    private fun updateCommentVotes(
        votes: MutableList<String>,
        newCommentId: String,
        isRemove: Boolean = false
    ): MutableList<String> {
        if (!isRemove) {
            votes.add(newCommentId)
        } else {
            votes.remove(newCommentId)
        }
        return votes
    }

    fun updateVotesComment(post: Post, comment: Comment) {
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            _comments.value?.data?.toMutableList()?.let {
                it.forEachIndexed { index, _comment ->
                    if (_comment.id == comment.id) {
                        it[index] = comment
//                        repository.updateCommentsPost(post, it)
                        return@forEachIndexed
                    }
                }
            }
        }
    }

    fun deleteComment(comment: Comment): LiveData<Resource<List<Comment>>> {
        val oldList = _comments.value?.data?.toMutableList()
        val newList = _comments.value?.data?.toMutableList()
        newList?.remove(comment)
        _comments.postValue(Resource.loading(newList))
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            val commentData = deleteCommentUseCase.execute(comment.id, postArg.id)
            when (commentData.status) {
                SUCCESS -> _comments.postValue(Resource.success(newList))
                ERROR -> _comments.postValue(
                    Resource.error(
                        commentData.message.toString(),
                        oldList
                    )
                )
                LOADING -> Unit
            }
        }
        return _comments
    }

    fun refreshData() {
        getComments(postArg.id, isRefresh = true)
    }

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Log.e(PostViewModel::class.java.simpleName, "An error happened: $e")
        _comments.postValue(Resource.error("$e", null))
    }
}