package id.forum.core.user.presentation

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import id.forum.core.databinding.UserItemLayoutBinding
import id.forum.core.user.domain.model.User
import id.forum.core.user.presentation.UserAdapter.UserAdapterListener

class UserViewHolder(
    private val binding: UserItemLayoutBinding
) : ViewHolder(binding.root) {

    fun bind(user: User, listener: UserAdapterListener) {
        binding.apply {
            this.user = user
            this.listener = listener
            executePendingBindings()
        }
    }
}
