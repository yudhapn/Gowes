package id.forum.core.post.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.forum.core.base.BaseViewModel
import id.forum.core.data.Resource
import id.forum.core.post.domain.model.Post
import id.forum.core.post.domain.usecase.BookmarkPostUseCase
import id.forum.core.post.domain.usecase.DeletePostUseCase
import id.forum.core.post.domain.usecase.VotePostUseCase
import id.forum.core.user.domain.model.User
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class PostViewModel(
    private val currentUser: User,
    private val votePostUseCase: VotePostUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val bookmarkPostUseCase: BookmarkPostUseCase
) :
    BaseViewModel() {
    private var supervisorJob = SupervisorJob()

    private val _status = MutableLiveData<Resource<Post>>()
    val status: LiveData<Resource<Post>>
        get() = _status

    fun votePost(post: Post, isUpVote: Boolean, isDelete: Boolean) {
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            _status.postValue(
                votePostUseCase.execute(
                    currentUserId = currentUser.id,
                    post = post,
                    isUpVote = isUpVote,
                    isDelete = isDelete
                )
            )
        }
    }

    fun deletePost(post: Post) {
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            _status.postValue(deletePostUseCase.execute(post.id))
        }
    }

    fun updateBookmarkedPost(post: Post, isBookmarked: Boolean, currentUser: User): User {
        post.isUpVoted = !isBookmarked // !isBookmarked is a new value
        val currentBookmark = currentUser.bookmarkedPosts.toMutableList()
        if (!isBookmarked) {
            currentBookmark.add(post.id)
        } else {
            currentBookmark.find { it == post.id }.let { postId ->
                currentBookmark.remove(postId)
            }
        }
        currentUser.bookmarkedPosts = currentBookmark

        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            bookmarkPostUseCase.execute(post.id, currentUser.id, !isBookmarked)
        }
        return currentUser
    }

    fun updateUpVotesAmount(post: Post, isUpVote: Boolean): Post {
        post.isUpVoted = !isUpVote
        // if have not click upVote then increase vote counter vice versa
        if (!isUpVote) {
            post.voteCount++
        } else {
            post.voteCount--
        }
        return post
    }

    fun updateDownVotesAmount(post: Post, isDownVote: Boolean): Post {
        post.isDownVoted = !isDownVote
        // if have not click downVote then decrease vote counter vice versa
        if (!isDownVote) {
            post.voteCount--
        } else {
            post.voteCount++
        }
        return post
    }

    fun checkDownVotes(post: Post): Boolean = post.isDownVoted

    fun checkUpVotes(post: Post): Boolean = post.isUpVoted

    fun updatePostVotesCurrentUser(
        currentUser: User,
        oldUpVote: Boolean,
        oldDownVote: Boolean,
        newPost: Post
    ): User {
        Log.d(
            "PostViewModel",
            "old upVote: $oldUpVote, old downVote: $oldDownVote, new upVote: ${newPost.isUpVoted}, new downVote: ${newPost.isDownVoted},"
        )
        when {
            oldDownVote && newPost.isUpVoted -> {
                Log.d("PostViewModel", "updatePostVotes case 5")
                val upVotes = currentUser.upVotePosts.toMutableList()
                val downVotes = currentUser.downVotePosts.toMutableList()
                currentUser.upVotePosts = updatePostVotes(upVotes, newPost.id)
                currentUser.downVotePosts = updatePostVotes(downVotes, newPost.id, true)
            }
            oldUpVote && newPost.isDownVoted -> {
                Log.d("PostViewModel", "updatePostVotes case 6")
                val upVotes = currentUser.upVotePosts.toMutableList()
                val downVotes = currentUser.downVotePosts.toMutableList()
                currentUser.upVotePosts = updatePostVotes(upVotes, newPost.id, true)
                currentUser.downVotePosts = updatePostVotes(downVotes, newPost.id)
            }

            !oldUpVote && newPost.isUpVoted -> {
                Log.d("PostViewModel", "updatePostVotes case 1")
                val upVotes = currentUser.upVotePosts.toMutableList()
                currentUser.upVotePosts = updatePostVotes(upVotes, newPost.id)
            }
            oldUpVote && !newPost.isUpVoted -> {
                Log.d("PostViewModel", "updatePostVotes case 2")
                val upVotes = currentUser.upVotePosts.toMutableList()
                currentUser.upVotePosts = updatePostVotes(upVotes, newPost.id, true)
            }
            !oldDownVote && newPost.isDownVoted -> {
                Log.d("PostViewModel", "updatePostVotes case 3")
                val downVotes = currentUser.downVotePosts.toMutableList()
                currentUser.downVotePosts = updatePostVotes(downVotes, newPost.id)
            }
            oldDownVote && !newPost.isDownVoted -> {
                Log.d("PostViewModel", "updatePostVotes case 4")
                val downVotes = currentUser.downVotePosts.toMutableList()
                currentUser.downVotePosts = updatePostVotes(downVotes, newPost.id, true)
            }
        }
        return currentUser
    }

    private fun updatePostVotes(
        votes: MutableList<String>,
        newPostId: String,
        isRemove: Boolean = false
    ): MutableList<String> {
        if (!isRemove) {
            votes.add(newPostId)
        } else {
            votes.remove(newPostId)
        }
        return votes
    }

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Log.e(PostViewModel::class.java.simpleName, "An error happened: $e")
    }

}