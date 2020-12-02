package id.forum.chat.domain.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import id.forum.core.user.domain.model.User
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Chat(
    var id: String = "0L",
    val users: List<User> = emptyList(),
    var messages: List<Message> = emptyList(),
    var lastText: String = ""
) : Parcelable

object ChatDiffCallback : DiffUtil.ItemCallback<Chat>() {
    override fun areItemsTheSame(oldItem: Chat, newItem: Chat) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Chat, newItem: Chat) =
        oldItem == newItem
}