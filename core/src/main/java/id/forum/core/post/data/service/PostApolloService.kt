package id.forum.core.post.data.service

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.exception.ApolloException
import id.forum.core.*
import id.forum.core.data.Resource
import id.forum.core.data.Token
import id.forum.core.post.data.mapper.mapToDomain
import id.forum.core.post.domain.model.Post
import id.forum.core.type.BookmarkInsertInput
import id.forum.core.type.BookmarkPostRelationInput
import id.forum.core.type.PostVoteInsertInput
import id.forum.core.user.domain.model.User
import id.forum.core.util.apolloResponseFetchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import java.util.*

@ExperimentalCoroutinesApi
class PostApolloService(private val token: Token, private val currentUser: User) : KoinComponent {
    private val apolloClient: ApolloClient by inject { parametersOf(token) }

    fun getBookmarkedPosts(isRefresh: Boolean): Flow<List<Post>> = apolloClient
        .query(BookmarkedPostsByUserIdQuery(Input.fromNullable(currentUser.id)))
        .responseFetcher(isRefresh.apolloResponseFetchers())
        .watcher()
        .toFlow()
        .map { response ->
            response.data?.bookmarks?.map {
                it?.fragments?.bookmarkDetails?.post?.mapToDomain(currentUser) ?: Post()
            } ?: emptyList()
        }

    fun getFeedPosts(currentUser: User, isRefresh: Boolean): Flow<List<Post>> {
//        val subscriber = object : ApolloStore.RecordChangeSubscriber {
//            override fun onCacheRecordsChanged(changedRecordKeys: Set<String>) {
//                println("onCacheRecordsChanged $changedRecordKeys")
//            }
//
//        }
//        apolloClient.apolloStore.subscribe(subscriber)
//        val dump = apolloClient.apolloStore.normalizedCache().dump()
//        println("dump: " + NormalizedCache.prettifyDump(dump))
        return apolloClient
            .query(PostsByCommunitiesIdQuery(Input.fromNullable(currentUser.communities.map { it.id })))
            .responseFetcher(isRefresh.apolloResponseFetchers())
            .watcher()
            .toFlow()
            .map<Response<PostsByCommunitiesIdQuery.Data>, List<Post>> { response ->
                response.data?.posts?.map {
                    it?.fragments?.postDetails?.mapToDomain(currentUser) ?: Post()
                } ?: emptyList()
            }
    }

    suspend fun posts(date: String, isRefresh: Boolean): Resource<List<Post>> =
        try {
            val postData =
                apolloClient.query(ExplorePostsQuery(date = date)).responseFetcher(isRefresh.apolloResponseFetchers()).await()
            postData.data?.getExplorePostPaginationQueryResolver?.let { list ->
                val posts = list.map {
                    it?.fragments?.postDetails?.mapToDomain(currentUser) ?: Post()
                } ?: emptyList()
                Resource.success(posts)
            } ?: run {
                throw ApolloException(
                    "unknown error in PostApolloService ${postData.errors?.get(0)?.message}"
                )
            }
        } catch (e: ApolloException) {
            throw Exception("PostApolloService, error happened: ${e.message}")
        }

    suspend fun deletePost(postId: String): Resource<Post> {
        try {
            val response = apolloClient
                .mutate(DeletePostMutation(Input.fromNullable(postId)))
                .toDeferred().await()

            if (response.hasErrors()) {
                return Resource.error(
                    response.errors?.get(0)?.message
                        ?: "Something wrong, please try again later",
                    null
                )
            }
            return Resource.success(null)
        } catch (e: ApolloException) {
            throw Exception("PostApolloService apollo, error happened: ${e.message}")
        }
    }

    suspend fun bookmarkPost(
        postId: String,
        userId: String,
        isBookmarked: Boolean
    ): Resource<Post> {
        val mutation = if (isBookmarked) {
            InsertBookmarkPostMutation(
                BookmarkInsertInput(
                    post = Input.fromNullable(
                        BookmarkPostRelationInput(
                            link = Input.fromNullable(postId)
                        )
                    ),
                    user = Input.fromNullable(userId),
                    bookmarkedOn = Input.fromNullable(Calendar.getInstance().time)
                )
            )
        } else {
            DeleteBookmarkPostMutation(Input.fromNullable(postId), Input.fromNullable(userId))
        }
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

    suspend fun votePost(
        currentUserId: String,
        post: Post,
        isUpVote: Boolean,
        isDelete: Boolean
    ): Resource<Post> {
        val mutation = VotePostMutation(
            userId = Input.fromNullable(currentUserId),
            postId = Input.fromNullable(post.id),
            vote = PostVoteInsertInput(
                post = Input.fromNullable(post.id),
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