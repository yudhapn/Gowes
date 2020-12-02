package id.forum.core.post.domain.usecase

import id.forum.core.data.Resource
import id.forum.core.post.domain.model.Post
import id.forum.core.post.domain.repository.PostRepository

class VotePostUseCase(private val postRepository: PostRepository) {
    suspend fun execute(
        currentUserId: String,
        post: Post,
        isUpVote: Boolean,
        isDelete: Boolean
    ): Resource<Post> =
        postRepository.votePost(
            currentUserId = currentUserId,
            post = post,
            isUpVote = isUpVote,
            isDelete = isDelete
        )
}