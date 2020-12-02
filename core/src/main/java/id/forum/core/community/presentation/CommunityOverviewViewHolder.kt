package id.forum.core.community.presentation

import androidx.recyclerview.widget.RecyclerView
import id.forum.core.community.domain.model.Community
import id.forum.core.community.presentation.CommunityAdapter.CommunityAdapterListener
import id.forum.core.databinding.ItemListCommunityOverviewBinding

class CommunityOverviewViewHolder(
    private val binding: ItemListCommunityOverviewBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(community: Community, listener: CommunityAdapterListener) {
        binding.apply {
            this.community = community
            this.listener = listener
            executePendingBindings()
        }
    }
}
