package id.forum.search.presentation

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import id.forum.core.R
import id.forum.search.presentation.community.CommunitiesFragment
import id.forum.search.presentation.post.PostsFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Suppress("DEPRECATION")
class SearchViewPagerAdapter(fm: FragmentManager, private val context: Context) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment =
        when (position) {
            0 -> PostsFragment()
            1 -> CommunitiesFragment()
            else -> CommunitiesFragment()
        }

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence? =
        when (position) {
            0 -> context.getString(R.string.post_tab)
            1 -> context.getString(R.string.community_tab)
            else -> context.getString(R.string.community_tab)
        }
}