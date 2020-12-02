package id.forum.core.post.domain.repository

import androidx.paging.PagingData
import id.forum.core.data.Resource
import id.forum.core.post.domain.model.Post
import id.forum.core.user.domain.model.User
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getPosts(isRefresh: Boolean): Flow<PagingData<Post>>
    fun getFeedPosts(currentUser: User, isRefresh: Boolean): Flow<List<Post>>
    fun getBookmarkedPosts(isRefresh: Boolean): Flow<List<Post>>
    suspend fun deletePost(postId: String): Resource<Post>
    suspend fun bookmarkPost(
        postId: String,
        userId: String,
        isBookmarked: Boolean
    ): Resource<Post>

    suspend fun votePost(currentUserId: String, post: Post, isUpVote: Boolean, isDelete: Boolean): Resource<Post>
}