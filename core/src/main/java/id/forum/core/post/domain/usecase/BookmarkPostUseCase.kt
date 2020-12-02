package id.forum.core.post.domain.usecase

import id.forum.core.data.Resource
import id.forum.core.post.domain.model.Post
import id.forum.core.post.domain.repository.PostRepository

class BookmarkPostUseCase(private val postRepository: PostRepository) {
    suspend fun execute(
        postId: String,
        userId: String,
        isBookmarked: Boolean
    ): Resource<Post> = postRepository.bookmarkPost(postId, userId, isBookmarked)
}