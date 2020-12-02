package id.forum.chat.presentation

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.forum.chat.databinding.ItemListChatLayoutBinding
import id.forum.chat.domain.model.Chat
import id.forum.chat.domain.model.ChatDiffCallback
import id.forum.chat.presentation.ChatAdapter.ViewHolder.Companion.from
import id.forum.core.user.domain.model.User


class ChatAdapter(private val listener: ChatListener, private val userId: String) :
    ListAdapter<Chat, ChatAdapter.ViewHolder>(ChatDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position), listener, userId)

    class ViewHolder private constructor(private val binding: ItemListChatLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat, listener: ChatListener, userId: String) {
            binding.apply {
                // UserId is our user account id
                val receiver = chat.users.find { it.id != userId }
                Log.d("ChatAdapter", "receiver: $receiver")
                this.chat = chat
                this.receiver = receiver
                this.listener = listener
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup) =
                ViewHolder(
                    ItemListChatLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
        }
    }
}

class ChatListener(val clickListener: (view: View, chat: Chat, receiver: User) -> Unit) {
    fun onClick(view: View, chat: Chat, user: User) = clickListener(view, chat, user)
}