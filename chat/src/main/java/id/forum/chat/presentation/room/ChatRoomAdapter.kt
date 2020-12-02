package id.forum.chat.presentation.room

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import id.forum.chat.databinding.ItemListChatLeftLayoutBinding
import id.forum.chat.databinding.ItemListChatRightLayoutBinding
import id.forum.chat.domain.model.Message
import id.forum.chat.domain.model.MessageDiffCallback

private const val MSG_TYPE_LEFT = 0
private const val MSG_TYPE_RIGHT = 1

class ChatRoomAdapter(private val currentUid: String, private val receiverAvatar: String) :
   ListAdapter<Message, ViewHolder>(MessageDiffCallback) {

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
      when (viewType) {
         MSG_TYPE_RIGHT -> RightViewHolder.from(
            ItemListChatRightLayoutBinding.inflate(
               LayoutInflater.from(parent.context),
               parent,
               false
            )
         )
         MSG_TYPE_LEFT -> LeftViewHolder.from(
            ItemListChatLeftLayoutBinding.inflate(
               LayoutInflater.from(parent.context),
               parent,
               false
            )
         )
         else -> throw ClassCastException("Unknown viewType $viewType")
      }

   override fun getItemViewType(position: Int): Int =
      if (getItem(position).senderId.equals(currentUid))
         MSG_TYPE_RIGHT
      else
         MSG_TYPE_LEFT

   override fun onBindViewHolder(holder: ViewHolder, position: Int) =
      when (holder) {
         is RightViewHolder -> holder.bind(getItem(position))
         is LeftViewHolder -> holder.bind(getItem(position), receiverAvatar)
         else -> throw ClassCastException("Unknown viewHolder")
      }

   class RightViewHolder(private val binding: ItemListChatRightLayoutBinding) :
      ViewHolder(binding.root) {
      fun bind(message: Message) {
         binding.message = message
      }

      companion object {
         fun from(binding: ItemListChatRightLayoutBinding) =
            RightViewHolder(binding)
      }
   }

   class LeftViewHolder(private val binding: ItemListChatLeftLayoutBinding) : ViewHolder(binding.root) {
      fun bind(message: Message, receiverAvatar: String) {
         binding.apply {
            this.message = message
            this.receiverAvatar = receiverAvatar
         }

      }

      companion object {
         fun from(binding: ItemListChatLeftLayoutBinding) =
            LeftViewHolder(binding)
      }
   }
}
