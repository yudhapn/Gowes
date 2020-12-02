package id.forum.search.data.repository

import id.forum.core.community.domain.model.Community
import id.forum.core.post.domain.model.Post
import id.forum.search.data.service.SearchApolloService
import id.forum.search.domain.repository.SearchRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.koin.core.KoinComponent
import org.koin.core.inject

@ExperimentalCoroutinesApi
class SearchRepositoryImpl : SearchRepository, KoinComponent {
    override fun searchPosts(keyword: String): Flow<List<Post>> {
        val searchService: SearchApolloService by inject()
        return searchService.searchPosts(keyword)
    }

    override fun searchCommunities(keyword: String): Flow<List<Community>> {
        val searchService: SearchApolloService by inject()
        return searchService.searchCommunities(keyword)
    }
}