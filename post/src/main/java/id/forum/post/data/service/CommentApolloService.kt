package id.forum.post.data.service

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.exception.ApolloException
import id.forum.core.CommentsQuery
import id.forum.core.DeleteCommentMutation
import id.forum.core.InsertCommentMutation
import id.forum.core.VoteCommentMutation
import id.forum.core.data.Resource
import id.forum.core.data.Token
import id.forum.core.post.data.mapper.mapToDomain
import id.forum.core.post.domain.model.Comment
import id.forum.core.post.domain.model.Post
import id.forum.core.type.CommentInsertInput
import id.forum.core.type.CommentSortByInput
import id.forum.core.type.CommentSortByInput.UPDATEDON_DESC
import id.forum.core.type.CommentUserRelationInput
import id.forum.core.type.CommentVoteInsertInput
import id.forum.core.user.domain.model.User
import id.forum.core.util.apolloResponseFetchers
import id.forum.post.COMMENT_SORT_BY_UPDATED_ON
import id.forum.post.COMMENT_SORT_BY_VOTE_COUNT
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import java.util.*

@ExperimentalCoroutinesApi
class CommentApolloService(
    private val currentUser: User,
    private val token: Token
) : KoinComponent {
    private val apolloClient: ApolloClient by inject { parametersOf(token) }

    fun getComments(postId: String, sortBy: Int, isRefresh: Boolean): Flow<Post> {
        val sortByInput = when (sortBy) {
            COMMENT_SORT_BY_UPDATED_ON -> UPDATEDON_DESC
            COMMENT_SORT_BY_VOTE_COUNT -> CommentSortByInput.VOTECOUNT_DESC
            else -> CommentSortByInput.VOTECOUNT_DESC
        }
        return apolloClient
            .query(CommentsQuery(Input.fromNullable(postId), Input.fromNullable(sortByInput)))
            .responseFetcher(isRefresh.apolloResponseFetchers())
            .watcher()
            .toFlow()
            .map { response ->
                val post =
                    response.data?.post?.fragments?.postDetails?.mapToDomain(currentUser) ?: Post()
                val comments = response.data?.comments?.map {
                    it?.fragments?.commentDetails?.mapToDomain() ?: Comment()
                } ?: emptyList()
                post.comments = comments
                post
            }
    }


    suspend fun createComment(postId: String, comment: Comment): Resource<Comment> {
        val mutation = InsertCommentMutation(
            CommentInsertInput(
                content = Input.fromNullable(comment.content),
                post = Input.fromNullable(postId),
                user = Input.fromNullable(
                    CommentUserRelationInput(
                        link = Input.fromNullable(comment.user.id)
                    )
                ),
                voteCount = Input.fromNullable(0),
                createdOn = Input.fromNullable(Calendar.getInstance().time),
                updatedOn = Input.fromNullable(Calendar.getInstance().time)
            )
        )
        try {
            val response = apolloClient
                .mutate(mutation)
                .refetchQueries(
                    CommentsQuery(
                        Input.fromNullable(postId), Input.fromNullable(UPDATEDON_DESC)
                    )
                )
                .toDeferred().await()
            response.data?.insertCommentIncreaseCounterMutation?.fragments?.commentDetails?.let {
                val updatedComment = it.mapToDomain()
                return Resource.success(updatedComment)
            } ?: run {
                throw IllegalStateException("unknown error in CommentApolloService")
            }
        } catch (e: ApolloException) {
            Log.e("CommentApolloService","error happened: ${e.message}")
            return Resource.error("Something wrong, please try again later", null)
        }
    }

    suspend fun deleteComment(commentId: String, postId: String): Resource<Comment> {
        Log.d("deleteComment", "commentId: $commentId, postId: $postId")
        try {
            val response = apolloClient
                .mutate(
                    DeleteCommentMutation(
                        commentId = Input.fromNullable(commentId),
                        postId = Input.fromNullable(postId)
                    )
                )
                .toDeferred().await()

            if (response.hasErrors()) {
                return Resource.error(
                    response.errors?.get(0)?.message ?: "Something wrong, please try again later",
                    null
                )
            }
            return Resource.success(null)
        } catch (e: ApolloException) {
            Log.e("CommentApolloService","error happened: ${e.message}")
            return Resource.error("Something wrong, please try again later", null)
        }
    }

    suspend fun voteComment(
        currentUserId: String,
        comment: Comment,
        isUpVote: Boolean,
        isDelete: Boolean
    ): Resource<Comment> {
        val mutation = VoteCommentMutation(
            userId = Input.fromNullable(currentUserId),
            commentId = Input.fromNullable(comment.id),
            vote = CommentVoteInsertInput(
                comment = Input.fromNullable(comment.id),
                user = Input.fromNullable(currentUserId),
                upVote = Input.fromNullable(isUpVote),
                isDelete = Input.fromNullable(isDelete),
                votedOn = Input.fromNullable(Calendar.getInstance().time)
            )
        )
        try {
            val response = apolloClient
                .mutate(mutation)
                .toDeferred().await()

            if (response.hasErrors()) {
                return Resource.error("${response.errors}", null)
            }
            return Resource.success(null)
        } catch (e: ApolloException) {
            throw Exception("PostApolloService, error happened: ${e.message}")
        }
    }
}
