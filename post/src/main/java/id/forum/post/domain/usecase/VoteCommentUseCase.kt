package id.forum.post.domain.usecase

import id.forum.core.data.Resource
import id.forum.core.post.domain.model.Comment
import id.forum.core.post.domain.model.Post
import id.forum.post.domain.repository.CommentsRepository

class VoteCommentUseCase(private val commentsRepository: CommentsRepository) {
    suspend fun execute(
        currentUserId: String,
        comment: Comment,
        isUpVote: Boolean,
        isDelete: Boolean
    ): Resource<Comment> =
        commentsRepository.voteComment(
            currentUserId = currentUserId,
            comment = comment,
            isUpVote = isUpVote,
            isDelete = isDelete
        )
}