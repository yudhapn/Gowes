package id.forum.community.presentation.member

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import id.forum.community.databinding.ItemListOverviewLayoutBinding
import id.forum.community.presentation.member.MemberOverviewAdapter.MemberAdapterListener
import id.forum.core.user.domain.model.User

class MemberOverviewViewHolder(
    private val binding: ItemListOverviewLayoutBinding
) : ViewHolder(binding.root) {

    fun bind(user: User, listener: MemberAdapterListener) {
        binding.apply {
            this.user = user
            this.listener = listener
            executePendingBindings()
        }
    }
}
