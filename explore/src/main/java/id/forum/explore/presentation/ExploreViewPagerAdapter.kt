package id.forum.explore.presentation

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import id.forum.core.R
import id.forum.explore.presentation.community.CommunityListFragment
import id.forum.explore.presentation.post.PostListFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Suppress("DEPRECATION")
class ExploreViewPagerAdapter(fm: FragmentManager, private val context: Context) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment =
        when (position) {
            0 -> PostListFragment()
            1 -> CommunityListFragment(true)
            else -> CommunityListFragment(true)
        }

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence? =
        when (position) {
            0 -> context.getString(R.string.post_tab)
            1 -> context.getString(R.string.community_tab)
            else -> context.getString(R.string.community_tab)
        }
}