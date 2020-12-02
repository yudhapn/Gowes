package id.forum.faq.data.mapper

import id.forum.core.fragment.FaqTopicDetails
import id.forum.core.faq.domain.model.Faq
import id.forum.core.faq.domain.model.Topic
import java.util.*

fun FaqTopicDetails.mapToDomain(): Topic =
    Topic(
        id = _id ?: "",
        name = name ?: "",
        faqs = contents?.map {
            it?.mapToDomain() ?: Faq()
        } ?: emptyList<Faq>(),
        createdOn = createdOn ?: Calendar.getInstance().time
    )

fun FaqTopicDetails.Content.mapToDomain(): Faq =
    Faq(
        id = _id ?: "",
        title = title ?: "",
        question = question ?: "",
        answer = answer ?: ""
    )
