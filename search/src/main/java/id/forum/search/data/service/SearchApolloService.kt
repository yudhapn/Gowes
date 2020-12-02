package id.forum.search.data.service

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.fetcher.ApolloResponseFetchers.NETWORK_ONLY
import id.forum.core.SearchCommunitiesQuery
import id.forum.core.SearchPostsQuery
import id.forum.core.community.data.mapper.mapToDomain
import id.forum.core.community.domain.model.Community
import id.forum.core.data.Token
import id.forum.core.post.data.mapper.mapToDomain
import id.forum.core.post.domain.model.Post
import id.forum.core.user.domain.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

@ExperimentalCoroutinesApi
class SearchApolloService(
    private val currentUser: User,
    private val token: Token
) : KoinComponent {
    private val apolloClient: ApolloClient by inject { parametersOf(token) }

    fun searchPosts(keyword: String): Flow<List<Post>> =
        apolloClient
            .query(SearchPostsQuery(keyword))
            .responseFetcher(NETWORK_ONLY)
            .watcher()
            .toFlow()
            .map { response ->
                Log.d(
                    "PostApolloService",
                    "post list size: ${response.data?.searchPostQuery?.size}"
                )
                response.data?.searchPostQuery?.map {
                    it?.fragments?.postDetails?.mapToDomain(currentUser) ?: Post()
                } ?: emptyList()
            }

    fun searchCommunities(keyword: String): Flow<List<Community>> =
        apolloClient
            .query(SearchCommunitiesQuery(keyword))
            .responseFetcher(NETWORK_ONLY)
            .watcher()
            .toFlow()
            .map { response ->
                Log.d(
                    "PostApolloService",
                    "post list size: ${response.data?.searchCommunityQuery?.size}"
                )
                response.data?.searchCommunityQuery?.map {
                    it?.fragments?.communityDetails?.mapToDomain() ?: Community()
                } ?: emptyList()
            }

}