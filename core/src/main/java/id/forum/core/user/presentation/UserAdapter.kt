package id.forum.core.user.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import id.forum.core.databinding.UserItemLayoutBinding
import id.forum.core.user.domain.model.User
import id.forum.core.user.domain.model.UserDiffCallback

class UserAdapter(
    private val listener: UserAdapterListener
) :
    ListAdapter<User, UserViewHolder>(UserDiffCallback) {
    interface UserAdapterListener {
        fun onMemberClicked(cardView: View, user: User)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        UserViewHolder(
            UserItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }
}