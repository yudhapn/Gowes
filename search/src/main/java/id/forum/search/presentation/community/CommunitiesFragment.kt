package id.forum.search.presentation.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import id.forum.core.account.presentation.UserAccountViewModel
import id.forum.core.community.domain.model.Community
import id.forum.core.community.presentation.CommunityAdapter
import id.forum.core.community.presentation.CommunityAdapter.CommunityAdapterListener
import id.forum.core.util.VH_TYPE_EXPLORE
import id.forum.explore.presentation.ExploreFragmentDirections
import id.forum.search.databinding.FragmentCommunityListBinding
import id.forum.search.presentation.SearchViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

// This fragment can called from homefragment so it's not only as child fragment
@ExperimentalCoroutinesApi
class CommunitiesFragment() : Fragment(),
    CommunityAdapterListener {
    private lateinit var binding: FragmentCommunityListBinding
    private val viewModel: SearchViewModel by viewModel()
    private val accountViewModel: UserAccountViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommunityListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = this@CommunitiesFragment
            searchViewModel = viewModel
            communityRecyclerView.apply {
                adapter = CommunityAdapter(
                    this@CommunitiesFragment,
                    VH_TYPE_EXPLORE
                )
            }
            accountViewModel.keyword.observe(viewLifecycleOwner, Observer { keyword ->
                if (keyword.isNotBlank()) {
                    viewModel.searchCommunities(keyword)
                }
            })

            viewModel.communities.observe(viewLifecycleOwner, Observer {
                accountViewModel.setCurrentState(it.status)
            })
        }
    }

    override fun onCommunityClicked(cardView: View, community: Community) {
        val action = ExploreFragmentDirections.actionToCommunity(community)
        findNavController().navigate(action)
    }
}
