package id.forum.search.domain.repository

import id.forum.core.community.domain.model.Community
import id.forum.core.post.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchPosts(keyword: String): Flow<List<Post>>
    fun searchCommunities(keyword: String): Flow<List<Community>>
}