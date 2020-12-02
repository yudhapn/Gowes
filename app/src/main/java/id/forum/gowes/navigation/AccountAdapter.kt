package id.forum.gowes.navigation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import id.forum.gowes.databinding.AccountItemLayoutBinding
import id.forum.gowes.databinding.LogoutItemLayoutBinding
import id.forum.core.user.domain.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

/**
 * An adapter which holds a list of selectable accounts owned by the current user.
 */
class AccountAdapter(private val listener: AccountAdapterListener) :
    ListAdapter<DataItem, ViewHolder>(DataItemDiffCallback) {
    private val adapterScope = CoroutineScope(Dispatchers.Default)

    interface AccountAdapterListener {
        fun onAccountClicked(user: User)
        fun onLogoutClicked()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> LogoutViewHolder(
                LogoutItemLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                listener
            )
            ITEM_VIEW_TYPE_ITEM -> AccountViewHolder(
                AccountItemLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                listener
            )
            else -> throw ClassCastException("Unknown viewTyoe ${viewType}")
        }

    override fun getItemViewType(position: Int) =
        when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.UserItem -> ITEM_VIEW_TYPE_ITEM
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is LogoutViewHolder -> holder.bind()
            is AccountViewHolder -> holder.bind((getItem(position) as DataItem.UserItem).user)
        }
    }

    fun addHeaderAndSubmitList(list: List<User>?) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(DataItem.Header)
                else -> list.map { DataItem.UserItem(it) } + listOf(DataItem.Header)
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }
}

object DataItemDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem) =
        oldItem == newItem
}


sealed class DataItem {
    data class UserItem(val user: User) : DataItem() {
        override val id = user.id
    }

    object Header : DataItem() {
        override val id = "logout"
    }

    abstract val id: String
}