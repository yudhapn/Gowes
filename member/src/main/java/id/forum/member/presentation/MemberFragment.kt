package id.forum.member.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import id.forum.community.presentation.CommunityFragmentArgs
import id.forum.member.databinding.FragmentMemberBinding
import id.forum.member.injectFeature

class MemberFragment : Fragment() {
    private lateinit var binding: FragmentMemberBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectFeature()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: CommunityFragmentArgs by navArgs()
        binding.apply {
            viewPager.adapter =
                MemberViewPagerAdapter(
                    childFragmentManager,
                    requireContext(),
                    args.community
                )
            tabLayout.setupWithViewPager(viewPager)
        }
    }

}
