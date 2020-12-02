package id.forum.community.presentation

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import id.forum.community.presentation.member.MemberOverviewAdapter
import id.forum.core.post.domain.model.Comment
import id.forum.core.user.domain.model.User

@BindingAdapter("membersData", "adminsData")
fun RecyclerView.bindRvMember(members: List<User>?, admins: List<User>?) {
    val adapter = adapter as MemberOverviewAdapter
    val list = admins?.let { adminList ->
        members?.let { memberList ->
            (adminList + memberList).take(5)
        }
    }
    adapter.submitList(list)
}