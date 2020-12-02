package id.forum.gowes.navigation

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.google.android.material.floatingactionbutton.FloatingActionButton
import id.forum.gowes.R

/**
 * An action to be performed when a bottom sheet's state is changed.
 */
interface OnStateChangedAction {
    fun onStateChanged(sheet: View, newState: Int)
}

/**
 * A state change action that tells the calling client when a open-sheet specific menu should be
 * used.
 */
class ChangeSettingsMenuStateAction(
    private val onShouldShowSettingsMenu: (showSettings: Boolean) -> Unit
) : OnStateChangedAction {

    private var hasCalledShowSettingsMenu: Boolean = false

    override fun onStateChanged(sheet: View, newState: Int) {
        if (newState == STATE_HIDDEN) {
            hasCalledShowSettingsMenu = false
            onShouldShowSettingsMenu(false)
        } else {
            if (!hasCalledShowSettingsMenu) {
                hasCalledShowSettingsMenu = true
                onShouldShowSettingsMenu(true)
            }
        }
    }
}

/**
 * A state change action that handles showing the fab when the sheet is hidden and hiding the fab
 * when the sheet is not hidden.
 */
class ShowHideFabStateAction(private val fab: FloatingActionButton) : OnStateChangedAction {
    var dest: Int = R.id.homeFragment

    override fun onStateChanged(sheet: View, newState: Int) {
        val showOnFragment =
            (dest != R.id.communityFragment && dest != R.id.bookmarkFragment
                    && dest != R.id.exploreFragment && dest != R.id.communityListFragment
                    && dest != R.id.memberListFragment && dest != R.id.memberFragment
                    && dest != R.id.faqTopicFragment && dest != R.id.faqContentFragment
                    && dest != R.id.createChatFragment && dest != R.id.createCommunityFragment)
//      val showOnFragment =
//         (dest != R.id.communityFragment && dest != R.id.faqFragment
//                 && dest != R.id.exploreFragment && dest != R.id.saveFragment
//                 && dest != R.id.communityListFragment && dest != R.id.memberListFragment
//                 && dest != R.id.memberFragment)
        if (newState == STATE_HIDDEN && showOnFragment) {
            fab.show()
        } else {
            fab.hide()
        }
    }
}

/**
 * A state change action that sets a view's visibility depending on whether the sheet is hidden
 * or not.
 *
 * By default, the view will be hidden when the sheet is hidden and shown when the sheet is shown
 * (not hidden). If [reverse] is set to true, the view will be shown when the sheet is hidden and
 * hidden when the sheet is shown (not hidden).
 */
class VisibilityStateAction(
    private val view: View,
    private val reverse: Boolean = false
) : OnStateChangedAction {
    override fun onStateChanged(sheet: View, newState: Int) {
        val stateHiddenVisibility = if (!reverse) View.GONE else View.VISIBLE
        val stateDefaultVisibility = if (!reverse) View.VISIBLE else View.GONE
        when (newState) {
            STATE_HIDDEN -> view.visibility = stateHiddenVisibility
            else -> view.visibility = stateDefaultVisibility
        }
    }
}

/**
 * A state change action which scrolls a [RecyclerView] to the top when the sheet is hidden.
 *
 * This is used to make sure the navigation drawer's [RecyclerView] is never half-scrolled when
 * opened to the half-expanded state, which can happen if the sheet is hidden while scrolled.
 */
class ScrollToTopStateAction(
    private val recyclerView: RecyclerView
) : OnStateChangedAction {
    override fun onStateChanged(sheet: View, newState: Int) {
        if (newState == STATE_HIDDEN) recyclerView.scrollToPosition(0)
    }
}
