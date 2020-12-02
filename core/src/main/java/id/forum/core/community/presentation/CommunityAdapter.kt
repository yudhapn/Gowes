package id.forum.core.community.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import id.forum.core.community.domain.model.Community
import id.forum.core.community.domain.model.CommunityDiffCallback
import id.forum.core.databinding.ItemListCommunityDetailBinding
import id.forum.core.databinding.ItemListCommunityOverviewBinding
import id.forum.core.util.VH_TYPE_EXPLORE
import id.forum.core.util.VH_TYPE_PROFILE

class CommunityAdapter(
    private val listener: CommunityAdapterListener,
    private val viewHolderType: Int
) : ListAdapter<Community, ViewHolder>(CommunityDiffCallback) {
    interface CommunityAdapterListener {
        fun onCommunityClicked(cardView: View, community: Community)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewHolderType) {
            VH_TYPE_EXPLORE -> CommunityDetailViewHolder(
                ItemListCommunityDetailBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            VH_TYPE_PROFILE -> CommunityOverviewViewHolder(
                ItemListCommunityOverviewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw ClassCastException("Unknown viewType $viewType")
        }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is CommunityDetailViewHolder -> holder.bind(getItem(position), listener)
            is CommunityOverviewViewHolder -> holder.bind(getItem(position), listener)
        }
    }
}