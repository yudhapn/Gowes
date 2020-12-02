package id.forum.explore.presentation.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import id.forum.core.community.domain.model.Community
import id.forum.core.community.presentation.CommunityAdapter
import id.forum.core.community.presentation.CommunityAdapter.CommunityAdapterListener
import id.forum.core.util.VH_TYPE_EXPLORE
import id.forum.explore.databinding.FragmentCommunitiesBinding
import id.forum.explore.injectFeature
import id.forum.explore.presentation.ExploreFragmentDirections
import id.forum.explore.presentation.ExploreViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

// This fragment can called from homefragment so it's not only as child fragment
@ExperimentalCoroutinesApi
class CommunityListFragment(private val _childFragment: Boolean = false) : Fragment(),
    CommunityAdapterListener {
    private lateinit var binding: FragmentCommunitiesBinding
    private val exploreViewModel: ExploreViewModel by viewModel { parametersOf(1) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!_childFragment) {
            injectFeature()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommunitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = this@CommunityListFragment
            childFragment = _childFragment
            viewModel = exploreViewModel
            communityRecyclerView.apply {
                adapter = CommunityAdapter(
                    this@CommunityListFragment,
                    VH_TYPE_EXPLORE
                )
            }
            refreshLayout.setOnRefreshListener { exploreViewModel.refreshCommunityData() }
        }
    }

    override fun onCommunityClicked(cardView: View, community: Community) {
        val action = if (_childFragment) {
            ExploreFragmentDirections.actionToCommunity(community)
        } else {
            CommunityListFragmentDirections.actionToCommunity(community)
        }
        findNavController().navigate(action)
    }

}
