package id.forum.search.domain.usecase

import id.forum.core.community.domain.model.Community
import id.forum.search.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow

class SearchCommunitiesUseCase(private val searchRepository: SearchRepository) {
    fun execute(keyword: String): Flow<List<Community>> = searchRepository.searchCommunities(keyword)
}