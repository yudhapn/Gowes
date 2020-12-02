package id.forum.chat.presentation

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import id.forum.chat.domain.model.Chat
import id.forum.chat.domain.model.Message
import id.forum.chat.presentation.create.UserChatListAdapter
import id.forum.chat.presentation.room.ChatRoomAdapter
import id.forum.core.post.domain.model.Comment
import id.forum.core.user.domain.model.User
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("usersChatData")
fun RecyclerView.bindRvUsersChat(data: List<User>?) {
    val adapter = adapter as UserChatListAdapter
    adapter.submitList(data)
}

@BindingAdapter("chatsData")
fun RecyclerView.bindRvChat(data: List<Chat>?) {
    val adapter = adapter as ChatAdapter
    adapter.submitList(data)
}

@BindingAdapter("messagesData")
fun RecyclerView.bindRvMessage(data: List<Message>?) {
    val adapter = adapter as ChatRoomAdapter
    adapter.submitList(data)
}

@BindingAdapter("setTimeSent")
fun TextView.setTimeSent(time: Date) {
    val timeFormat = SimpleDateFormat("hh:mm a")
    text = timeFormat.format(time)
}