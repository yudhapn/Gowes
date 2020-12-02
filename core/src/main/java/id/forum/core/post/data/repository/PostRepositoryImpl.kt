package id.forum.core.post.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import id.forum.core.data.Resource
import id.forum.core.post.data.service.PostApolloService
import id.forum.core.post.data.source.ExplorePostDataSource
import id.forum.core.post.domain.model.Post
import id.forum.core.post.domain.repository.PostRepository
import id.forum.core.user.domain.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import org.koin.core.KoinComponent
import org.koin.core.inject

@ExperimentalCoroutinesApi
class PostRepositoryImpl : PostRepository, KoinComponent {

    override fun getPosts(isRefresh: Boolean): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {ExplorePostDataSource(isRefresh)}
        ).flow
    }

    override fun getFeedPosts(currentUser: User, isRefresh: Boolean): Flow<List<Post>> {
        val postService: PostApolloService by inject()
        return postService.getFeedPosts(currentUser, isRefresh)
    }

    override fun getBookmarkedPosts(isRefresh: Boolean): Flow<List<Post>> {
        val postService: PostApolloService by inject()
        return postService.getBookmarkedPosts(isRefresh)
    }

    override suspend fun deletePost(postId: String): Resource<Post> {
        val postService: PostApolloService by inject()
        return postService.deletePost(postId)
    }

    override suspend fun bookmarkPost(
        postId: String,
        userId: String,
        isBookmarked: Boolean
    ): Resource<Post> {
        val postService: PostApolloService by inject()
        return postService.bookmarkPost(postId, userId, isBookmarked)
    }

    override suspend fun votePost(
        currentUserId: String,
        post: Post,
        isUpVote: Boolean,
        isDelete: Boolean
    ): Resource<Post> {
        val postService: PostApolloService by inject()
        return postService.votePost(currentUserId, post, isUpVote, isDelete)
    }
}
