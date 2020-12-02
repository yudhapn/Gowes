package id.forum.faq.data.repository

import id.forum.faq.data.service.FaqApolloService
import id.forum.core.faq.domain.model.Topic
import id.forum.faq.domain.repository.FaqRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import org.koin.core.KoinComponent
import org.koin.core.inject

@ExperimentalCoroutinesApi
class FaqRepositoryImpl : FaqRepository, KoinComponent {
    override fun getFaqs(): Flow<List<Topic>> {
        val faqApolloService: FaqApolloService by inject()
        return faqApolloService.getFaqs()
    }
}