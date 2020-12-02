package id.forum.faq.data.service

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.toFlow
import id.forum.core.FaqsQuery
import id.forum.core.data.Token
import id.forum.faq.data.mapper.mapToDomain
import id.forum.core.faq.domain.model.Topic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

@ExperimentalCoroutinesApi
class FaqApolloService(private val token: Token) : KoinComponent {
    private val apolloClient: ApolloClient by inject { parametersOf(token) }

    fun getFaqs(): Flow<List<Topic>> = apolloClient
        .query(FaqsQuery())
        .watcher()
        .toFlow()
        .map { response ->
            response.data?.faqTopics?.map {
                it?.fragments?.faqTopicDetails?.mapToDomain() ?: Topic()
            } ?: emptyList()
        }
}