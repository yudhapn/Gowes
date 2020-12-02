package id.forum.member.presentation

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import id.forum.core.community.domain.model.Community
import id.forum.core.util.FRAGMENT_TYPE_MEMBER
import id.forum.core.util.FRAGMENT_TYPE_REQUEST
import id.forum.core.R

@Suppress("DEPRECATION")
class MemberViewPagerAdapter(
   fm: FragmentManager, private val context: Context, private val community: Community
) : FragmentPagerAdapter(fm) {

   override fun getItem(position: Int): Fragment =
      when (position) {
         0 -> MemberListFragment(
            FRAGMENT_TYPE_MEMBER,
            community,
            true
         )
         1 -> MemberListFragment(
            FRAGMENT_TYPE_REQUEST,
            community,
            true
         )
         else -> MemberListFragment(
            FRAGMENT_TYPE_MEMBER,
            community,
            true
         )
      }

   override fun getCount(): Int = 2

   override fun getPageTitle(position: Int): CharSequence? =
      when (position) {
         0 -> context.getString(R.string.member_tab)
         1 -> context.getString(R.string.request_button)
         else -> context.getString(R.string.request_button)
      }
}