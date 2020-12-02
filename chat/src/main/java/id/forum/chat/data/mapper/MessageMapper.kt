package id.forum.chat.data.mapper

import id.forum.chat.domain.model.Message
import id.forum.core.fragment.MessageDetails
import java.util.*

fun MessageDetails.mapToDomain(): Message =
    Message(
        id = _id ?: "",
        senderId = user?._id ?: "",
        content = content ?: "",
        image = attachment ?: "",
        sentOn = sentOn ?: Calendar.getInstance().time
    )
