package id.forum.gowes.navigation

import androidx.recyclerview.widget.RecyclerView
import id.forum.gowes.databinding.AccountItemLayoutBinding
import id.forum.core.user.domain.model.User
import id.forum.gowes.navigation.AccountAdapter.*

/**
 * ViewHolder for [AccountAdapter]. Holds a single account which can be selected.
 */
class AccountViewHolder(
    val binding: AccountItemLayoutBinding,
    val listener: AccountAdapterListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(usr: User) {
        binding.run {
            user = usr
            accountListener = listener
            executePendingBindings()
        }
    }
}