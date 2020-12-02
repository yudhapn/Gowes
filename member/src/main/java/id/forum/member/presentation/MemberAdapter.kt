package id.forum.member.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import id.forum.core.user.domain.model.User
import id.forum.member.databinding.ItemListAdminLayoutBinding
import id.forum.member.databinding.ItemListMemberLayoutBinding
import id.forum.member.databinding.ItemListRequestLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ITEM_VIEW_TYPE_ADMIN = 0
private const val ITEM_VIEW_TYPE_MEMBER = 1
private const val ITEM_VIEW_TYPE_REQUEST = 2


class MemberAdapter(
   private val listener: MemberAdapterListener
) :
    ListAdapter<DataItem, ViewHolder>(
       DataItemDiffCallback
    ) {
    private val adapterScope = CoroutineScope(Dispatchers.Default)

    interface MemberAdapterListener {
        fun onMemberClicked(user: User)
        fun onMenuClicked(anchor: View, user: User)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewType) {
           ITEM_VIEW_TYPE_ADMIN -> AdminViewHolder.from(
              ItemListAdminLayoutBinding.inflate(
                 LayoutInflater.from(parent.context), parent, false
              ), listener
           )
           ITEM_VIEW_TYPE_MEMBER -> MemberViewHolder.from(
              ItemListMemberLayoutBinding.inflate(
                 LayoutInflater.from(parent.context), parent, false
              ), listener
           )
           ITEM_VIEW_TYPE_REQUEST -> RequestViewHolder.from(
              ItemListRequestLayoutBinding.inflate(
                 LayoutInflater.from(parent.context), parent, false
              ), listener
           )
            else -> throw ClassCastException("Unknown viewType $viewType")
        }

    override fun getItemViewType(position: Int) =
        when (getItem(position)) {
           is DataItem.AdminItem -> ITEM_VIEW_TYPE_ADMIN
           is DataItem.MemberItem -> ITEM_VIEW_TYPE_MEMBER
           is DataItem.RequestItem -> ITEM_VIEW_TYPE_REQUEST
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        when (holder) {
           is AdminViewHolder -> holder.bind((getItem(position) as DataItem.AdminItem).user)
           is MemberViewHolder -> holder.bind((getItem(position) as DataItem.MemberItem).user)
           is RequestViewHolder -> holder.bind((getItem(position) as DataItem.RequestItem).user)
            else -> throw ClassCastException("Unknown viewHolder in position $position")
        }

    fun addHeaderAndSubmitList(_admins: List<User>?, _members: List<User>?, _request: List<User>?) {
        adapterScope.launch {
            val admins = _admins?.map {
                DataItem.AdminItem(
                   it
                )
            } ?: emptyList()
            val members = _members?.map {
                DataItem.MemberItem(
                   it
                )
            } ?: emptyList()
            val request = _request?.map {
                DataItem.RequestItem(
                   it
                )
            } ?: emptyList()
            val items = admins + members + request
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    class AdminViewHolder(
       private val binding: ItemListAdminLayoutBinding,
       private val _listener: MemberAdapterListener
    ) :
        ViewHolder(binding.root) {
        fun bind(_user: User) {
            binding.user = _user
            binding.listener = _listener
        }

        companion object {
            fun from(binding: ItemListAdminLayoutBinding, listener: MemberAdapterListener) =
                AdminViewHolder(
                   binding,
                   listener
                )
        }
    }

    class MemberViewHolder(
       private val binding: ItemListMemberLayoutBinding,
       private val _listener: MemberAdapterListener
    ) :
        ViewHolder(binding.root) {
        fun bind(_user: User) {
            binding.user = _user
            binding.listener = _listener
        }

        companion object {
            fun from(binding: ItemListMemberLayoutBinding, listener: MemberAdapterListener) =
                MemberViewHolder(
                   binding,
                   listener
                )
        }
    }

    class RequestViewHolder(
       private val binding: ItemListRequestLayoutBinding,
       private val _listener: MemberAdapterListener
    ) :
        ViewHolder(binding.root) {
        fun bind(_user: User) {
            binding.user = _user
            binding.listener = _listener
        }

        companion object {
            fun from(binding: ItemListRequestLayoutBinding, listener: MemberAdapterListener) =
                RequestViewHolder(
                   binding,
                   listener
                )
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
    data class AdminItem(val user: User) : DataItem() {
        override val id = user.id
    }

    data class MemberItem(val user: User) : DataItem() {
        override val id = user.id
    }

    data class RequestItem(val user: User) : DataItem() {
        override val id = user.id
    }

    abstract val id: String
}