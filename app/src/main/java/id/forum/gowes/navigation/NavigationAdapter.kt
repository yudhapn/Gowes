package id.forum.gowes.navigation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import id.forum.gowes.databinding.NavCreateCommunityItemLayoutBinding
import id.forum.gowes.databinding.NavDividerItemLayoutBinding
import id.forum.gowes.databinding.NavEmailFolderItemLayoutBinding
import id.forum.gowes.databinding.NavMenuItemLayoutBinding

private const val VIEW_TYPE_NAV_MENU_ITEM = 4
private const val VIEW_TYPE_NAV_DIVIDER = 6
private const val VIEW_TYPE_NAV_COMMUNITY_ITEM = 5
private const val VIEW_TYPE_NAV_CREATE_COMMUNITY = 8

class NavigationAdapter(
    private val listener: NavigationAdapterListener
) : ListAdapter<NavigationModelItem, NavigationViewHolder<NavigationModelItem>>(
    NavigationModelItem.NavModelItemDiff
) {

    interface NavigationAdapterListener {
        fun onNavMenuItemClicked(item: NavigationModelItem.NavMenuItem)
        fun onNavCommunityClicked(navCommunity: NavigationModelItem.NavCommunity)
        fun onNavCreateCommunityClicked()
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is NavigationModelItem.NavMenuItem -> VIEW_TYPE_NAV_MENU_ITEM
            is NavigationModelItem.NavDivider -> VIEW_TYPE_NAV_DIVIDER
            is NavigationModelItem.NavCommunity -> VIEW_TYPE_NAV_COMMUNITY_ITEM
            is NavigationModelItem.NavCreateCommunity -> VIEW_TYPE_NAV_CREATE_COMMUNITY
            else -> throw RuntimeException("Unsupported ItemViewType for obj ${getItem(position)}")
        }
    }

    @Suppress("unchecked_cast")
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NavigationViewHolder<NavigationModelItem> {
        return when (viewType) {
            VIEW_TYPE_NAV_MENU_ITEM -> NavigationViewHolder.NavMenuItemViewHolder(
                NavMenuItemLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                listener
            )
            VIEW_TYPE_NAV_DIVIDER -> NavigationViewHolder.NavDividerViewHolder(
                NavDividerItemLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            VIEW_TYPE_NAV_COMMUNITY_ITEM -> NavigationViewHolder.EmailFolderViewHolder(
                NavEmailFolderItemLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                listener
            )
            VIEW_TYPE_NAV_CREATE_COMMUNITY -> NavigationViewHolder.CreateCommunityViewHolder(
                NavCreateCommunityItemLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                listener
            )
            else -> throw RuntimeException("Unsupported view holder type")
        } as NavigationViewHolder<NavigationModelItem>
    }

    override fun onBindViewHolder(
        holder: NavigationViewHolder<NavigationModelItem>,
        position: Int
    ) {
        holder.bind(getItem(position))
    }
}