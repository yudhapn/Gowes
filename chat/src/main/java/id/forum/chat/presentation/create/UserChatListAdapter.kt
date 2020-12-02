package id.forum.chat.presentation.create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import id.forum.chat.databinding.ItemListUserChatLayoutBinding
import id.forum.chat.presentation.create.UserChatListAdapter.UserChatViewHolder
import id.forum.core.user.domain.model.User
import id.forum.core.user.domain.model.UserDiffCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class UserChatListAdapter(
   private val listener: UserChatAdapterListener
) :
   ListAdapter<User, UserChatViewHolder>(UserDiffCallback) {
   private val adapterScope = CoroutineScope(Dispatchers.Default)

   interface UserChatAdapterListener {
      fun onUserClicked(user: User)
   }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
      UserChatViewHolder.from(
         ItemListUserChatLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
         ), listener
      )

   override fun onBindViewHolder(holder: UserChatViewHolder, position: Int) =
      holder.bind(getItem(position))

   class UserChatViewHolder(
      private val binding: ItemListUserChatLayoutBinding,
      private val _listener: UserChatAdapterListener
   ) :
      ViewHolder(binding.root) {
      fun bind(_user: User) {
         binding.user = _user
         binding.listener = _listener
      }

      companion object {
         fun from(binding: ItemListUserChatLayoutBinding, listener: UserChatAdapterListener) =
            UserChatViewHolder(binding, listener)
      }
   }

}
