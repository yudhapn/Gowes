package id.forum.post.domain.usecase

import id.forum.core.data.Resource
import id.forum.core.post.domain.model.Comment
import id.forum.post.domain.repository.CommentsRepository

class DeleteCommentUseCase(private val commentsRepository: CommentsRepository) {
    suspend fun execute(commentId: String, postId: String): Resource<Comment> = commentsRepository.deleteComment(commentId, postId)
}