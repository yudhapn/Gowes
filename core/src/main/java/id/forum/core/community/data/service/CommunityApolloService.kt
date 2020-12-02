package id.forum.core.community.data.service

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import id.forum.core.CommunitiesQuery
import id.forum.core.CommunityQuery
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
class CommunityApolloService(
    private val currentUser: User,
    private val token: Token
) : KoinComponent {
    private val apolloClient: ApolloClient by inject { parametersOf(token) }

    fun getCommunities(): Flow<List<Community>> = apolloClient
        .query(CommunitiesQuery())
        .responseFetcher(ApolloResponseFetchers.CACHE_FIRST)
        .watcher()
        .toFlow()
        .map { response ->
            response.data?.communitys?.map {
                it?.fragments?.communityDetails?.mapToDomain() ?: Community()
            } ?: emptyList()
        }

    fun getCommunity(communityId: String): Flow<Community> = apolloClient
        .query(CommunityQuery(Input.fromNullable(communityId)))
        .responseFetcher(ApolloResponseFetchers.CACHE_FIRST)
        .watcher()
        .toFlow()
        .map { response ->
            response.data?.community?.let {
                val community = it.fragments.communityDetails.mapToDomain()
                community.posts = response.data?.posts?.map { postData ->
                    postData?.fragments?.postDetails?.mapToDomain(currentUser) ?: Post()
                } ?: emptyList()
                community
            } ?: Community()
        }
}