package id.forum.member.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import id.forum.core.account.presentation.UserAccountViewModel
import id.forum.core.community.domain.model.Community
import id.forum.core.user.domain.model.User
import id.forum.core.util.FRAGMENT_TYPE_MEMBER
import id.forum.core.util.FRAGMENT_TYPE_REQUEST
import id.forum.member.R
import id.forum.member.databinding.FragmentMemberListBinding
import id.forum.member.injectFeature
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import id.forum.core.R as coreRes

@ExperimentalCoroutinesApi
class MemberListFragment(
    private val fragTypeArgs: Int = FRAGMENT_TYPE_MEMBER,
    private var community: Community = Community(),
    private val _childFragment: Boolean = false
) : Fragment(),
    MemberAdapter.MemberAdapterListener {
    private lateinit var binding: FragmentMemberListBinding
    private val memberAdapter =
        MemberAdapter(this@MemberListFragment)
    private val memberViewModel: MemberViewModel by viewModel()
    private val accountViewModel: UserAccountViewModel by sharedViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!_childFragment) {
            injectFeature()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMemberListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: MemberListFragmentArgs by navArgs()
        community = if (_childFragment) community else args.community
        memberViewModel.setCommunity(community)
        binding.apply {
            childFragment = _childFragment
            fragType = fragTypeArgs
            this.viewModel = memberViewModel
            recyclerView.adapter = memberAdapter
            memberViewModel.community.observe(viewLifecycleOwner, Observer {
                accountViewModel.setCurrentState(it.status)
                when (fragTypeArgs) {
                    FRAGMENT_TYPE_MEMBER -> {
                        memberAdapter.addHeaderAndSubmitList(
                            it.data?.admins, it.data?.members, null
                        )
                    }
                    FRAGMENT_TYPE_REQUEST -> memberAdapter.addHeaderAndSubmitList(
                        null, null, it.data?.memberRequest
                    )
                }
            })
        }
    }

    private fun showMenu(anchor: View, user: User) {
        val popup = PopupMenu(requireContext(), anchor)
        val menu = if (fragTypeArgs == FRAGMENT_TYPE_MEMBER) {
            R.menu.member_menu
        } else {
            R.menu.request_member_menu
        }
        popup.menuInflater.inflate(menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            showDialog(item.itemId, user)
            true
        }
        popup.show()
    }

    private fun showDialog(itemId: Int, user: User) {
        val message = when (itemId) {
            R.id.menu_accept -> getString(coreRes.string.accept_member)
            R.id.menu_reject -> getString(coreRes.string.reject_member)
            R.id.menu_appoint -> getString(coreRes.string.appoint_admin_member)
            R.id.menu_expel -> getString(coreRes.string.expel_member)
            else -> getString(coreRes.string.accept_member)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setMessage(message)
            .setNegativeButton(resources.getString(coreRes.string.cancel_button)) { _, _ ->
            }
            .setPositiveButton(resources.getString(coreRes.string.ok_button)) { _, _ ->
                when (itemId) {
                    R.id.menu_accept -> memberViewModel.acceptMember(community, user)
                    R.id.menu_reject -> memberViewModel.rejectMember(community, user)
                    R.id.menu_appoint -> memberViewModel.appointAsAdmin(community, user)
                    R.id.menu_expel -> memberViewModel.expelMember(community, user)
                    else -> Unit
                }
            }
            .show()
    }


    override fun onMemberClicked(user: User) {
        val directions = if (_childFragment) {
            MemberFragmentDirections.actionToProfile(user)
        } else {
            MemberListFragmentDirections.actionToProfile(user)
        }
        findNavController().navigate(directions)
    }

    override fun onMenuClicked(anchor: View, user: User) = showMenu(anchor, user)
}
