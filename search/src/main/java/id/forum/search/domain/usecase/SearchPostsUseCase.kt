package id.forum.search.domain.usecase

import id.forum.core.post.domain.model.Post
import id.forum.search.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow

class SearchPostsUseCase(private val searchRepository: SearchRepository) {
    fun execute(keyword: String): Flow<List<Post>> = searchRepository.searchPosts(keyword)
}