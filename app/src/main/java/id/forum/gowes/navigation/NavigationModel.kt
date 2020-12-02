package id.forum.gowes.navigation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.forum.core.R
import id.forum.core.community.domain.model.Community
import org.koin.core.KoinComponent

/**
 * A class which maintains and generates a navigation list to be displayed by [NavigationAdapter].
 */
object NavigationModel : KoinComponent {

    private var navigationMenuItems = mutableListOf(
        NavigationModelItem.NavMenuItem(
            id = 0,
            icon = R.drawable.ic_twotone_inbox,
            titleRes = R.string.nav_home,
            checked = true
        ),
        NavigationModelItem.NavMenuItem(
            id = 1,
            icon = R.drawable.ic_twotone_faq,
            titleRes = R.string.nav_faq,
            checked = false
        ),
        NavigationModelItem.NavMenuItem(
            id = 2,
            icon = R.drawable.ic_twotone_explore,
            titleRes = R.string.nav_explore,
            checked = false
        ),
        NavigationModelItem.NavMenuItem(
            id = 3,
            icon = R.drawable.ic_twotone_chat,
            titleRes = R.string.nav_chat,
            checked = false
        ),
        NavigationModelItem.NavMenuItem(
            id = 4,
            icon = R.drawable.ic_twotone_profile,
            titleRes = R.string.nav_profile,
            checked = false
        ),
        NavigationModelItem.NavMenuItem(
            id = 5,
            icon = R.drawable.ic_bookmark,
            titleRes = R.string.nav_bookmark,
            checked = false
        )
    )

    private val _navigationList: MutableLiveData<List<NavigationModelItem>> = MutableLiveData()
    val navigationList: LiveData<List<NavigationModelItem>>
        get() = _navigationList

    init {
//        postListUpdate()
    }

    private var communities: List<Community> = emptyList()

    /**
     * Set the currently selected menu item.
     *
     * @return true if the currently selected item has changed.
     */
    fun setNavigationMenuItemChecked(id: Int): Boolean {
        var updated = false
        navigationMenuItems.forEachIndexed { index, item ->
            val shouldCheck = item.id == id
            if (item.checked != shouldCheck) {
                navigationMenuItems[index] = item.copy(checked = shouldCheck)
                updated = true
            }
        }
        if (updated) postListUpdate()
        return updated
    }


    fun postListUpdate(communities: List<Community> = emptyList()) {
        if (communities.isNotEmpty()) {
            this.communities = communities
        }
        val newList = navigationMenuItems +
                NavigationModelItem.NavDivider("Community") +
                this.communities.map { NavigationModelItem.NavCommunity(it) } +
                NavigationModelItem.NavCreateCommunity("Create Community")
        _navigationList.value = newList
    }
}

