package id.forum.core.community.presentation

import androidx.recyclerview.widget.RecyclerView
import id.forum.core.community.domain.model.Community
import id.forum.core.databinding.ItemListCommunityDetailBinding

class CommunityDetailViewHolder(
    private val binding: ItemListCommunityDetailBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(_community: Community, _listener: CommunityAdapter.CommunityAdapterListener) {
        binding.apply {
            community = _community
            listener = _listener
            executePendingBindings()
        }
    }
}
