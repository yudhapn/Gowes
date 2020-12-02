package id.forum.gowes

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View.*
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.google.android.material.behavior.SwipeDismissBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import id.forum.core.account.presentation.UserAccountViewModel
import id.forum.core.data.Status.ERROR
import id.forum.core.data.Status.SUCCESS
import id.forum.core.media.domain.model.MediaList
import id.forum.core.post.domain.model.Post
import id.forum.core.util.*
import id.forum.explore.presentation.ExploreFragmentDirections
import id.forum.gowes.databinding.ActivityMainBinding
import id.forum.gowes.navigation.*
import id.forum.login.presentation.LoginFragmentDirections
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener,
    NavController.OnDestinationChangedListener {
    private val viewModel: UserAccountViewModel by viewModel()
    private lateinit var showHideFabStateAction: ShowHideFabStateAction
    private val binding: ActivityMainBinding by contentView(R.layout.activity_main)
    private val bottomNavDrawer: BottomNavDrawerFragment by lazy(LazyThreadSafetyMode.NONE) {
        supportFragmentManager.findFragmentById(R.id.bottom_nav_drawer) as BottomNavDrawerFragment
    }
    private lateinit var shared: SharedPreferences
    private var hasLandedHome = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Gowes_DayNight)
        super.onCreate(savedInstanceState)
        viewModel.userAccount.observe(this, Observer { currentUser ->
            binding.progressBar.visibility = when (currentUser.status) {
                SUCCESS, ERROR -> INVISIBLE
                else -> VISIBLE
            }
            if (currentUser.data == null) {
                Log.d("mainActivity", "not logged in yet")
                val action = LoginFragmentDirections.actionGlobalLogin()
                findNavController(R.id.nav_host_fragment).navigate(action)
            } else {
                Log.d("mainActivity", "current user is: ${currentUser.data}")
            }
        })
        requestPermissionsIfNecessary()
        shared = getSharedPreferences("shared", MODE_PRIVATE)
        val darkValue = shared.getInt("MODE_DARK", 0)
        if (darkValue != 0) delegate.localNightMode = darkValue
        setUpBottomNavigationAndFab()
        showSnackBar()
    }

    private fun setUpBottomNavigationAndFab() {
        // Set a custom animation for showing and hiding the FAB
        binding.fab.apply {
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)
        }

        binding.fab.setOnClickListener {
            Log.d("MainActivity", "fab clicked")
            findNavController(R.id.nav_host_fragment)
                .navigate(AppNavGraphDirections.actionGlobalCompose(MediaList()))
        }

        bottomNavDrawer.apply {
            addOnSlideAction(HalfClockwiseRotateSlideAction(binding.bottomAppBarChevron))
            addOnSlideAction(AlphaSlideAction(binding.bottomAppBarTitle, true))
            showHideFabStateAction = ShowHideFabStateAction(binding.fab)
            addOnStateChangedAction(showHideFabStateAction)
            addOnStateChangedAction(ChangeSettingsMenuStateAction { showSettings ->
                // Toggle between the current destination's BAB menu and the menu which should
                // be displayed when the BottomNavigationDrawer is open.
                binding.bottomAppBar.replaceMenu(
                    if (showSettings) {
                        R.menu.bottom_app_bar_settings_menu
                    } else {
                        getBottomAppBarMenu()
                    }
                )
            })

            addOnSandwichSlideAction(HalfCounterClockwiseRotateSlideAction(binding.bottomAppBarChevron))

            // Wrap binding.run to ensure ContentViewBindingDelegate is calling this Activity's
            // setContentView before accessing views
            binding.run {
                findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener(
                    this@MainActivity
                )
            }

        }

        // Set up the BottomAppBar menu
        binding.bottomAppBar.apply {
            setNavigationOnClickListener { bottomNavDrawer.toggle() }
            setOnMenuItemClickListener(this@MainActivity)
        }
        // Set up the BottomNavigationDrawer's open/close affordance
        binding.bottomAppBarContentContainer.apply {
            setOnClickListener { bottomNavDrawer.toggle() }
        }
    }

    override fun onDestinationChanged(
        controller: NavController, destination: NavDestination, arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.homeFragment -> {
                if (!hasLandedHome) {
                    setAccountAvatar()
                    hasLandedHome = true
                }
                setBottomAppBarVisible(0, getString(R.string.nav_home), hideFab = false)
            }
            R.id.faqTopicFragment, R.id.faqContentFragment -> setBottomAppBarVisible(
                1, getString(R.string.nav_faq)
            )
            R.id.exploreFragment -> setBottomAppBarVisible(
                2, getString(R.string.nav_explore), menu = getBottomAppBarMenu(destination)
            )
            R.id.chatFragment -> setBottomAppBarVisible(
                3, getString(R.string.nav_chat), R.drawable.ic_message, hideFab = false
            )
            R.id.profileFragment -> setBottomAppBarVisible(
                4, getString(R.string.nav_profile), hideFab = false
            )
            R.id.bookmarkFragment -> setBottomAppBarVisible(5, getString(R.string.nav_bookmark))
            R.id.createCommunityFragment -> setBottomAppBarVisible(
                -1, getString(R.string.nav_create_community)
            )
            R.id.communityFragment -> setBottomAppBarVisible(str = getString(R.string.nav_community))
            R.id.postFragment -> setBottomAppBarVisible(
                drawableFab = R.drawable.ic_reply,
                menu = getBottomAppBarMenu(destination),
                hideFab = false
            )
            R.id.communityListFragment -> setBottomAppBarVisible(str = getString(R.string.community_list))
            R.id.memberListFragment, R.id.memberFragment -> setBottomAppBarVisible(
                str = getString(R.string.member_list)
            )
            R.id.createChatFragment -> setBottomAppBarVisible(str = getString(R.string.new_chat))
            else -> setBottomAppBarForCompose()
        }
        showHideFabStateAction.dest = destination.id
    }

    @MenuRes
    private fun getBottomAppBarMenu(destination: NavDestination? = null): Int {
        val dest = destination ?: findNavController(R.id.nav_host_fragment).currentDestination
        return when (dest?.id) {
            R.id.exploreFragment -> R.menu.bottom_app_bar_explore_menu
//            R.id.postFragment -> R.menu.bottom_app_bar_post_menu
            else -> R.menu.empty_menu
        }
    }

    private fun setBottomAppBarVisible(
        idNavMenuItem: Int = -1,
        str: String = "",
        drawableFab: Int = R.drawable.asl_edit_reply,
        hideFab: Boolean = true,
        @MenuRes menu: Int = R.menu.empty_menu
    ) {
        binding.run {
            NavigationModel.setNavigationMenuItemChecked(idNavMenuItem)
            if (hideFab) {
                fab.hide()
            } else {
                setFabListener(VH_TYPE_HOME, null)
                fab.setImageState(intArrayOf(-android.R.attr.state_activated), true)
                fab.contentDescription = getString(R.string.fab_compose_email_content_description)
                fab.setImageResource(drawableFab)
                fab.show()
            }
            bottomAppBar.visibility = VISIBLE
            bottomAppBarTitle.text = str
            bottomAppBarTitle.visibility = VISIBLE
            bottomAppBar.replaceMenu(menu)
            bottomAppBar.performShow()
        }
    }

    private fun setBottomAppBarForCompose() {
        binding.run {
            bottomAppBar.performHide()
            fab.hide()
            bottomAppBar.visibility = GONE
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_settings -> {
                bottomNavDrawer.close()
                showDarkThemeMenu()
            }
            R.id.menu_logout -> {
                bottomNavDrawer.close()
                logout()
            }
            R.id.menu_search -> findNavController(R.id.nav_host_fragment).navigate(
                ExploreFragmentDirections.actionToSearch()
            )
        }
        return true
    }

    private fun logout() {
//        hasLandedHome = false
        viewModel.logout().observe(this, Observer {
            viewModel.setCurrentState(it.status)
            when (it.status) {
                SUCCESS -> {
                    findNavController(R.id.nav_host_fragment).navigate(LoginFragmentDirections.actionGlobalLogin())
                }
                ERROR -> viewModel.showSnackBar(it.message.toString())
                else -> Log.e("AccountViewModel", "failed logout: ${it.message.toString()}")
            }
        })
    }

    private fun showDarkThemeMenu() {
        MenuBottomSheetDialogFragment(R.menu.dark_theme_bottom_sheet_menu) {
            onDarkThemeMenuItemSelected(it.itemId)
        }.show(supportFragmentManager, null)
    }

    /**
     * Set this Activity's night mode based on a user's in-app selection.
     */
    private fun onDarkThemeMenuItemSelected(itemId: Int): Boolean {
        val nightMode = when (itemId) {
            R.id.menu_light -> AppCompatDelegate.MODE_NIGHT_NO
            R.id.menu_dark -> AppCompatDelegate.MODE_NIGHT_YES
            R.id.menu_battery_saver -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            R.id.menu_system_default -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            else -> return false
        }

        delegate.localNightMode = nightMode
        findNavController(R.id.nav_host_fragment).apply {
            currentDestination?.id?.let { popBackStack(it, true) }
            navigate(R.id.homeFragment)
        }
        shared.edit {
            putInt("MODE_DARK", nightMode)
            apply()
        }
        return true
    }

    private var doubleBackToExitPressedOnce = false

    @SuppressLint("RestrictedApi")
    override fun onBackPressed() {
        val isFirstDest = findNavController(R.id.nav_host_fragment).backStack.size < 3
        if (bottomNavDrawer.behavior.state != BottomSheetBehavior.STATE_HIDDEN) {
            bottomNavDrawer.close()
            return
        }
        if (isFirstDest) {
            if (doubleBackToExitPressedOnce && isFirstDest) {
                super.onBackPressed()
                return
            }
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
            return
        }
        findNavController(R.id.nav_host_fragment).navigateUp()
    }

    private fun requestPermissionsIfNecessary() {
        if (!checkAllPermissions(this)) {
            if (permissionRequestCount < maxNumberRequestPermissions) {
                permissionRequestCount += 1
                ActivityCompat.requestPermissions(
                    this, permissions.toTypedArray(), requestCodePermission
                )
            }
        }
    }

    fun setFabListener(fromFragment: Int, pst: Post?) {
        binding.fab.setOnClickListener {
            val action = when (fromFragment) {
                FROM_FRAGMENT_COMMENT -> AppNavGraphDirections.actionGlobalCompose(MediaList())
                else -> AppNavGraphDirections.actionGlobalCompose(MediaList())
            }
            findNavController(R.id.nav_host_fragment).navigate(action)
        }
    }


    fun setAccountAvatar() {
        bottomNavDrawer.setUserAvatar()
    }

    private fun showSnackBar() {
        viewModel.messageSnackBar.observe(this, Observer { message ->
            if (message.isNotBlank()) {
                val snackBar =
                    Snackbar.make(binding.coordinatorLayout, message, Snackbar.LENGTH_SHORT).apply {
                        if (binding.fab.visibility == VISIBLE) {
                            this.anchorView = binding.fab
                        }
                    }
                snackBar.show()
                val snackBarView = snackBar.view
                when (val snackBarLayoutParams = snackBarView.layoutParams) {
                    is CoordinatorLayout.LayoutParams -> {
                        when (val coordinatorLayoutBehavior = snackBarLayoutParams.behavior) {
                            is SwipeDismissBehavior -> {
                                coordinatorLayoutBehavior.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_END_TO_START)
                                snackBarLayoutParams.behavior = coordinatorLayoutBehavior
                            }
                        }
                    }
                }
            }
        })
    }
}
