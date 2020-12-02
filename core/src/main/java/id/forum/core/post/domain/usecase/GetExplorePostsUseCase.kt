package id.forum.core.post.domain.usecase

import androidx.paging.PagingData
import id.forum.core.post.domain.model.Post
import id.forum.core.post.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow

class GetExplorePostsUseCase(private val postRepository: PostRepository) {

    fun execute(isRefresh: Boolean): Flow<PagingData<Post>> = postRepository.getPosts(isRefresh)
}
