package id.forum.community.presentation.member

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import id.forum.community.databinding.ItemListOverviewLayoutBinding
import id.forum.core.user.domain.model.User
import id.forum.core.user.domain.model.UserDiffCallback

class MemberOverviewAdapter(
   private val listener: MemberAdapterListener
) :
   ListAdapter<User, MemberOverviewViewHolder>(UserDiffCallback) {
   interface MemberAdapterListener {
      fun onMemberClicked(cardView: View, user: User)
   }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
      MemberOverviewViewHolder(
         ItemListOverviewLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
         )
      )

   override fun onBindViewHolder(holder: MemberOverviewViewHolder, position: Int) {
         holder.bind(getItem(position), listener)
   }
}