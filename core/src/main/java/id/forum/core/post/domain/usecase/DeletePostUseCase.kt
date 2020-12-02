package id.forum.core.post.domain.usecase

import id.forum.core.data.Resource
import id.forum.core.post.domain.model.Post
import id.forum.core.post.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow

class DeletePostUseCase(private val postRepository: PostRepository) {

    suspend fun execute(postId: String): Resource<Post> = postRepository.deletePost(postId)
}
