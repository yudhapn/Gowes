package id.forum.profile.data

import android.util.Log
import id.forum.core.UserQuery

fun List<UserQuery.Chat?>.mapToDomain(currentUserId: String): List<String> {
    val chatIds: MutableList<String> = mutableListOf()
    // Loop chat list
    this.forEach { chat ->
        Log.d("ProfileApolloService", "chatId: ${chat?.fragments?.chatDetails?._id}")
        // find out if user ever chat with current user
        val matchUser = chat?.fragments?.chatDetails?.users?.find { userId ->
            userId?._id.equals(currentUserId, true)
        }
        // if math user is exist then add chat id in to chatId list and exit from loop
        if (matchUser != null) {
            chatIds.add(chat.fragments.chatDetails._id ?: "")
            return@forEach
        }
    }
    return chatIds
}