package id.forum.explore.usecase

import id.forum.core.post.domain.model.Post
import id.forum.core.post.domain.repository.PostRepository
import id.forum.core.user.domain.model.User
import kotlinx.coroutines.flow.Flow

internal class GetExplorePostsUseCase(private val postRepository: PostRepository) {

    fun execute(currentUser: User, isRefresh: Boolean): Flow<List<Post>> =
        postRepository.getFeedPosts(currentUser, isRefresh)
}
