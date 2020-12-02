package id.forum.faq.domain.repository

import id.forum.core.faq.domain.model.Topic
import kotlinx.coroutines.flow.Flow

interface FaqRepository {
    fun getFaqs(): Flow<List<Topic>>
}