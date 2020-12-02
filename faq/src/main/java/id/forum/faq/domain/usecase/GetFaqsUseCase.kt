package id.forum.faq.domain.usecase

import id.forum.core.faq.domain.model.Topic
import id.forum.faq.domain.repository.FaqRepository
import kotlinx.coroutines.flow.Flow

internal class GetFaqsUseCase(private val faqRepository: FaqRepository) {
    fun execute(): Flow<List<Topic>> = faqRepository.getFaqs()
}
