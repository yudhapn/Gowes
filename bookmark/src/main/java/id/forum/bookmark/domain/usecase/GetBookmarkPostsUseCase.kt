package id.forum.bookmark.domain.usecase

import id.forum.core.post.domain.model.Post
import id.forum.core.post.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow

internal class GetBookmarkPostsUseCase(private val postRepository: PostRepository) {

    fun execute(isRefresh: Boolean): Flow<List<Post>> = postRepository.getBookmarkedPosts(isRefresh)
}
