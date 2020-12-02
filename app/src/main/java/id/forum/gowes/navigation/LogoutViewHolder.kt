package id.forum.gowes.navigation

import androidx.recyclerview.widget.RecyclerView
import id.forum.gowes.databinding.LogoutItemLayoutBinding
import id.forum.gowes.navigation.AccountAdapter.*

class LogoutViewHolder(
    private val binding: LogoutItemLayoutBinding,
    private val _listener: AccountAdapterListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind() {
        binding.run {
            listener = _listener
            executePendingBindings()
        }
    }
}