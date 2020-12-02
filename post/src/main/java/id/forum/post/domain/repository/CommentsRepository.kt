package id.forum.post.domain.repository

import id.forum.core.data.Resource
import id.forum.core.post.domain.model.Comment
import id.forum.core.post.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface CommentsRepository {
    fun getCommentsPost(postId: String, sortBy: Int, isRefresh: Boolean): Flow<Post>
    suspend fun deleteComment(commentId: String, postId: String): Resource<Comment>
    suspend fun createCommentsPost(postId: String, comment: Comment): Resource<Comment>
    suspend fun voteComment(currentUserId: String, comment: Comment, isUpVote: Boolean, isDelete: Boolean): Resource<Comment>
}