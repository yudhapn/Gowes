package id.forum.post.data.repository

import id.forum.core.data.Resource
import id.forum.core.post.data.service.PostApolloService
import id.forum.core.post.domain.model.Comment
import id.forum.core.post.domain.model.Post
import id.forum.post.data.service.CommentApolloService
import id.forum.post.domain.repository.CommentsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import org.koin.core.KoinComponent
import org.koin.core.inject


@ExperimentalCoroutinesApi
class CommentsRepositoryImpl : CommentsRepository, KoinComponent {

    override fun getCommentsPost(
        postId: String,
        sortBy: Int,
        isRefresh: Boolean
    ): Flow<Post> {
        val commentService: CommentApolloService by inject()
        return commentService.getComments(postId, sortBy, isRefresh)
    }

    override suspend fun deleteComment(commentId: String, postId: String): Resource<Comment> {
        val commentService: CommentApolloService by inject()
        return commentService.deleteComment(commentId, postId)
    }

    override suspend fun createCommentsPost(postId: String, comment: Comment): Resource<Comment> {
        val commentService: CommentApolloService by inject()
        return commentService.createComment(postId, comment)
    }

    override suspend fun voteComment(
        currentUserId: String,
        comment: Comment,
        isUpVote: Boolean,
        isDelete: Boolean
    ): Resource<Comment> {
        val commentService: CommentApolloService by inject()
        return commentService.voteComment(currentUserId, comment, isUpVote, isDelete)

    }
}