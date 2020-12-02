package id.forum.post.domain.usecase

import id.forum.core.post.domain.model.Comment
import id.forum.core.post.domain.model.Post
import id.forum.post.domain.repository.CommentsRepository
import kotlinx.coroutines.flow.Flow

class GetCommentsPostUseCase(private val commentsRepository: CommentsRepository) {
    fun execute(postId: String, sortBy: Int, isRefresh: Boolean): Flow<Post> =
        commentsRepository.getCommentsPost(postId, sortBy, isRefresh)
}