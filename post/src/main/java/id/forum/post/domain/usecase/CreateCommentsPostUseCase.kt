package id.forum.post.domain.usecase

import id.forum.core.data.Resource
import id.forum.core.post.domain.model.Comment
import id.forum.post.domain.repository.CommentsRepository

class CreateCommentsPostUseCase(private val commentsRepository: CommentsRepository) {
    suspend fun execute(postId: String, comment: Comment): Resource<Comment> =
        commentsRepository.createCommentsPost(postId, comment)
}